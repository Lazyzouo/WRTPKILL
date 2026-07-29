# Official Release and Data Transparency / 官方发布与数据透明声明

> [!IMPORTANT]
> **WRTPKILL is fully open source.** The source code and official build workflow are publicly auditable. Official builds contain no hidden backdoor or telemetry and do not upload server configuration, player data, coordinates, worlds, logs, or runtime records. Data created and maintained by WRTPKILL remains stored on the server running the plugin.
>
> The update checker only sends HTTPS requests to the public GitHub Releases API to read version metadata and, when automatic download is enabled, download the selected official JAR. It does not send plugin-managed or Minecraft server data. As with any HTTPS request, GitHub receives ordinary connection metadata such as the source IP address and WRTPKILL version in the User-Agent header.
>
> **WRTPKILL 是完全开源项目。** 源代码与官方构建流程均公开可审计。官方构建不包含隐藏后门或遥测，不会上传服务器配置、玩家数据、坐标、世界、日志或运行记录。WRTPKILL 创建和维护的数据只保存在运行本插件的服务器上。
>
> 更新检查仅通过 HTTPS 访问公开的 GitHub Releases API，以读取版本信息，并在启用自动下载时下载所选官方 JAR；不会发送插件管理的数据或 Minecraft 服务器资料。与任何 HTTPS 请求相同，GitHub 会收到来源 IP 地址及 User-Agent 中的 WRTPKILL 版本等普通连接元数据。

## Official Release

This Release is built automatically from the corresponding tagged source by the repository's GitHub Actions workflow. The only official download page is:

**https://github.com/Lazyzouo/WRTPKILL/releases**

Do not treat third-party mirrors or repackaged files as official builds. Select the required version by its Release tag and install exactly one of these two uploaded assets:

- `en.us.jar`: English is the official default language.
- `zh.cn.jar`: Simplified Chinese is the official default language.

Both packages contain the same compiled plugin code, resources, and configuration comments. Only the official default `language` parameter differs. GitHub records a SHA-256 digest for each uploaded asset. GitHub's automatic **Source code (zip)** and **Source code (tar.gz)** links are source archives, not installable plugin JARs.

**Compatibility:** Paper/Folia `1.21.11` · **Tested:** Minecraft `1.21.11` · **Java:** 21 · **Author:** Lazyz

## 官方发布

本 Release 由仓库的 GitHub Actions 工作流根据对应标签源码自动构建。唯一官方发布下载页面为：

**https://github.com/Lazyzouo/WRTPKILL/releases**

请勿将第三方镜像或重新打包文件视为官方构建。请按 Release 标签选择所需版本，并且只安装下列两个上传资源中的一个：

- `en.us.jar`：官方默认语言为英文。
- `zh.cn.jar`：官方默认语言为简体中文。

两个包包含完全相同的已编译插件代码、资源与配置注释，仅官方默认 `language` 参数不同。GitHub 会为每个上传资源记录 SHA-256 摘要。GitHub 自动显示的 **Source code (zip)** 与 **Source code (tar.gz)** 是源码压缩包，不是可安装的插件 JAR。

**兼容：** Paper/Folia `1.21.11` · **测试版本：** Minecraft `1.21.11` · **Java：** 21 · **作者：** Lazyz
