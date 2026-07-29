# Security Policy / 安全政策

## English

### Supported versions

| Version | Supported |
| --- | --- |
| `1.1.x` | Yes |
| `< 1.1.0` | No |

Security support targets Paper/Folia `1.21.11` and Java 21.

### Data handling and network boundary

WRTPKILL is fully open source. Official builds contain no hidden backdoor or telemetry. Plugin configuration, player state, coordinates, worlds, logs, and runtime records are not uploaded and remain on the Minecraft server.

The updater only contacts the public GitHub Releases API to compare version metadata and, when enabled, download an official JAR. It sends no plugin-managed or Minecraft server data. GitHub receives ordinary HTTPS connection metadata, including the source IP address and WRTPKILL version in the User-Agent header. See [RELEASE_NOTICE.md](RELEASE_NOTICE.md) for the complete official statement.

### Reporting a vulnerability

Use GitHub's private **Report a vulnerability** feature in the Security tab. Do not publish exploitable details in a public issue. Include the affected version, server software, reproduction steps, impact, and any proposed mitigation. Maintainers will acknowledge a valid report as soon as practical and coordinate disclosure after a fix is available.

Never include production tokens, player data, IP addresses, or complete server configurations.

## 中文

### 支持版本

| 版本 | 是否支持 |
| --- | --- |
| `1.1.x` | 是 |
| `< 1.1.0` | 否 |

安全支持目标为 Paper/Folia `1.21.11` 与 Java 21。

### 数据处理与网络边界

WRTPKILL 是完全开源项目。官方构建不包含隐藏后门或遥测。插件不会上传配置、玩家状态、坐标、世界、日志或运行记录，这些数据只保存在 Minecraft 服务器上。

更新器仅访问公开的 GitHub Releases API 以比较版本，并在启用时下载官方 JAR；不会发送插件管理的数据或 Minecraft 服务器资料。GitHub 会收到来源 IP 地址及 User-Agent 中的 WRTPKILL 版本等普通 HTTPS 连接元数据。完整官方声明见 [RELEASE_NOTICE.md](RELEASE_NOTICE.md)。

### 漏洞报告

请在仓库 Security 页面使用私密的 **Report a vulnerability** 功能，不要在公开 Issue 中披露可利用细节。请提供受影响版本、服务端类型、复现步骤、影响和建议缓解措施。维护者会在合理时间内确认有效报告，并在修复可用后协调公开。

禁止提交生产令牌、玩家数据、IP 地址或完整服务器配置。
