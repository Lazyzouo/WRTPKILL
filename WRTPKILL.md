# WRTPKILL Administrator Guide / 管理员配置说明

Version `1.4.1` | Paper/Folia `1.21.11` | Java 21 | Author: Lazyz

## English

### Recommended deployment

WRTPKILL is recommended for PVP training servers, organized practice networks, and multi-arena communities. It provides the operational layer around a training session: safe movement between practice worlds, TPA-based team assembly, controlled position sharing, predictable respawn, and recovery of one-use teleport permissions after death or a long disconnect.

WRTPKILL does not replace a combat engine, arena ruleset, or anti-cheat plugin. Pair it with the server's preferred PVP, permissions, and protection systems.

### Data and update transparency

Official WRTPKILL builds contain no hidden backdoor or telemetry. Configuration, player state, coordinates, worlds, logs, and runtime records remain local to the server. The updater only reads public GitHub Release metadata and optionally downloads the selected official language JAR; it does not upload Minecraft server data. GitHub still receives ordinary HTTPS connection metadata such as the source IP address and the WRTPKILL version in the User-Agent header.

The only official download page is `https://github.com/Lazyzouo/WRTPKILL/releases`. Install `WRTPKILL-<version>-en.us.jar` or `WRTPKILL-<version>-zh.cn.jar` from the required tagged Release. GitHub's automatic source archives are not plugin JARs. The complete statement is maintained in `RELEASE_NOTICE.md` and prepended to every Release.

### Configuration ownership

The repository's `src/main/resources/config.yml` contains official example values only. At runtime, the user's settings live in `plugins/WRTPKILL/config.yml`. The two language packages contain the same complete schema and comments; only their default `language` value differs. Keep server-specific world names, coordinates, player names, and thresholds in the runtime copy, not in the source repository.

WRTPKILL uses a Kitloader-style schema migration identified by `config-version`. Starting with `1.4.1`, `config.yml` is the only active configuration file: missing official paths and comments are added while existing values, custom keys, and the user-owned `worlds` section are preserved. Every rewrite first creates a timestamped copy under `plugins/WRTPKILL/config-backups/`; a newer schema is never downgraded. The obsolete `.wrtpkill-default-config.yml` is no longer generated and is deleted after a successful upgrade. Changed files use temporary-file and atomic replacement, so manual deletion of `config.yml` is not required.

When an existing `en_US.yml` still contains the exact official pre-1.2.1 offline/unlock divider-panel defaults, WRTPKILL substitutes the current left-aligned layouts in memory. It does not write the migration back to disk and never changes customized text.

### Settings

| Path | Default | Behavior |
| --- | --- | --- |
| `language` | `zh_CN` | Accepts `zh_CN` or `en_US`; reload with `/wrtp reload` |
| `updater.enabled` | `true` | Checks the latest official GitHub Release at startup |
| `updater.auto-download` | `true` | Downloads a newer stable JAR to the server update directory |
| `whitelist` | `[]` | Player names that bypass the RTP/TPA one-use lock |
| `tpa-enabled` | `true` | Enables all player TPA commands |
| `tpa-safe-radius` | `32` | Minimum horizontal `/tpaccept` distance; clamped to `1-1024` and reloaded with `/wrtp reload` |
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
8. After `/tpaccept`, the requesting player is placed at a safe point at least `tpa-safe-radius` blocks from the target player. Candidate chunks load asynchronously; solid, non-liquid ground and two passable blocks are required. The accepted/success messages receive the effective value through `{radius}`. If no candidate is safe, the request fails without leaving the one-use lock active.
9. Startup, shutdown, and updater statuses share one colored `[WRTPKILL]` console prefix. The compact banner uses an inner width of 60 visible console columns, a centered bilingual control header, a dashed section divider, and complete cyan left/right borders; updater states use aqua, green, yellow, or red according to their result.
10. Every in-game component is recursively forced bold after legacy colors are parsed, including help, dynamic help, TPA buttons and hover text, and position gradients.
11. Every in-game message line passes through one final left-alignment stage. It removes visible leading whitespace from help, divider panels, `/pos`, ordinary feedback, and interactive TPA labels or hover text, including whitespace placed after formatting codes. The console startup banner uses a separate path and remains centered.
12. At startup, the configuration upgrader reads only `config.yml`, advances `config-version`, inserts missing official paths, and backs up the previous file under `config-backups/` before rewriting. Existing values and the dynamic `worlds` section remain user-owned. The legacy default baseline is deleted and never regenerated.

### Permissions and trust boundaries

