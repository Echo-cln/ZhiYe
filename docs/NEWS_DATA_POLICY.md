# 知页新闻数据来源与版权策略

## 1. 数据流

`媒体公开 RSS / Atom` → `RssParser` → `NewsRepository` → `Room` → `RecyclerView / 详情`

- Android 客户端通过 HTTPS 请求公开 RSS / Atom 订阅地址。
- 解析标题、摘要、作者、发布时间、图片地址、原文 URL。
- 以原文 URL 的 SHA-256 作为稳定文章 ID，避免同一篇文章在多个栏目重复。
- 将文章元数据写入 Room，供搜索、收藏、阅读历史和离线列表使用。
- 用户点击“阅读原文”后，打开媒体原始页面。

## 2. 当前配置的来源

| 媒体 | 订阅策略 | 当前状态说明 |
|---|---|---|
| BBC News | 公开 RSS：World、Technology、Business、Politics、Entertainment & Arts | 公开订阅地址；按栏目拉取 |
| The Guardian | 公开 RSS：World、Technology、Business、Culture | 官方帮助页说明可为栏目页面追加 /rss |
| 中国新闻网 | 官方 RSS 目录：即时、国际、财经、社会、文娱等 | 以其公开 RSS 目录为准 |
| Reuters | Google News 的 site:reuters.com 搜索 RSS | 第三方聚合，不是 Reuters 官方 RSS |
| 新华社 | 官方网站入口 | 暂不配置未经核实的 RSS/API |
| 人民日报 | 官方网站入口 | 暂不配置未经核实的 RSS/API |
| 财新、Financial Times | 官方网站入口 | 需遵守订阅与访问权限 |

## 3. 订阅地址

### BBC
- World: https://feeds.bbci.co.uk/news/world/rss.xml
- Technology: https://feeds.bbci.co.uk/news/technology/rss.xml
- Business: https://feeds.bbci.co.uk/news/business/rss.xml
- Politics: https://feeds.bbci.co.uk/news/politics/rss.xml
- Entertainment & Arts: https://feeds.bbci.co.uk/news/entertainment_and_arts/rss.xml

### The Guardian
- World: https://www.theguardian.com/world/rss
- Technology: https://www.theguardian.com/technology/rss
- Business: https://www.theguardian.com/business/rss
- Culture: https://www.theguardian.com/culture/rss

### 中国新闻网
- RSS 目录: https://www.chinanews.com/rss/
- 即时新闻: https://www.chinanews.com.cn/rss/scroll-news.xml
- 国际新闻: https://www.chinanews.com.cn/rss/world.xml
- 财经新闻: https://www.chinanews.com.cn/rss/finance.xml
- 社会新闻: https://www.chinanews.com.cn/rss/society.xml
- 文娱新闻: https://www.chinanews.com.cn/rss/culture.xml

### Reuters 聚合
- Google News RSS: https://news.google.com/rss/search?q=site%3Areuters.com&hl=en-US&gl=US&ceid=US%3Aen

## 4. 内容与版权边界

- 当前实现只持久化订阅源提供的标题、摘要、来源、作者、时间、图片地址和原文链接。
- 不解析或保存 `content:encoded` / Atom `content` 中的整篇文章正文。
- 新闻详情页明确标注“媒体订阅源摘要”，并提供原媒体阅读入口。
- 不移除来源署名，不将媒体摘要包装为知页原创。
- 不绕过付费墙，不下载受限制的全文或图片。
- 图片只在订阅源明确提供可访问 URL 时展示；后续需进一步核对每家媒体的展示许可与署名要求。

## 5. 抓取方式与运行限制

当前版本是**客户端直接拉取公开订阅源**，不是自建爬虫服务，也没有后端聚合服务器。刷新在后台线程执行，解析结果保存到 Room。没有网络时可读取已缓存内容。

若未来需要多设备同步、统一抓取调度、来源故障监控或服务端去重，再评估独立后端；本课程阶段不为“看起来高级”而强行引入后端。
