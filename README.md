# WRTPKILL

[![Build](https://github.com/Lazyzouo/WRTPKILL/actions/workflows/build.yml/badge.svg)](https://github.com/Lazyzouo/WRTPKILL/actions/workflows/build.yml)
[![CodeQL](https://github.com/Lazyzouo/WRTPKILL/actions/workflows/codeql.yml/badge.svg)](https://github.com/Lazyzouo/WRTPKILL/actions/workflows/codeql.yml)
[![Release](https://img.shields.io/github/v/release/Lazyzouo/WRTPKILL)](https://github.com/Lazyzouo/WRTPKILL/releases/latest)
[![License](https://img.shields.io/github/license/Lazyzouo/WRTPKILL)](LICENSE)

> [!IMPORTANT]
> **Open Source and Data Transparency / 开源与数据透明声明**
>
> WRTPKILL is fully open source. Official builds contain no hidden backdoor or telemetry and do not upload server configuration, player data, coordinates, worlds, logs, or runtime records. Plugin-managed data remains on your server. Update checks only read version metadata from GitHub Releases and optionally download an official JAR; they do not upload Minecraft server data.
>
> WRTPKILL 是完全开源项目。官方构建不包含隐藏后门或遥测，不会上传服务器配置、玩家数据、坐标、世界、日志或运行记录；插件管理的数据只保存在你的服务器上。更新检查仅从 GitHub Releases 读取版本信息并按需下载官方 JAR，不会上传 Minecraft 服务器资料。
>
> **Official downloads / 唯一官方下载：** https://github.com/Lazyzouo/WRTPKILL/releases
>
> Select the version by its Release tag and install only `en.us.jar` or `zh.cn.jar`. The automatic Source code archives are not plugin JARs. / 请按 Release 标签选择版本，并且只安装 `en.us.jar` 或 `zh.cn.jar`；自动显示的 Source code 压缩包不是插件 JAR。
>
> Full statement / 完整声明：[RELEASE_NOTICE.md](RELEASE_NOTICE.md)

## English

WRTPKILL is a Folia-ready Paper plugin that combines multi-world random teleportation, player teleport requests, position visibility, controlled respawning, offline cleanup, and a one-use teleport lock. It is maintained by **Lazyz**.

### Official software introduction

WRTPKILL is recommended for PVP training servers, organized practice networks, and multi-arena communities that need a consistent way to move players between training areas. It provides the server-side workflow around a match: safe random deployment, player invitations, visible or private position sharing, controlled respawn, and lock recovery after death or a long disconnect.

The plugin is a training-server operations layer, not a combat engine or anti-cheat replacement. Pair it with the server's preferred PVP, arena, permissions, and protection plugins for the actual rules of each training mode.

#### Recommended PVP training scenarios

- Fast movement between overworld, Nether, and dedicated practice worlds.
- TPA-based team assembly before a drill or sparring session.
- `/pos` and `/nopos` for controlled teammate coordination and privacy.
- Predictable respawn and lock recovery after repeated practice deaths.
- Offline cleanup and shared spawn management for rotating training groups.

### Compatibility

| Item | Requirement |
| --- | --- |
| Tested/target Minecraft version | `1.21.11` |
| Server software | Paper or Folia |
| Java | 21 or newer |
| Plugin version | `1.1.3` |

The project compiles against Paper API `1.21.11`. Compatibility with forks or older Minecraft versions is not guaranteed.

### Features

- Dynamic per-world RTP commands with configurable bounds or WorldBorder support.
- `/tpa`, `/tpaccept`, `/tpdeny`, and `/tpcancel` with clickable controls and complete feedback.
- One-use RTP/TPA locking, released by death or the configured offline interval.
- OP-only teleport whitelist management.
- `/pos` world grouping, red world-name gradients, green coordinate gradients, and privacy through `/nopos`.
- Optional forced respawn, shared spawn, first-join teleport, and offline inventory cleanup.
- Simplified Chinese and English user interfaces.
- Startup update checks with automatic download to the server update directory.
- Folia-aware command, teleport, and player scheduling.

### Download and installation

1. Download `en.us.jar` from the [latest release](https://github.com/Lazyzouo/WRTPKILL/releases/latest). Use `zh.cn.jar` instead for a Simplified Chinese default installation.
2. Place it in the server `plugins` directory.
3. Start the server once to generate the configuration.
4. Restart the server. Use `/wrtp help` in game.

Both packages contain the same complete code, resources, configuration comments, and official default parameters. They differ only in the default `language` value. The plugin never overwrites an existing runtime configuration or language file. Repository defaults contain only official example values; a server owner's private values live outside this source repository.

### Main commands

| Command | Access | Purpose |
| --- | --- | --- |
| `/wrtp help` | Everyone | Show the context-aware help menu |
| `/tpa <player>` | Player | Send a teleport request |
| `/tpaccept`, `/tpdeny`, `/tpcancel` | Player | Manage teleport requests |
| `/pos`, `/nopos` | Player | View positions or change position privacy |
| `/suicide` | Player | Die and return through the configured respawn flow |
| `/wrtp setspawn [confirm]` | Admin | Set the shared respawn point |
| `/wrtp add <command> <world>` | Admin | Add a dynamic RTP command |
| `/wrtp remove <command>` | Admin | Remove a dynamic RTP command |
| `/wrtp reload` | Admin | Reload configuration and language |
| `/wrtp whitelist add <player>` | OP only | Add a teleport-lock bypass |

See [WRTPKILL.md](WRTPKILL.md) for all settings, behavior, limits, update rules, and operational notes.

### Automatic updates

At startup, WRTPKILL checks the latest GitHub Release. If a newer semantic version exists and `updater.auto-download` is enabled, it downloads `en.us.jar` or `zh.cn.jar` to match the configured language, then stages it under the running plugin JAR's filename in the update directory. The server must be restarted to apply it. Failures are reported in the console with the official release URL.

### Build from source

```bash
./gradlew clean build
```

Release-ready outputs are `build/libs/en.us.jar` and `build/libs/zh.cn.jar`. Gradle also creates an internal versioned JAR, but the official GitHub Release attaches only the two language packages. Do not publish a modified binary under the official project name without clearly identifying the fork.

## 中文

WRTPKILL 是由 **Lazyz** 维护的 Paper/Folia 综合插件，整合多世界随机传送、玩家互传、坐标展示与隐藏、死亡复活接管、离线清理及一次性传送权限锁。

### 官方软件介绍

WRTPKILL 建议用于 PVP 训练服务器、组织化练习网络和多竞技场社区，为训练区域之间的玩家移动提供统一的服务端流程。插件覆盖一场训练所需的基础环节：安全随机部署、玩家互相邀请、公开或私密坐标协作、可控复活，以及玩家死亡或长时间离线后的权限锁恢复。

本插件定位为训练服务器运营基础设施，不是战斗判定引擎，也不是反作弊插件。实际训练规则应配合服主选择的 PVP、竞技场、权限与区域保护插件共同配置。

#### 推荐的 PVP 训练场景

- 在主世界、下界和专用训练世界之间快速移动。
- 训练或对练开始前通过 TPA 组建队伍。
- 使用 `/pos` 与 `/nopos` 控制队友协作和坐标隐私。
- 重复训练死亡后获得统一的复活与传送锁恢复流程。
- 为轮换训练小组管理离线清理和统一复活点。

### 兼容性

| 项目 | 要求 |
| --- | --- |
| 测试/目标 Minecraft 版本 | `1.21.11` |
| 服务端 | Paper 或 Folia |
| Java | 21 或更高版本 |
| 插件版本 | `1.1.3` |

项目使用 Paper API `1.21.11` 编译；不保证兼容旧版 Minecraft 或所有第三方分支。

### 主要功能

- 按世界动态创建 RTP 指令，可使用自定义范围或世界边界。
- 完整的 TPA 请求、接受、拒绝、取消、超时反馈与点击按钮。
- RTP/TPA 一次性权限锁，死亡或达到离线时长后解除。
- 仅 OP 可用的传送白名单添加指令。
- `/pos` 按世界分组，世界名红色渐变、坐标绿色渐变，支持 `/nopos` 隐藏。
- 可选强制复活、统一复活点、首次进服传送和离线背包清理。
- 简体中文与英文界面。
- 开服自动检查更新并下载到服务端更新目录。
- 兼容 Folia 的调度与异步传送流程。

### 下载与安装

1. 从[最新 Release](https://github.com/Lazyzouo/WRTPKILL/releases/latest)下载中文默认包 `zh.cn.jar`；需要英文默认配置时下载 `en.us.jar`。
2. 放入服务端 `plugins` 目录。
3. 启动一次服务器生成配置。
4. 重启服务器，并在游戏内使用 `/wrtp help`。

两个安装包包含完全相同的完整代码、资源、配置注释和官方默认参数，唯一差异是默认 `language` 值。插件不会覆盖现有运行配置或语言文件。仓库只保存官方示例参数，服主的私人参数位于服务器运行目录，不会因源码同步而上传。

完整配置、逻辑、限制和运维说明见 [WRTPKILL.md](WRTPKILL.md)。

### 自动更新

插件会在启动时检查 GitHub 最新 Release。若发现更高语义版本且 `updater.auto-download` 已开启，会按当前配置语言选择 `en.us.jar` 或 `zh.cn.jar`，并沿用正在运行的插件 JAR 文件名暂存到更新目录；重启服务器后应用。失败时后台会显示官方 Release 地址供手动下载。

## License / 许可证

[MIT License](LICENSE) © 2026 Lazyz
