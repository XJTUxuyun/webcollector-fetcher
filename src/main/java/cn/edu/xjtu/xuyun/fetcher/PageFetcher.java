package cn.edu.xjtu.xuyun.fetcher;

import java.security.cert.X509Certificate;
import java.util.Date;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

/**
 * 下载器
 * @author xuyun
 * @version 1.0
 * @since 20180315
 */
public class PageFetcher
{

	protected PoolingHttpClientConnectionManager connectionManager;
	protected CloseableHttpClient httpClient;
	protected final Object mutex = new Object();
	protected long lastFetchTime = 0;
	protected IdleConnectionMonitorThread connectionMonitorThread = null;
	protected Config config = null;


	public PageFetcher(Config config)
	{
		this.config = config;
		RequestConfig requestConfig = RequestConfig.custom().setExpectContinueEnabled(false)
				.setCookieSpec(CookieSpecs.IGNORE_COOKIES).setRedirectsEnabled(false)
				.setSocketTimeout(config.getSocketTimeout()).setConnectTimeout(config.getConnectionTimeout()).build();

		RegistryBuilder<ConnectionSocketFactory> connRegistryBuilder = RegistryBuilder.create();
		connRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
		if (config.isIncludeHttpsPages())
		{
			try
			{
				SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy()
				{
					public boolean isTrusted(final X509Certificate[] chain, String authType)
					{
						return true;
					}
				}).build();
				@SuppressWarnings("deprecation")
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
						SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				connRegistryBuilder.register("https", sslsf);
			} catch (Exception e)
			{
				// logger.warn("Exception thrown while trying to register https");
				// logger.debug("Stacktrace", e);
				System.out.println(e);
			}
		}
		Registry<ConnectionSocketFactory> connRegistry = connRegistryBuilder.build();
		connectionManager = new PoolingHttpClientConnectionManager(connRegistry);
		connectionManager.setMaxTotal(config.getMaxTotalConnections());
		connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		clientBuilder.setDefaultRequestConfig(requestConfig);
		clientBuilder.setConnectionManager(connectionManager);
		clientBuilder.setUserAgent(config.getUserAgentString());
		clientBuilder.setDefaultHeaders(config.getDefaultHeaders());
		if (config.getProxyHost() != null)
		{
			if (config.getProxyUsername() != null)
			{
				BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(new AuthScope(config.getProxyHost(), config.getProxyPort()),
						new UsernamePasswordCredentials(config.getProxyUsername(), config.getProxyPassword()));
				clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
			}
			HttpHost proxy = new HttpHost(config.getProxyHost(), config.getProxyPort());
			clientBuilder.setProxy(proxy);
		}
		httpClient = clientBuilder.build();
		if (connectionMonitorThread == null)
		{
			connectionMonitorThread = new IdleConnectionMonitorThread(connectionManager);
		}
		connectionMonitorThread.start();
	}

	public synchronized void shutDown()
	{
		if (connectionMonitorThread != null)
		{
			connectionManager.shutdown();
			connectionMonitorThread.shutdown();
		}
	}

	protected HttpUriRequest newHttpUriRequest(String url)
	{
		return new HttpGet(url);
	}
	
	
	
	protected void politenessDelay() throws InterruptedException 
	{
		synchronized (mutex)
		{
			long now = (new Date()).getTime();
			if ((now - lastFetchTime) < config.getPolitenessDelay())
			{
				Thread.sleep(config.getPolitenessDelay() - (now - lastFetchTime));
			}
			lastFetchTime = (new Date()).getTime();
		}
	}

	public PageFetchResult fetchPage(String url) throws Exception
	{
		// Getting URL, setting headers & content
		PageFetchResult fetchResult = new PageFetchResult();
		String toFetchURL = url;
		HttpUriRequest request = null;
		try
		{
			request = newHttpUriRequest(toFetchURL);
			// Applying Politeness delay
			politenessDelay();

			request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			request.addHeader("Accept-Encoding", "gzip, deflate, sdch");
			request.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
			request.addHeader("Connection", "keep-alive");
			request.addHeader("Host", "wenshu.court.gov.cn");
			request.addHeader("Upgrade-Insecure-Requests", "1");
			CloseableHttpResponse response = httpClient.execute(request);
			fetchResult.setEntity(response.getEntity());
			fetchResult.setResponseHeaders(response.getAllHeaders());

			int statusCode = response.getStatusLine().getStatusCode();

			fetchResult.setStatusCode(statusCode);
			return fetchResult;

		} finally
		{ 
			// occurs also with thrown exceptions
			System.out.println(url + " status code:" + fetchResult.getStatusCode());
			if ((fetchResult.getEntity() == null) && (request != null))
			{
				request.abort();
			}
		}
	}

}
