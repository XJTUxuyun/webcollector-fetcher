package cn.edu.xjtu.xuyun.fetcher;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * PageFecher监控器，用于关闭那些超时或者其它情况的连接
 * @author xuyun
 * @version 1.0
 * @since 20180315
 */
public class IdleConnectionMonitorThread extends Thread
{

	private final PoolingHttpClientConnectionManager connMgr;
	private volatile boolean shutdown;

	public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connMgr)
	{
		super("Connection Manager");
		this.connMgr = connMgr;
	}

	@Override
	public void run()
	{
		try
		{
			while (!shutdown)
			{
				synchronized (this)
				{
					wait(5000);
					// Close expired connections
					connMgr.closeExpiredConnections();
					// Optionally, close connections that have been idle longer than 30 sec
					connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
				}
			}
		} catch (InterruptedException ignored)
		{
			// terminate
		}
	}

	public void shutdown()
	{
		shutdown = true;
		synchronized (this)
		{
			notifyAll();
		}
	}

}
