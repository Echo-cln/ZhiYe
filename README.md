# 知页 · ZhiYe NewsApp

以新闻阅读为核心的原生 Android 资讯应用。项目从零实现课程 NewsApp 案例，采用 Java + XML、MVVM、Room、Repository、Fragment、RecyclerView 与 Navigation Component。

## 功能
- 新闻首页、六大议题频道、媒体来源目录、搜索、新闻详情
- 收藏、阅读历史、设置、本地新闻新增/编辑/删除
- 本地账户注册、登录（课程演示，不是云端认证）
- RSS / Atom 新闻订阅、Room 缓存、离线阅读
- 手机单栏与 sw600dp 平板双栏布局

## 新闻数据与版权边界
应用优先读取媒体公开 RSS / Atom；只持久化标题、摘要、来源、发布时间、图片链接和原文链接等元数据，不抓取或缓存未经许可的整篇文章。全文阅读跳转原媒体页面。Reuters 如使用 Google News RSS，明确标记为聚合结果而非官方 Reuters Feed。没有可核验订阅接口的媒体仅展示官网入口，不伪装已接入。

## 技术栈
Java 17 · XML · AndroidX · Material Components · MVVM · LiveData · Room · Repository · Navigation · RecyclerView · Glide

## 运行
Android Studio 打开项目，使用 JDK 17、Android SDK 35、Gradle 8.9；运行 app 模块。首次点击刷新后同步公开订阅源。网络不可用时读取 Room 缓存。GitHub Actions 配置 Debug 构建与单元测试。

## 课程要求映射
实验三：从零实现 NewsApp；完成新闻列表显示与 Room 数据库操作，并使用 RecyclerView、Fragment 显示/替换/数据传递；Navigation 为增强项。课程大作业在此基础上增加注册登录、CRUD、媒体与主题筛选、收藏、历史、设置及平板适配。
