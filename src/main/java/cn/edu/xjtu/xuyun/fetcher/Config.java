package cn.edu.xjtu.xuyun.fetcher;

import java.util.Collection;
import java.util.HashSet;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * PageFetcher配置
 * @author xuyun
 * @version 1.0
 * @since 20180315
 */
public class Config
{
	/**
	 * Socket timeout in milliseconds
	 */
	private int socketTimeout = 3000;

	/**
	 * Connection timeout in milliseconds
	 */
	private int connectionTimeout = 5000;

	/**
	 * Should we also crawl https pages?
	 */
	private boolean includeHttpsPages = true;

	/**
	 * Maximum total connections
	 */
	private int maxTotalConnections = 50;

	/**
	 * Maximum Connections per host
	 */
	private int maxConnectionsPerHost = 15;

	/**
	 * user-agent string that is used for representing your crawler to web servers.
	 * See http://en.wikipedia.org/wiki/User_agent for more details
	 */
	private String userAgentString = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.75 Safari/537.36";

	/**
	 * Default request header values.
	 */
	private Collection<BasicHeader> defaultHeaders = new HashSet<BasicHeader>();

	/**
	 * If crawler should run behind a proxy, this parameter can be used for
	 * specifying the proxy host.
	 */
	private String proxyHost = null;

	/**
	 * If crawler should run behind a proxy, this parameter can be used for
	 * specifying the proxy port.
	 */
	private int proxyPort = 80;

	/**
	 * If crawler should run behind a proxy and user/pass is needed for
	 * authentication in proxy, this parameter can be used for specifying the
	 * username.
	 */
	private String proxyUsername = null;

	/**
	 * If crawler should run behind a proxy and user/pass is needed for
	 * authentication in proxy, this parameter can be used for specifying the
	 * password.
	 */
	private String proxyPassword = null;

	/**
	 * Politeness delay in milliseconds (delay between sending two requests to the
	 * same host).
	 */
	private int politenessDelay = 500;

	public void setSocketTimeout(int socketTimeout)
	{
		this.socketTimeout = socketTimeout;
	}

	public int getSocketTimeout()
	{
		return this.socketTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout)
	{
		this.connectionTimeout = connectionTimeout;
	}

	public int getConnectionTimeout()
	{
		return this.connectionTimeout;
	}

	public boolean isIncludeHttpsPages()
	{
		return includeHttpsPages;
	}

	public void setIncludeHttpsPages(boolean includeHttpsPages)
	{
		this.includeHttpsPages = includeHttpsPages;
	}

	public int getMaxTotalConnections()
	{
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(int maxTotalConnections)
	{
		this.maxTotalConnections = maxTotalConnections;
	}

	public int getMaxConnectionsPerHost()
	{
		return maxConnectionsPerHost;
	}

	public void setMaxConnectionsPerHost(int maxConnectionsPerHost)
	{
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}

	public String getUserAgentString()
	{
		return userAgentString;
	}

	public void setUserAgentString(String userAgentString)
	{
		this.userAgentString = userAgentString;
	}

	public Collection<BasicHeader> getDefaultHeaders()
	{
		return new HashSet<BasicHeader>(defaultHeaders);
	}

	public void setDefaultHeaders(Collection<? extends Header> defaultHeaders)
	{
		Collection<BasicHeader> copiedHeaders = new HashSet<BasicHeader>();
		for (Header header : defaultHeaders)
		{
			copiedHeaders.add(new BasicHeader(header.getName(), header.getValue()));
		}
		this.defaultHeaders = copiedHeaders;
	}

	public String getProxyHost()
	{
		return proxyHost;
	}

	public void setProxyHost(String proxyHost)
	{
		this.proxyHost = proxyHost;
	}

	public String getProxyUsername()
	{
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername)
	{
		this.proxyUsername = proxyUsername;
	}

	public int getProxyPort()
	{
		return proxyPort;
	}

	public void setProxyPort(int proxyPort)
	{
		this.proxyPort = proxyPort;
	}

	public String getProxyPassword()
	{
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword)
	{
		this.proxyPassword = proxyPassword;
	}

	public int getPolitenessDelay()
	{
		return politenessDelay;
	}

	public void setPolitenessDelay(int politenessDelay)
	{
		this.politenessDelay = politenessDelay;
	}
}