- `worldrtp.admin` defaults to OP and grants configuration-changing subcommands.
- `/wrtp whitelist add <player>` checks `isOp()` explicitly and cannot be delegated through the permission node alone.
- `/wrtp help` is available to all users and intentionally has no plugin prefix.
- Help panels, death/respawn panels, and offline-rejoin panels have no prefix. Ordinary feedback uses `[WRTP]`.

### Limits

- RTP currently tests one random X/Z candidate per command execution. Unsafe candidates require the player to run the command again.
- Removing a dynamic RTP command from configuration stops it from functioning immediately, but the command label can remain in the server command map until restart.
- TPA requests expire after a fixed 30 seconds.
- `tpa-safe-radius` accepts `1-1024`; lower or higher values are clamped and the effective value is used by both placement and `{radius}` messages. Custom messages must keep the `{radius}` placeholder to display it.
- The whitelist bypasses the lock check. A successful teleport can still leave a lock marker that will be bypassed while the player remains whitelisted.
- Automatic updates require outbound HTTPS access to `api.github.com` and GitHub release assets. Each Release has exactly two manually uploaded packages, `WRTPKILL-<version>-en.us.jar` and `WRTPKILL-<version>-zh.cn.jar`; GitHub also exposes its unavoidable automatic source archives. Release JARs are uploaded directly from Gradle output and must never be renamed. The updater cannot replace the active JAR until server restart.
- Offline cleanup permanently clears inventory and Ender Chest. Test the interval and backup policy before enabling it on a production server.
- For a consistent framed layout, custom divider-panel lines should remain within 39 visible characters. Split longer custom text with `\n` to avoid client-side wrapping.
- Without a default baseline, a deliberately deleted official path is treated as missing and can be restored during a later schema migration. Custom keys and the entire `worlds` section remain preserved.
- Invalid YAML cannot be merged. WRTPKILL leaves the original `config.yml` untouched and reports the error; the syntax must be corrected, but deleting the file is not required.
- `.wrtpkill-default-config.yml` is obsolete in `1.4.1` and is removed automatically after a successful upgrade. Timestamped files under `config-backups/` remain local recovery copies of the previous runtime configuration.
- Paper/Folia `1.21.11` and Java 21 are the supported target. Older versions and unrelated server implementations are not guaranteed.

## 中文

### 推荐部署

WRTPKILL 建议用于 PVP 训练服务器、组织化练习网络和多竞技场社区，为训练流程提供基础运营层：在训练世界之间安全移动、通过 TPA 组建队伍、控制坐标分享、统一复活，以及在死亡或长时间离线后恢复一次性传送权限。

WRTPKILL 不替代战斗引擎、竞技场规则或反作弊插件，应与服主选择的 PVP、权限和区域保护系统配合使用。

### 数据与更新透明度

WRTPKILL 官方构建不包含隐藏后门或遥测。配置、玩家状态、坐标、世界、日志和运行记录只保存在服务器本地。更新器仅读取公开的 GitHub Release 版本信息，并按需下载所选官方语言 JAR；不会上传 Minecraft 服务器资料。GitHub 仍会收到来源 IP 地址及 User-Agent 中的 WRTPKILL 版本等普通 HTTPS 连接元数据。

唯一官方下载页面是 `https://github.com/Lazyzouo/WRTPKILL/releases`。请从所需标签的 Release 中安装 `WRTPKILL-<版本>-en.us.jar` 或 `WRTPKILL-<版本>-zh.cn.jar`；GitHub 自动显示的源码压缩包不是插件 JAR。完整声明保存在 `RELEASE_NOTICE.md`，并会自动置于每个 Release 顶部。

### 配置归属

仓库内 `src/main/resources/config.yml` 只保存官方示例参数。服务端的用户参数位于 `plugins/WRTPKILL/config.yml`。两个语言包包含相同的完整结构与注释，只有默认 `language` 值不同。服务器专用世界名、坐标、玩家名和阈值应只写入运行目录，不应提交到源码仓库。

WRTPKILL 使用由 `config-version` 标识的 Kitloader 风格 schema 迁移。`1.4.1` 起只使用 `config.yml`：自动补充缺失的官方路径与注释，同时保留现有参数、自定义节点和归用户所有的 `worlds` 区段。每次重写前会将旧文件备份到 `plugins/WRTPKILL/config-backups/`，高于当前插件的 schema 不会被强制降级。旧版 `.wrtpkill-default-config.yml` 不再生成，并会在成功升级后自动删除；配置使用临时文件与原子替换写入，不需要手动删除 `config.yml`。

