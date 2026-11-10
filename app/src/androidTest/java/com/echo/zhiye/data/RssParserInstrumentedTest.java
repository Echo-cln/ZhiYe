package com.echo.zhiye.data;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RssParserInstrumentedTest {
    private NewsSource testSource() {
        return new NewsSource("test", "测试媒体", "国内媒体", "测试订阅",
                "https://example.org", "https://example.org/rss", "科技", "测试");
    }

    private List<NewsEntity> parse(String xml) throws Exception {
        return new RssParser().parse(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), testSource());
    }

    @Test
    public void parserKeepsPublisherSummaryAndExtractsDescriptionImage() throws Exception {
        String xml = "<?xml version=\"1.0\"?><rss version=\"2.0\"><channel><item>"
                + "<title>测试新闻标题</title><link>https://example.org/story/1</link>"
                + "<description><![CDATA[<p>这是一段媒体提供的摘要。</p><img src=\"https://example.org/cover.jpg\"/>]]></description>"
                + "<pubDate>Mon, 09 Nov 2026 08:00:00 +0000</pubDate></item></channel></rss>";

        List<NewsEntity> items = parse(xml);

        assertEquals(1, items.size());
        NewsEntity article = items.get(0);
        assertEquals("测试新闻标题", article.title);
        assertEquals("这是一段媒体提供的摘要。", article.summary);
        assertEquals("https://example.org/cover.jpg", article.imageUrl);
        assertTrue(article.publishedAt > 0);
        assertEquals(64, article.id.length());
    }

    @Test
    public void parserDoesNotInventPublicationTime() throws Exception {
        String xml = "<rss><channel><item><title>无时间新闻</title>"
                + "<link>https://example.org/no-date</link><description>摘要</description>"
                + "</item></channel></rss>";

        List<NewsEntity> items = parse(xml);

        assertEquals(1, items.size());
        assertEquals(0L, items.get(0).publishedAt);
    }

    @Test
    public void parserReadsAtomNestedAuthorAndAlternateLink() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<feed xmlns=\"http://www.w3.org/2005/Atom\" "
                + "xmlns:media=\"http://search.yahoo.com/mrss/\">"
                + "<entry><title>Atom 新闻</title><id>urn:example:story:2</id>"
                + "<link rel=\"self\" href=\"https://example.org/api/story/2\"/>"
                + "<link rel=\"alternate\" href=\"https://example.org/story/2\"/>"
                + "<updated>2026-11-10T04:30:00Z</updated>"
                + "<author><name>Jane Reporter</name><email>jane@example.org</email></author>"
                + "<summary type=\"html\">&lt;p&gt;Atom summary &amp;amp; context&lt;/p&gt;</summary>"
                + "<media:thumbnail url=\"https://example.org/atom-cover.webp\"/>"
                + "</entry></feed>";

        List<NewsEntity> items = parse(xml);

        assertEquals(1, items.size());
        NewsEntity article = items.get(0);
        assertEquals("https://example.org/story/2", article.url);
        assertEquals("Jane Reporter", article.author);
        assertEquals("Atom summary & context", article.summary);
        assertEquals("https://example.org/atom-cover.webp", article.imageUrl);
        assertTrue(article.publishedAt > 0);
    }

    @Test
    public void parserUsesHttpGuidWhenRssLinkIsMissingAndIgnoresAudioEnclosure() throws Exception {
        String xml = "<rss><channel><item><title>GUID 新闻</title>"
                + "<guid isPermaLink=\"true\">https://example.org/guid/3</guid>"
                + "<enclosure url=\"https://example.org/audio.mp3\" type=\"audio/mpeg\"/>"
                + "<description>仅有摘要</description></item></channel></rss>";

        List<NewsEntity> items = parse(xml);

        assertEquals(1, items.size());
        assertEquals("https://example.org/guid/3", items.get(0).url);
        assertNull(items.get(0).imageUrl);
    }
}
