# 知页 · ZhiYe NewsApp

知页是一款以新闻阅读为核心的原生 Android 资讯应用。项目从零实现课程 NewsApp 案例，使用 Java + XML、MVVM、Room、Repository、Fragment、RecyclerView 与 Navigation Component。

## 功能

- 新闻首页：按主题、媒体来源筛选，支持下拉刷新。
- 议题目录：科技、政治、经济、文化、社会观察、娱乐。
- 媒体目录：区分已接入公开订阅源与仅提供官网入口的媒体。
- 搜索：按标题、摘要、媒体名称检索，并组合主题/媒体筛选。
- 新闻详情：查看订阅摘要、媒体署名、发布时间，跳转原文及系统分享。
- 阅读体验：收藏、阅读历史、阅读进度续读、字体大小与行间距设置。
- 本地内容：新闻新增、读取、编辑、删除。
- 本地账户：注册、登录与退出（课程演示用途，不是云端账号系统）。
- 设备适配：手机单栏、sw600dp 及以上平板双栏。

## 新闻数据与版权边界

应用通过媒体公开 RSS / Atom 订阅源获取新闻元数据。当前保存标题、摘要、来源、作者、发布时间、图片链接和原文链接，不抓取或缓存未经许可的整篇新闻。详情页明确标注订阅摘要，阅读全文跳转媒体原始页面。

Reuters 当前使用 Google News 对 Reuters 域名的聚合 RSS，明确标记为第三方聚合，不称为 Reuters 官方 RSS。新华社、人民日报、财新及 Financial Times 当前只提供官网入口；没有核实的公开订阅接口时，不伪装成已接入。

## 技术架构

- UI：Java + XML、Activity、Fragment、RecyclerView、Navigation Component。
- Architecture：MVVM + Repository。
- Persistence：Room（NewsEntity、UserEntity、DAO、数据库迁移）。
- Async：LiveData、ExecutorService、后台线程。
- Network：HTTPS RSS / Atom 解析、URL 哈希去重、Room 缓存。
- Images：Glide。
- Test：Room 与 RSS Instrumentation Tests、GitHub Actions Android build。

## 运行

Android Studio 打开项目，使用 JDK 17、Android SDK 35、Gradle 8.9，运行 app 模块。首次启动展示明确标记的离线样例内容；点击刷新后同步已配置的公开订阅源。网络不可用时仍可查看 Room 缓存和本地新闻。

## 课程要求映射

实验三要求从零实现 NewsApp 的部分功能，必须完成新闻列表显示与 Room 数据库操作；本项目同时使用 RecyclerView、Fragment 与 Navigation。课程大作业要求 MVVM、Room、基本注册登录和数据 CRUD；Repository 作为额外架构层使用。项目没有直接提交课程参考代码，而是独立实现数据层、页面和交互。

## 当前范围说明

本轮暂不实现有声新闻/全文 TTS。它属于可选阅读增强，不替代课程要求的新闻列表、Room、MVVM、注册登录和 CRUD；待核心功能稳定后再单独评估是否纳入最终范围。