当现有 `en_US.yml` 仍使用 1.2.1 之前完全一致的官方离线/解锁分割线面板默认值时，WRTPKILL 会仅在内存中替换为当前左对齐布局；不会写回磁盘，也不会修改任何自定义文本。

### 配置项

| 路径 | 默认值 | 逻辑 |
| --- | --- | --- |
| `language` | `zh_CN` | 可选 `zh_CN` 或 `en_US`，使用 `/wrtp reload` 重载 |
| `updater.enabled` | `true` | 开服时检查官方 GitHub 最新 Release |
| `updater.auto-download` | `true` | 将新版稳定 JAR 下载到服务端更新目录 |
| `whitelist` | `[]` | 绕过 RTP/TPA 一次性传送锁的玩家名 |
| `tpa-enabled` | `true` | TPA 系列指令总开关 |
| `tpa-safe-radius` | `32` | `/tpaccept` 最小水平距离，限制为 `1-1024`，使用 `/wrtp reload` 重载 |
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
8. `/tpaccept` 后，请求方会被放置在目标玩家至少 `tpa-safe-radius` 格外的安全位置。候选区块会异步加载，落点必须有坚实非液体地面和两个可通行方块；接受/成功提示通过 `{radius}` 显示本次有效值。找不到安全点时请求失败且不会残留一次性传送锁。
9. 启动、卸载和更新器状态统一使用彩色 `[WRTPKILL]` 后台前缀；紧凑横幅采用 60 个可见控制台列的内部宽度，并包含居中双语管理标题、虚线区段分隔和完整青色左右边框；更新状态按结果使用青、绿、黄或红色。

10. 所有游戏内组件在解析颜色后都会递归强制加粗，包括帮助、动态帮助、TPA 按钮及悬停说明和坐标渐变。
11. 所有游戏内消息都会经过统一的最终左对齐阶段，清除帮助菜单、分割线面板、`/pos`、普通反馈、TPA 按钮及悬停文本的可见行首空格，包括写在颜色代码之后的空格。服务器后台启动横幅走独立路径并继续居中。
12. 插件启动时只读取 `config.yml`，推进 `config-version`、补充缺失的官方路径，并在重写前备份旧文件到 `config-backups/`。现有参数与动态 `worlds` 区段继续归用户所有；旧默认值基线会被删除且不会再次生成。

### 权限边界

- `worldrtp.admin` 默认仅 OP 拥有，用于所有修改配置的管理子指令。
- `/wrtp whitelist add <玩家>` 额外直接检查 OP，不能只靠权限节点委派。
- `/wrtp help` 对所有玩家开放，并且不显示插件前缀。
- 帮助、死亡/复活和离线重上线面板无前缀，普通反馈统一使用 `[WRTP]`。

### 限制

- RTP 每次执行只尝试一个随机 X/Z；若不安全，玩家需要重新执行指令。
- 从配置移除动态 RTP 后功能立即失效，但指令标签可能保留在服务端命令表中直至重启。
- TPA 请求超时固定为 30 秒。
- `tpa-safe-radius` 可设置 `1-1024`，超出范围会自动限制；落点和 `{radius}` 消息使用同一个有效值。自定义消息需保留 `{radius}` 才会显示半径。
- 白名单绕过锁定检查；成功传送后仍可能写入锁标记，但玩家留在白名单期间会继续绕过。
- 自动更新需要访问 `api.github.com` 和 GitHub Release 资源；每个 Release 只手动上传 `WRTPKILL-<版本>-en.us.jar` 与 `WRTPKILL-<版本>-zh.cn.jar`，GitHub 仍会显示无法关闭的自动源码压缩包。Release JAR 必须直接上传 Gradle 原产物，禁止改名。活动中的 JAR 必须等服务端重启才能替换。
- 离线清理会永久清空背包与末影箱，生产服启用前必须确认阈值并做好备份。
- 为保持分割线框架整齐，自定义面板每行应保持在 39 个可见字符以内；更长文本应使用 `\n` 拆行，避免客户端自动换行。
- 不再保存默认值基线，因此主动删除的官方路径在后续 schema 迁移时会被视为缺失并可能重新补回；自定义节点与整个 `worlds` 区段仍会保留。
- 无效 YAML 无法合并。WRTPKILL 会保持原 `config.yml` 不动并在后台报告错误；只需修正语法，不需要删除配置文件。
- `.wrtpkill-default-config.yml` 从 `1.4.1` 起已废弃，并会在成功升级后自动删除；`config-backups/` 中的时间戳文件仍是旧运行配置的本地恢复副本。
- 官方目标是 Paper/Folia `1.21.11` 与 Java 21，不保证兼容旧版或其他服务端实现。
