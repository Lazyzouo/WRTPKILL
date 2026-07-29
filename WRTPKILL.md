# WRTPKILL Administrator Guide / 管理员配置说明

Version `1.2.2` | Paper/Folia `1.21.11` | Java 21 | Author: Lazyz

## English

### Recommended deployment

WRTPKILL is recommended for PVP training servers, organized practice networks, and multi-arena communities. It provides the operational layer around a training session: safe movement between practice worlds, TPA-based team assembly, controlled position sharing, predictable respawn, and recovery of one-use teleport permissions after death or a long disconnect.

WRTPKILL does not replace a combat engine, arena ruleset, or anti-cheat plugin. Pair it with the server's preferred PVP, permissions, and protection systems.

### Data and update transparency

Official WRTPKILL builds contain no hidden backdoor or telemetry. Configuration, player state, coordinates, worlds, logs, and runtime records remain local to the server. The updater only reads public GitHub Release metadata and optionally downloads the selected official language JAR; it does not upload Minecraft server data. GitHub still receives ordinary HTTPS connection metadata such as the source IP address and the WRTPKILL version in the User-Agent header.

The only official download page is `https://github.com/Lazyzouo/WRTPKILL/releases`. Install `WRTPKILL-<version>-en.us.jar` or `WRTPKILL-<version>-zh.cn.jar` from the required tagged Release. GitHub's automatic source archives are not plugin JARs. The complete statement is maintained in `RELEASE_NOTICE.md` and prepended to every Release.

### Configuration ownership

The repository's `src/main/resources/config.yml` contains official example values only. At runtime, Paper extracts it to `plugins/WRTPKILL/config.yml`. The `WRTPKILL-<version>-en.us.jar` and `WRTPKILL-<version>-zh.cn.jar` packages preserve the complete configuration and its comments; only their default `language` value differs. Existing runtime configuration and `plugins/WRTPKILL/lang/en_US.yml` are not overwritten during updates. Keep server-specific world names, coordinates, player names, and thresholds in the runtime copy, not in the source repository.

When an existing `en_US.yml` still contains the exact official pre-1.2.1 offline/unlock divider-panel defaults, WRTPKILL substitutes the new centered layouts in memory. It does not write the migration back to disk and never changes customized text.

### Settings

| Path | Default | Behavior |
| --- | --- | --- |
| `language` | `zh_CN` | Accepts `zh_CN` or `en_US`; reload with `/wrtp reload` |
| `updater.enabled` | `true` | Checks the latest official GitHub Release at startup |
| `updater.auto-download` | `true` | Downloads a newer stable JAR to the server update directory |
| `whitelist` | `[]` | Player names that bypass the RTP/TPA one-use lock |
| `tpa-enabled` | `true` | Enables all player TPA commands |
| `suicide-settings.force-respawn-enabled` | `true` | Skips the vanilla death screen and uses the shared spawn |
| `suicide-settings.respawn-world` | `world` | World used by the shared respawn location |
| `suicide-settings.offline-clear-enabled` | `true` | Enables delayed offline cleanup and lock release |
| `suicide-settings.offline-clear-minutes` | `3` | Offline duration before cleanup applies |
| `suicide-settings.first-join-teleport-enabled` | `true` | Teleports first-time players to the shared spawn |
| `custom-spawn.enabled` | `false` | Uses configured coordinates instead of the world's spawn |
| `worlds.<command>.world-name` | varies | Target world for the generated RTP command |
| `worlds.<command>.use-border` | `false` | Uses WorldBorder instead of manual X/Z bounds |
| `scan-min-y`, `scan-max-y` | per world | Vertical range scanned for a landing point |

Use `/wrtp setspawn`, then `/wrtp setspawn confirm` within 10 seconds, instead of manually editing spawn coordinates.

### Core logic

1. Each configured key below `worlds` is registered as a command at startup or reload.
2. RTP selects one random X/Z point, loads its chunk asynchronously, scans the configured Y range, and teleports asynchronously when a standing position is found.
3. A completed RTP or accepted TPA sets the player's persistent one-use lock.
4. Death clears that lock and displays one combined respawn/unlock panel. Death-related pending state survives delayed respawn and rejoin handling.
5. When offline cleanup triggers, inventory and Ender Chest are cleared, the player returns to spawn, and the teleport lock is removed.
6. `/nopos` stores persistent privacy state. Administrators can still see hidden players and receive a hidden marker.
7. The updater compares semantic versions from GitHub Releases. It selects `WRTPKILL-<version>-en.us.jar` or `WRTPKILL-<version>-zh.cn.jar` according to the active language, saves it under the running plugin JAR's filename in the update directory, and requires a restart to apply it.
8. Startup, shutdown, and updater statuses share one colored `[WRTPKILL]` console prefix. The banner uses an inner width of 60 visible console columns with complete cyan left/right borders; updater states use aqua, green, yellow, or red according to their result.
9. Every in-game component is recursively forced bold after legacy colors are parsed, including help, dynamic help, TPA buttons and hover text, and position gradients.
10. Non-help divider panels remove manual indentation and center each line after placeholder replacement against the divider's middle star. Help menus intentionally keep their list layout.

