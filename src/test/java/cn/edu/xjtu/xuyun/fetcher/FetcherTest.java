package cn.edu.xjtu.xuyun.fetcher;

public class FetcherTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Fetcher test");
		PageFetcher aa = new PageFetcher(new Config());
		aa.fetchPage("https://www.baidu.com");
		aa.shutDown();
	}
}
