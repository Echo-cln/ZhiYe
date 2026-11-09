package com.echo.zhiye.data;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RssParser {
    private static final Pattern IMG=Pattern.compile("<img[^>]+src=[\\\"']([^\\\"']+)[\\\"']",Pattern.CASE_INSENSITIVE);
    public List<NewsEntity> fetch(NewsSource source) throws Exception {
        HttpURLConnection c=(HttpURLConnection)new URL(source.feedUrl).openConnection();
        c.setConnectTimeout(10000); c.setReadTimeout(15000);
        c.setRequestProperty("User-Agent","ZhiYeNewsReader/0.1");
        c.setRequestProperty("Accept","application/rss+xml, application/atom+xml, application/xml, text/xml");
        try(InputStream in=c.getInputStream()) { return parse(in,source); }
        finally { c.disconnect(); }
    }
    private List<NewsEntity> parse(InputStream in,NewsSource source) throws Exception {
        List<NewsEntity> out=new ArrayList<>(); XmlPullParser p=Xml.newPullParser();
        p.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,true); p.setInput(in,StandardCharsets.UTF_8.name());
        Item item=null; int event=p.getEventType();
        while(event!=XmlPullParser.END_DOCUMENT) {
            if(event==XmlPullParser.START_TAG) {
                String name=p.getName().toLowerCase(Locale.ROOT);
                if(name.equals("item")||name.equals("entry")) item=new Item();
                else if(item!=null) {
                    if(name.equals("title")) item.title=clean(p.nextText());
                    else if(name.equals("description")||name.equals("summary")||name.equals("subtitle")) item.summary=clean(p.nextText());
                    else if(name.equals("encoded")||name.equals("content")) {
                        String url=p.getAttributeValue(null,"url");
                        if(url==null) url=p.getAttributeValue(null,"href");
                        String type=p.getAttributeValue(null,"type");
                        if(url!=null && ((type!=null && type.startsWith("image")) || url.matches("(?i).*\\.(jpg|jpeg|png|webp)(\\?.*)?$"))) item.image=url;
                        else skipElement(p);
                    }
                    else if(name.equals("link")) {
                        String href=p.getAttributeValue(null,"href"), rel=p.getAttributeValue(null,"rel");
                        if(href!=null && (rel==null||"alternate".equals(rel))) item.link=href;
                        else { String value=p.nextText(); if(item.link==null&&value!=null&&value.startsWith("http")) item.link=value.trim(); }
                    }
                    else if(name.equals("pubdate")||name.equals("published")||name.equals("updated")||name.equals("date")) item.date=p.nextText();
                    else if(name.equals("author")||name.equals("creator")) item.author=clean(p.nextText());
                    else if(name.equals("thumbnail")||name.equals("enclosure")) {
                        String url=p.getAttributeValue(null,"url"), type=p.getAttributeValue(null,"type");
                        if(url!=null&&(type==null||type.startsWith("image")||name.equals("thumbnail"))) item.image=url;
                    }
                }
            } else if(event==XmlPullParser.END_TAG) {
                String name=p.getName().toLowerCase(Locale.ROOT);
                if((name.equals("item")||name.equals("entry"))&&item!=null) {
                    if(item.image==null) item.image=extractImage(item.summary);
                    if(item.title!=null&&!item.title.isEmpty()&&item.link!=null) {
                        String summary=item.summary==null?"":item.summary;
                        // Keep only the publisher-provided summary. Full article content is never copied into Room.
                        out.add(new NewsEntity(sha256(item.link),item.title,summary,summary,item.image,source.name,source.id,
                                inferTopic(source.topic,item.title+" "+summary),item.link,item.author,parseDate(item.date),
                                false,0,0,false,System.currentTimeMillis()));
                    }
                    item=null;
                }
            }
            event=p.next();
        }
        return out;
    }
    private void skipElement(XmlPullParser p) throws Exception {
        int depth=1;
        while(depth>0) { int e=p.next(); if(e==XmlPullParser.START_TAG) depth++; else if(e==XmlPullParser.END_TAG) depth--; }
    }
    private String clean(String s) {
        if(s==null)return "";
        return s.replaceAll("(?is)<script.*?</script>"," ").replaceAll("(?is)<style.*?</style>"," ")
                .replaceAll("<[^>]+>"," ").replace("&nbsp;"," ").replace("&amp;","&")
                .replace("&quot;","\"").replace("&#39;","'").replaceAll("\\s+"," ").trim();
    }
    private String extractImage(String s) { if(s==null)return null; Matcher m=IMG.matcher(s); return m.find()?m.group(1):null; }
    private long parseDate(String raw) {
        if(raw==null||raw.trim().isEmpty())return System.currentTimeMillis();
        String[] patterns={"EEE, dd MMM yyyy HH:mm:ss Z","EEE, dd MMM yyyy HH:mm Z","yyyy-MM-dd'T'HH:mm:ss'Z'","yyyy-MM-dd'T'HH:mm:ssXXX","yyyy-MM-dd'T'HH:mm:ss.SSSXXX"};
        for(String pattern:patterns)try { SimpleDateFormat f=new SimpleDateFormat(pattern,Locale.US); f.setTimeZone(TimeZone.getTimeZone("UTC")); Date d=f.parse(raw.trim()); if(d!=null)return d.getTime(); } catch(Exception ignored) {}
        return System.currentTimeMillis();
    }
    private String inferTopic(String fallback,String text) {
        String t=text.toLowerCase(Locale.ROOT);
        if(has(t,"technology","artificial intelligence","ai ","chip","科技","人工智能","芯片"))return "科技";
        if(has(t,"business","economy","market","finance","经济","财经","金融","股市"))return "经济";
        if(has(t,"politic","election","government","外交","政治","选举","议会"))return "政治";
        if(has(t,"culture","art","book","文化","艺术","文学"))return "文化";
        if(has(t,"entertainment","film","movie","music","娱乐","电影","音乐","明星"))return "娱乐";
        if(has(t,"society","education","community","社会","教育","民生"))return "社会观察";
        return fallback==null||fallback.equals("国际")||fallback.equals("全部")?"社会观察":fallback;
    }
    private boolean has(String text,String... keys) { for(String k:keys)if(text.contains(k))return true; return false; }
    private String sha256(String s)throws Exception {
        byte[] a=MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
        StringBuilder b=new StringBuilder(); for(byte x:a)b.append(String.format(Locale.US,"%02x",x)); return b.toString();
    }
    private static class Item { String title,summary,image,link,date,author; }
}
