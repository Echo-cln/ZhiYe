package com.echo.zhiye.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsSource {
    public final String id, name, region, description, website, feedUrl, topic, type;
    public NewsSource(String id,String name,String region,String description,String website,String feedUrl,String topic,String type) {
        this.id=id; this.name=name; this.region=region; this.description=description;
        this.website=website; this.feedUrl=feedUrl; this.topic=topic; this.type=type;
    }
    public boolean hasFeed() { return feedUrl!=null && !feedUrl.isEmpty(); }
    public static List<NewsSource> all() {
        List<NewsSource> s=new ArrayList<>();
        s.add(new NewsSource("bbc-world","BBC News","国际媒体","世界新闻","https://www.bbc.com/news","https://feeds.bbci.co.uk/news/world/rss.xml","政治","官方公开 RSS"));
        s.add(new NewsSource("bbc-tech","BBC News","国际媒体","科技与科学","https://www.bbc.com/news/technology","https://feeds.bbci.co.uk/news/technology/rss.xml","科技","官方公开 RSS"));
        s.add(new NewsSource("bbc-business","BBC News","国际媒体","商业与经济","https://www.bbc.com/news/business","https://feeds.bbci.co.uk/news/business/rss.xml","经济","官方公开 RSS"));
        s.add(new NewsSource("bbc-politics","BBC News","国际媒体","政治与公共事务","https://www.bbc.com/news/politics","https://feeds.bbci.co.uk/news/politics/rss.xml","政治","官方公开 RSS"));
        s.add(new NewsSource("bbc-arts","BBC News","国际媒体","娱乐与艺术","https://www.bbc.com/news/entertainment_and_arts","https://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml","文化","官方公开 RSS"));
        s.add(new NewsSource("guardian-world","The Guardian","国际媒体","世界新闻与深度报道","https://www.theguardian.com/world","https://www.theguardian.com/world/rss","政治","官方公开 RSS"));
        s.add(new NewsSource("guardian-tech","The Guardian","国际媒体","科技与数字社会","https://www.theguardian.com/technology","https://www.theguardian.com/technology/rss","科技","官方公开 RSS"));
        s.add(new NewsSource("guardian-business","The Guardian","国际媒体","商业与经济","https://www.theguardian.com/business","https://www.theguardian.com/business/rss","经济","官方公开 RSS"));
        s.add(new NewsSource("guardian-culture","The Guardian","国际媒体","文化与艺术","https://www.theguardian.com/culture","https://www.theguardian.com/culture/rss","文化","官方公开 RSS"));
        s.add(new NewsSource("cns-general","中国新闻网","国内媒体","即时新闻","https://www.chinanews.com/rss/","https://www.chinanews.com.cn/rss/scroll-news.xml","社会观察","官方公开 RSS"));
        s.add(new NewsSource("cns-world","中国新闻网","国内媒体","国际新闻","https://www.chinanews.com/rss/","https://www.chinanews.com.cn/rss/world.xml","政治","官方公开 RSS"));
        s.add(new NewsSource("cns-finance","中国新闻网","国内媒体","财经新闻","https://www.chinanews.com/rss/","https://www.chinanews.com.cn/rss/finance.xml","经济","官方公开 RSS"));
        s.add(new NewsSource("cns-society","中国新闻网","国内媒体","社会新闻","https://www.chinanews.com/rss/","https://www.chinanews.com.cn/rss/society.xml","社会观察","官方公开 RSS"));
        s.add(new NewsSource("cns-culture","中国新闻网","国内媒体","文娱新闻","https://www.chinanews.com/rss/","https://www.chinanews.com.cn/rss/culture.xml","文化","官方公开 RSS"));
        s.add(new NewsSource("reuters-aggregate","Reuters 路透社","国际媒体","Google News 聚合 Reuters 域名报道；非 Reuters 官方订阅","https://www.reuters.com/","https://news.google.com/rss/search?q=site%3Areuters.com&hl=en-US&gl=US&ceid=US%3Aen","国际","第三方聚合"));
        s.add(new NewsSource("xinhua","新华社","国内媒体","官方网页入口，未配置未经核验的抓取接口","https://www.news.cn/","","全部","官网入口"));
        s.add(new NewsSource("people","人民日报","国内媒体","官方网页入口，未配置未经核验的抓取接口","https://paper.people.com.cn/","","全部","官网入口"));
        s.add(new NewsSource("caixin","财新","财经媒体","财经深度报道官方入口","https://www.caixin.com/","","经济","官网入口"));
        s.add(new NewsSource("ft","Financial Times","国际媒体","全球商业与经济报道官方入口","https://www.ft.com/","","经济","官网入口"));
        return Collections.unmodifiableList(s);
    }
}
