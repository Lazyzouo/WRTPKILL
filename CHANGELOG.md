# Changelog / 更新日志

All notable changes are recorded here. English is listed before Chinese.

所有重要变更均记录于此，英文内容排列在中文内容之前。

## [1.1.2] - 2026-07-29

### English

#### Added

- Official software introduction and recommended PVP training-server deployment guidance.
- Clear positioning of WRTPKILL as a training-server operations layer alongside PVP, arena, permissions, protection, and anti-cheat plugins.

#### Changed

- Project version and documentation compatibility labels updated to `1.1.2`.

### 中文

#### 新增

- 新增官方软件介绍与 PVP 训练服务器推荐部署说明。
- 明确 WRTPKILL 是配合 PVP、竞技场、权限、区域保护和反作弊插件使用的训练服务器运营基础设施。

#### 调整

- 项目版本和文档兼容性标识更新为 `1.1.2`。

## [1.1.1] - 2026-07-29

### English

#### Changed

- GitHub Releases now provide only `en.us.jar` and `zh.cn.jar` as uploaded assets; versioned JARs, configuration presets, and checksum files are no longer attached.
- Both language packages contain the complete compiled plugin resources and configuration comments. The English package changes only the official default language from `zh_CN` to `en_US`.
- Automatic updates now select the package matching the server's configured language and stage it under the running plugin JAR's filename for replacement on restart.

### 中文

#### 调整

- GitHub Release 上传资源现仅保留 `en.us.jar` 与 `zh.cn.jar`；不再附加带版本号 JAR、配置预设与校验和文件。
- 两个语言包都保留完整的已编译插件资源与配置注释；英文包只把官方默认语言由 `zh_CN` 改为 `en_US`。
- 自动更新会按服务器当前配置语言选择对应安装包，并沿用正在运行的插件 JAR 文件名暂存，待重启后替换。

## [1.1.0] - 2026-07-29

### English

#### Added

- Complete `en_US` language mode and downloadable English configuration preset.
- Startup semantic-version check and automatic stable-JAR download from official GitHub Releases.
- Prominent bilingual startup console banner.
- Official bilingual documentation, administration guide, contribution policy, security policy, issue forms, and pull-request template.
- GitHub Actions for builds, CodeQL scanning, and automatic versioned releases with checksums and language presets.
- OP-only `/wrtp whitelist add <player>` management and localized dynamic-help/position text.

#### Changed

- Project author is now `Lazyz` and project version is `1.1.0`.
- Build and documented compatibility target is Paper/Folia `1.21.11` with Java 21.
- Source configuration now contains only official example values; personal player and world values were removed.
- `/wrtp help` remains available without an administrator permission, while mutating subcommands enforce administrator access.
- World names in `/pos` use a red gradient, coordinates use a green gradient, and every world section ends with the standard divider.

#### Fixed

- All command failure paths now provide feedback, including offline/self TPA targets.
- Death, command-induced death, void death, respawn, and delayed rejoin notices remain recoverable through persistent pending markers.
- Help, death, respawn, and offline-rejoin panels remain intentionally unprefixed; ordinary command feedback uses `[WRTP]`.

### 中文

#### 新增

- 完整 `en_US` 英文模式及可下载英文配置预设。
- 启动时按语义版本检查官方 GitHub Release，并自动下载稳定 JAR。
- 醒目的双语后台启动横幅。
- 官方双语 README、管理员说明、贡献与安全政策、Issue 表单及 PR 模板。
- 自动构建、CodeQL 扫描、带校验和与语言预设的自动 Release 流程。
- 仅 OP 可用的 `/wrtp whitelist add <玩家>`，以及可本地化的动态帮助和坐标文本。

#### 调整

- 作者改为 `Lazyz`，版本升级至 `1.1.0`。
- 构建和文档目标调整为 Paper/Folia `1.21.11` 与 Java 21。
- 源码配置仅保留官方示例值，移除个人玩家名和个人世界名。
- `/wrtp help` 无需管理员权限；会修改配置的子指令会严格检查管理员权限。
- `/pos` 世界名使用红色渐变、坐标使用绿色渐变，每个世界分类底部显示统一分割线。

#### 修复

- 为全部指令失败路径补齐提示，包括 TPA 目标离线和向自己请求。
- 死亡、指令击杀、虚空死亡、复活与延迟重上线提示通过持久标记可靠恢复。
- 帮助、死亡、复活和离线重上线面板保持无前缀，普通指令反馈使用 `[WRTP]`。

[1.1.2]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.1.2
[1.1.1]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.1.1
[1.1.0]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.1.0