### Permissions and trust boundaries

- `worldrtp.admin` defaults to OP and grants configuration-changing subcommands.
- `/wrtp whitelist add <player>` checks `isOp()` explicitly and cannot be delegated through the permission node alone.
- `/wrtp help` is available to all users and intentionally has no plugin prefix.
- Help panels, death/respawn panels, and offline-rejoin panels have no prefix. Ordinary feedback uses `[WRTP]`.

### Limits

- RTP currently tests one random X/Z candidate per command execution. Unsafe candidates require the player to run the command again.
- Removing a dynamic RTP command from configuration stops it from functioning immediately, but the command label can remain in the server command map until restart.
- TPA requests expire after a fixed 30 seconds.
- The whitelist bypasses the lock check. A successful teleport can still leave a lock marker that will be bypassed while the player remains whitelisted.
- Automatic updates require outbound HTTPS access to `api.github.com` and GitHub release assets. Each Release has exactly two manually uploaded packages, `WRTPKILL-<version>-en.us.jar` and `WRTPKILL-<version>-zh.cn.jar`; GitHub also exposes its unavoidable automatic source archives. Release JARs are uploaded directly from Gradle output and must never be renamed. The updater cannot replace the active JAR until server restart.
- Offline cleanup permanently clears inventory and Ender Chest. Test the interval and backup policy before enabling it on a production server.
- Custom divider-panel lines should remain within 39 visible characters. Longer custom text must be split with `\n`; custom resource-pack fonts can shift the visual result because centering is based on visible character columns rather than client-specific pixel metrics.
- Paper/Folia `1.21.11` and Java 21 are the supported target. Older versions and unrelated server implementations are not guaranteed.

## 中文

### 推荐部署

WRTPKILL 建议用于 PVP 训练服务器、组织化练习网络和多竞技场社区，为训练流程提供基础运营层：在训练世界之间安全移动、通过 TPA 组建队伍、控制坐标分享、统一复活，以及在死亡或长时间离线后恢复一次性传送权限。

WRTPKILL 不替代战斗引擎、竞技场规则或反作弊插件，应与服主选择的 PVP、权限和区域保护系统配合使用。

### 数据与更新透明度

WRTPKILL 官方构建不包含隐藏后门或遥测。配置、玩家状态、坐标、世界、日志和运行记录只保存在服务器本地。更新器仅读取公开的 GitHub Release 版本信息，并按需下载所选官方语言 JAR；不会上传 Minecraft 服务器资料。GitHub 仍会收到来源 IP 地址及 User-Agent 中的 WRTPKILL 版本等普通 HTTPS 连接元数据。

唯一官方下载页面是 `https://github.com/Lazyzouo/WRTPKILL/releases`。请从所需标签的 Release 中安装 `WRTPKILL-<版本>-en.us.jar` 或 `WRTPKILL-<版本>-zh.cn.jar`；GitHub 自动显示的源码压缩包不是插件 JAR。完整声明保存在 `RELEASE_NOTICE.md`，并会自动置于每个 Release 顶部。

### 配置归属

仓库内 `src/main/resources/config.yml` 只保存官方示例参数。`WRTPKILL-<版本>-en.us.jar` 与 `WRTPKILL-<版本>-zh.cn.jar` 都保留完整配置及其注释，只有默认 `language` 值不同。服务端运行时会在 `plugins/WRTPKILL/config.yml` 生成独立配置；更新插件不会覆盖现有运行配置，也不会覆盖 `plugins/WRTPKILL/lang/en_US.yml`。服务器专用世界名、坐标、玩家名和阈值应只写入运行目录，不应提交到源码仓库。

当现有 `en_US.yml` 仍使用 1.2.1 之前完全一致的官方离线/解锁分割线面板默认值时，WRTPKILL 会仅在内存中替换为新版居中布局；不会写回磁盘，也不会修改任何自定义文本。

### 配置项

