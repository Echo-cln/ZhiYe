package com.echo.zhiye.data;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class RssParserInstrumentedTest {
 @Test public void parserKeepsPublisherSummaryAndExtractsDescriptionImage()throws Exception{
  String xml="<?xml version=\"1.0\"?><rss version=\"2.0\"><channel><item>"
   +"<title>测试新闻标题</title><link>https://example.org/story/1</link>"
   +"<description><![CDATA[<p>这是一段媒体提供的摘要。</p><img src=\"https://example.org/cover.jpg\"/>]]></description>"
   +"<pubDate>Mon, 09 Nov 2026 08:00:00 +0000</pubDate></item></channel></rss>";
  NewsSource source=new NewsSource("test","测试媒体","国内媒体","测试订阅","https://example.org","https://example.org/rss","科技","测试");
  List<NewsEntity> items=new RssParser().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)),source);
  assertEquals(1,items.size());
  NewsEntity article=items.get(0);
  assertEquals("测试新闻标题",article.title);
  assertEquals("这是一段媒体提供的摘要。",article.summary);
  assertEquals("https://example.org/cover.jpg",article.imageUrl);
  assertTrue(article.publishedAt>0);
  assertEquals(64,article.id.length());
 }
 @Test public void parserDoesNotInventPublicationTime()throws Exception{
  String xml="<rss><channel><item><title>无时间新闻</title><link>https://example.org/no-date</link><description>摘要</description></item></channel></rss>";
  NewsSource source=new NewsSource("test","测试媒体","国内媒体","测试订阅","https://example.org","https://example.org/rss","科技","测试");
  List<NewsEntity> items=new RssParser().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)),source);
  assertEquals(1,items.size());
  assertEquals(0L,items.get(0).publishedAt);
 }
}
