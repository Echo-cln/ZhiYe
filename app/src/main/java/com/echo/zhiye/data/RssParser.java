package com.echo.zhiye.data;

import android.text.Html;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
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
    private static final Pattern IMG = Pattern.compile(
            "<img[^>]+src=[\\\"']([^\\\"']+)[\\\"']", Pattern.CASE_INSENSITIVE);

    public List<NewsEntity> fetch(NewsSource source) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(source.feedUrl).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", "ZhiYeNewsReader/0.3 (Android)");
        connection.setRequestProperty("Accept",
                "application/rss+xml, application/atom+xml, application/xml, text/xml");
        try {
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new IOException("Feed returned HTTP " + status);
            }
            try (InputStream input = connection.getInputStream()) {
                return parse(input, source);
            }
        } finally {
            connection.disconnect();
        }
    }

    List<NewsEntity> parse(InputStream input, NewsSource source) throws Exception {
        List<NewsEntity> articles = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(input, null);

        Item item = null;
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                String name = parser.getName().toLowerCase(Locale.ROOT);
                if ("item".equals(name) || "entry".equals(name)) {
                    item = new Item();
                } else if (item != null) {
                    if ("title".equals(name)) {
                        item.title = clean(readTextSafely(parser));
                    } else if ("description".equals(name) || "summary".equals(name)
                            || "subtitle".equals(name)) {
                        item.rawSummary = readTextSafely(parser);
                        item.summary = clean(item.rawSummary);
                    } else if ("encoded".equals(name) || "content".equals(name)) {
                        String mediaUrl = attribute(parser, "url");
                        String mediaType = attribute(parser, "type");
                        if (isImageMedia(name, mediaUrl, mediaType)) {
                            item.image = mediaUrl;
                        } else {
                            String html = readTextSafely(parser);
                            if (item.image == null) item.image = extractImage(html);
                        }
                    } else if ("link".equals(name)) {
                        String href = attribute(parser, "href");
                        String rel = attribute(parser, "rel");
                        if (href != null && (rel == null || "alternate".equalsIgnoreCase(rel))) {
                            item.link = href.trim();
                        } else {
                            String value = readTextSafely(parser);
                            if (item.link == null && value != null && value.trim().startsWith("http")) {
                                item.link = value.trim();
                            }
                        }
                    } else if ("guid".equals(name) || "id".equals(name)) {
                        String value = readTextSafely(parser);
                        if (item.link == null && value != null && value.trim().startsWith("http")) {
                            item.link = value.trim();
                        }
                    } else if ("pubdate".equals(name) || "published".equals(name)
                            || "updated".equals(name) || "date".equals(name)) {
                        item.date = readTextSafely(parser);
                    } else if ("author".equals(name) || "creator".equals(name)) {
                        String author = readAuthor(parser);
                        if (!author.isEmpty()) item.author = author;
                    } else if ("name".equals(name) && (item.author == null || item.author.isEmpty())) {
                        item.author = clean(readTextSafely(parser));
                    } else if ("thumbnail".equals(name) || "enclosure".equals(name)) {
                        String imageUrl = attribute(parser, "url");
                        String mediaType = attribute(parser, "type");
                        if (isImageMedia(name, imageUrl, mediaType)) item.image = imageUrl;
                    }
                }
            } else if (event == XmlPullParser.END_TAG) {
                String name = parser.getName().toLowerCase(Locale.ROOT);
                if (("item".equals(name) || "entry".equals(name)) && item != null) {
                    if (item.image == null) item.image = extractImage(item.rawSummary);
                    if (item.title != null && !item.title.isEmpty()
                            && item.link != null && !item.link.isEmpty()) {
                        String summary = item.summary == null ? "" : item.summary;
                        articles.add(new NewsEntity(
                                sha256(item.link), item.title, summary, summary, item.image,
                                source.name, source.id, inferTopic(source.topic, item.title + " " + summary),
                                item.link, item.author, parseDate(item.date), false, 0, 0,
                                false, System.currentTimeMillis()));
                    }
                    item = null;
                }
            }
            event = parser.next();
        }
        return articles;
    }

    private boolean isImageMedia(String elementName, String url, String type) {
        if (url == null || url.trim().isEmpty()) return false;
        if ("thumbnail".equals(elementName)) return true;
        if (type != null && !type.trim().isEmpty()) {
            return type.toLowerCase(Locale.ROOT).startsWith("image/");
        }
        return url.matches("(?i).*\\.(jpg|jpeg|png|gif|webp|avif)(\\?.*)?$");
    }

    private String readAuthor(XmlPullParser parser) {
        StringBuilder author = new StringBuilder();
        int depth = 1;
        try {
            while (depth > 0) {
                int event = parser.next();
                if (event == XmlPullParser.START_TAG) {
                    depth++;
                    if ("name".equalsIgnoreCase(parser.getName())) {
                        String name = parser.nextText();
                        if (name != null && !name.trim().isEmpty()) author.append(name.trim());
                        depth--;
                    }
                } else if (event == XmlPullParser.TEXT && depth == 1) {
                    author.append(parser.getText());
                } else if (event == XmlPullParser.END_TAG) {
                    depth--;
                } else if (event == XmlPullParser.END_DOCUMENT) {
                    break;
                }
            }
        } catch (Exception ignored) {
            // An incomplete author element must not discard the rest of the feed.
        }
        return clean(author.toString());
    }

    private String attribute(XmlPullParser parser, String target) {
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            if (target.equalsIgnoreCase(parser.getAttributeName(i))) {
                return parser.getAttributeValue(i);
            }
        }
        return null;
    }

    private String readTextSafely(XmlPullParser parser) {
        try {
            return parser.nextText();
        } catch (Exception ignored) {
            return "";
        }
    }

    private String clean(String value) {
        if (value == null || value.isEmpty()) return "";
        String withoutExecutableContent = value
                .replaceAll("(?is)<script.*?</script>", " ")
                .replaceAll("(?is)<style.*?</style>", " ");
        String decoded = Html.fromHtml(withoutExecutableContent, Html.FROM_HTML_MODE_LEGACY).toString();
        return decoded.replace('\uFFFC', ' ')
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String extractImage(String html) {
        if (html == null) return null;
        Matcher matcher = IMG.matcher(html);
        return matcher.find() ? matcher.group(1) : null;
    }

    private long parseDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) return 0L;
        String[] patterns = {
                "EEE, dd MMM yyyy HH:mm:ss Z",
                "EEE, d MMM yyyy HH:mm:ss Z",
                "EEE, dd MMM yyyy HH:mm:ss z",
                "EEE, d MMM yyyy HH:mm:ss z",
                "EEE, dd MMM yyyy HH:mm Z",
                "EEE, d MMM yyyy HH:mm Z",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd"
        };
        for (String pattern : patterns) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                format.setLenient(false);
                Date date = format.parse(raw.trim());
                if (date != null) return date.getTime();
            } catch (Exception ignored) {
                // Try the next known RSS/Atom date representation.
            }
        }
        return 0L;
    }

    private String inferTopic(String fallback, String text) {
        String value = text.toLowerCase(Locale.ROOT);
        if (has(value, "technology", "artificial intelligence", "ai ", "chip", "科技", "人工智能", "芯片")) return "科技";
        if (has(value, "business", "economy", "market", "finance", "经济", "财经", "金融", "股市")) return "经济";
        if (has(value, "politic", "election", "government", "外交", "政治", "选举", "议会")) return "政治";
        if (has(value, "culture", "art", "book", "文化", "艺术", "文学")) return "文化";
        if (has(value, "entertainment", "film", "movie", "music", "娱乐", "电影", "音乐", "明星")) return "娱乐";
        if (has(value, "society", "education", "community", "社会", "教育", "民生")) return "社会观察";
        return fallback == null || fallback.equals("国际") || fallback.equals("全部") ? "社会观察" : fallback;
    }

    private boolean has(String text, String... keys) {
        for (String key : keys) if (text.contains(key)) return true;
        return false;
    }

    private String sha256(String value) throws Exception {
        byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte item : bytes) result.append(String.format(Locale.US, "%02x", item));
        return result.toString();
    }

    private static class Item {
        String title, summary, rawSummary, image, link, date, author;
    }
}