| 路径 | 默认值 | 逻辑 |
| --- | --- | --- |
| `language` | `zh_CN` | 可选 `zh_CN` 或 `en_US`，使用 `/wrtp reload` 重载 |
| `updater.enabled` | `true` | 开服时检查官方 GitHub 最新 Release |
| `updater.auto-download` | `true` | 将新版稳定 JAR 下载到服务端更新目录 |
| `whitelist` | `[]` | 绕过 RTP/TPA 一次性传送锁的玩家名 |
| `tpa-enabled` | `true` | TPA 系列指令总开关 |
| `suicide-settings.force-respawn-enabled` | `true` | 跳过原版死亡界面并使用统一复活点 |
| `suicide-settings.respawn-world` | `world` | 统一复活点所在世界 |
| `suicide-settings.offline-clear-enabled` | `true` | 开启离线清理与传送锁解除 |
| `suicide-settings.offline-clear-minutes` | `3` | 触发离线清理所需分钟数 |
| `suicide-settings.first-join-teleport-enabled` | `true` | 首次进服传送到统一复活点 |
| `custom-spawn.enabled` | `false` | 使用自定义坐标而非世界出生点 |
| `worlds.<指令>.world-name` | 按项设置 | 动态 RTP 指令的目标世界 |
| `worlds.<指令>.use-border` | `false` | 使用世界边界而非手动 X/Z 范围 |
| `scan-min-y`, `scan-max-y` | 按世界设置 | 安全落脚点的垂直扫描范围 |

建议在游戏内执行 `/wrtp setspawn`，并在 10 秒内执行 `/wrtp setspawn confirm`，不要手动填写复活坐标。

### 核心逻辑

1. `worlds` 下每个键都会在启动或重载时注册为动态指令。
2. RTP 每次选择一个随机 X/Z，异步加载区块，在配置的 Y 范围扫描落脚点，再异步传送。
3. 成功 RTP 或接受 TPA 后会写入玩家持久化的一次性传送锁。
4. 玩家死亡会解除传送锁，并合并显示复活与权限恢复消息；延迟复活或重新上线时仍可恢复待显示消息。
5. 离线清理触发后会清空背包和末影箱、返回复活点并解除传送锁。
6. `/nopos` 使用持久数据保存隐藏状态；管理员仍可看到隐藏玩家并收到隐藏标记。
7. 更新器按语义版本比较 GitHub Release，根据当前语言选择 `WRTPKILL-<版本>-en.us.jar` 或 `WRTPKILL-<版本>-zh.cn.jar`，沿用正在运行的插件 JAR 文件名保存到更新目录，重启后应用。
8. 启动、卸载和更新器状态统一使用彩色 `[WRTPKILL]` 后台前缀；横幅采用 60 个可见控制台列的内部宽度并补全青色左右边框，更新状态按结果使用青、绿、黄或红色。

9. 所有游戏内组件在解析颜色后都会递归强制加粗，包括帮助、动态帮助、TPA 按钮及悬停说明和坐标渐变。
10. 除帮助外，分割线面板会先移除手工缩进，在替换动态变量后以分割线中央星星为基准逐行居中；帮助菜单保留列表排版。

### 权限边界

- `worldrtp.admin` 默认仅 OP 拥有，用于所有修改配置的管理子指令。
- `/wrtp whitelist add <玩家>` 额外直接检查 OP，不能只靠权限节点委派。
- `/wrtp help` 对所有玩家开放，并且不显示插件前缀。
- 帮助、死亡/复活和离线重上线面板无前缀，普通反馈统一使用 `[WRTP]`。

### 限制

- RTP 每次执行只尝试一个随机 X/Z；若不安全，玩家需要重新执行指令。
- 从配置移除动态 RTP 后功能立即失效，但指令标签可能保留在服务端命令表中直至重启。
- TPA 请求超时固定为 30 秒。
- 白名单绕过锁定检查；成功传送后仍可能写入锁标记，但玩家留在白名单期间会继续绕过。
- 自动更新需要访问 `api.github.com` 和 GitHub Release 资源；每个 Release 只手动上传 `WRTPKILL-<版本>-en.us.jar` 与 `WRTPKILL-<版本>-zh.cn.jar`，GitHub 仍会显示无法关闭的自动源码压缩包。Release JAR 必须直接上传 Gradle 原产物，禁止改名。活动中的 JAR 必须等服务端重启才能替换。
- 离线清理会永久清空背包与末影箱，生产服启用前必须确认阈值并做好备份。
- 自定义分割线面板的每行应保持在 39 个可见字符以内；更长文本必须使用 `\n` 拆行。自定义资源包字体的字形宽度可能改变视觉效果，因为居中依据可见字符列，而不是客户端专用像素宽度。
- 官方目标是 Paper/Folia `1.21.11` 与 Java 21，不保证兼容旧版或其他服务端实现。
