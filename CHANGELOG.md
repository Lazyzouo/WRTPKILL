# Changelog / 更新日志

All notable changes are recorded here. English is listed before Chinese.

所有重要变更均记录于此，英文内容排列在中文内容之前。

## [1.2.5] - 2026-07-30

### English

#### Changed

- Removed in-game centering and left-aligned divider panels and `/pos` output.
- Divider panels and `/pos` now remove legacy leading spaces after placeholder replacement; help preserves command-list indentation and the console startup banner remains centered.
- Updated layout regression coverage and project version labels to `1.2.5`.

### 中文

#### 调整

- 取消游戏内文本居中，将分割线面板与 `/pos` 输出统一改为左对齐。
- 分割线面板与 `/pos` 会在替换动态变量后清除旧行首空格；帮助菜单保留命令列表缩进，控制台启动横幅继续居中。
- 更新布局回归测试，并将项目版本标识更新为 `1.2.5`。

## [1.2.4] - 2026-07-30

### English

#### Changed

- Reduced the startup-banner inner width from 76 to 60 visible console columns for a more compact server-console footprint.
- Preserved the bilingual header, dashed section divider, complete right border, and spacing around every official detail row.
- Updated banner regression coverage and project version labels to `1.2.4`.

### 中文

#### 调整

- 将启动横幅内部宽度从 76 个可见控制台列缩减至 60 个，降低其在服务器控制台中的横向占比。
- 保留双语顶部标题、虚线区段分隔、完整右边框及所有官方详情行的安全留白。
- 更新横幅回归测试，并将项目版本标识更新为 `1.2.4`。

## [1.2.3] - 2026-07-30

### English

#### Changed

- Added a centered bilingual control subtitle beneath the startup-banner title.
- Added a full-width dashed divider between the new header and plugin detail rows, and expanded the inner width from 60 to 76 visible console columns.
- Extended banner regression coverage and updated project version labels to `1.2.3`.

### 中文

#### 调整

- 在启动横幅主标题下新增居中的双语管理副标题。
- 在新顶部标题区与插件详情之间新增全宽虚线分隔，并将内部宽度从 60 扩展至 76 个可见控制台列。
- 扩展横幅回归测试，并将项目版本标识更新为 `1.2.3`。

## [1.2.2] - 2026-07-30

### English

#### Fixed

- Completed the cyan right border on every startup-banner detail row.
- Expanded the banner inner width from 52 to 60 visible console columns so the GitHub URL and transparency statement retain spacing before the border.
- Added startup-banner layout regression coverage and updated project version labels to `1.2.2`.

### 中文

#### 修复

- 为启动横幅的每一条详情行补齐青色右边框。
- 将横幅内部宽度从 52 个可见控制台列扩展到 60 个，确保 GitHub 地址与开源透明声明不会紧贴或遮挡边框。
- 新增启动横幅布局回归测试，并将项目版本标识更新为 `1.2.2`。

## [1.2.1] - 2026-07-29

### English

#### Added

- Recursive bold enforcement for every in-game WRTPKILL component, including help menus, dynamic RTP help, ordinary feedback, TPA buttons and hover text, and position gradients.
- Divider-star centering that removes manual indentation and recalculates each non-help panel line after dynamic placeholders are replaced.
- Layout tests for legacy and hex color codes, existing indentation removal, exact divider width, and every official Chinese and English centered-panel line.

#### Changed

- `/pos` now routes its divider, titles, world groups, player coordinates, empty state, and totals through the shared bold and centered message pipeline.
- Overlong English offline and unlock panels were split into centered lines within the 39-character divider width.
- Existing unmodified pre-1.2.1 English offline/unlock panel defaults are upgraded in memory without writing to `en_US.yml`; customized messages remain untouched.
- Project version and documentation compatibility labels updated to `1.2.1`.

### 中文

#### 新增

- 对所有 WRTPKILL 游戏内组件递归强制加粗，包括帮助菜单、动态 RTP 帮助、普通反馈、TPA 按钮及悬停说明和坐标渐变。
- 新增基于分割线中央星星的自动居中：移除旧手工缩进，并在替换动态变量后重新计算所有非帮助面板内文。
- 新增颜色码、十六进制颜色、旧缩进清理、准确分割线宽度及全部中英文官方面板行的布局回归测试。

#### 调整

- `/pos` 的分割线、标题、世界分类、玩家坐标、空状态和总计均改走共享粗体与居中消息管线。
- 将过长的英文离线与解锁面板拆分为不超过 39 个可见字符的居中行。
- 现有未修改的 1.2.1 之前英文官方离线/解锁面板默认值会仅在内存中升级，不写入 `en_US.yml`，自定义消息保持不变。
- 项目版本与文档兼容性标识更新为 `1.2.1`。

## [1.2.0] - 2026-07-29

### English

#### Added

- Versioned, language-specific Gradle artifacts using the immutable `WRTPKILL-<version>-<language>.jar` template.
- A WRTPKILL-specific, Kitloader-inspired colored startup banner with version, author, tested platform, language, GitHub, and open-source transparency details.
- Colored console states for update checking, disabled checks, latest version, available updates, manual downloads, successful downloads, failures, and plugin shutdown.
- Unit coverage for exact English and Simplified Chinese Release asset names.

#### Changed

- The Release workflow now uploads the original Gradle outputs directly from `build/libs`; copying or renaming Release JARs is forbidden by workflow and maintenance policy.
- The updater now resolves the exact versioned language asset from the latest Release while retaining the running plugin filename in the server update directory for restart replacement.
- The Release catalog is reset to `v1.2.0`; earlier Releases and tags are retired after the new assets pass verification.
- Project version and documentation compatibility labels updated to `1.2.0`.

### 中文

#### 新增

- 新增使用固定 `WRTPKILL-<版本>-<语言>.jar` 模板的带版本号双语言 Gradle 产物。
- 新增参考 Kitloader 但为 WRTPKILL 独立设计的彩色启动横幅，展示版本、作者、测试平台、语言、GitHub 与开源透明声明。
- 为更新检查、检查关闭、已是最新、发现新版、手动下载、下载成功、下载失败和插件卸载新增不同颜色的后台状态提示。
- 新增中英文 Release 资源准确文件名的单元测试。

#### 调整

- Release 工作流改为从 `build/libs` 直接上传 Gradle 原产物；工作流与维护规则均禁止复制或改名 Release JAR。
- 更新器会从最新 Release 精确选择带版本号的对应语言资源，同时仍沿用当前插件文件名暂存到服务器更新目录，供重启替换。
- Release 目录从 `v1.2.0` 重新作为保留基线；新版资源验证通过后移除更早的 Release 与标签。
- 项目版本与文档兼容性标识更新为 `1.2.0`。

## [1.1.3] - 2026-07-29

### English

#### Added

- Pinned bilingual open-source, no-backdoor, local-data-storage, and updater-network transparency statement.
- Official Release declaration identifying the only official download page, the two installable language JARs, compatibility, and the non-installable GitHub source archives.
- Reusable release-note generation that automatically prepends the declaration to every future GitHub Release.

#### Changed

- Project version and documentation compatibility labels updated to `1.1.3`.

### 中文

#### 新增

- 新增置顶双语声明，说明项目完全开源、无后门、插件数据仅本地保存，并公开更新器网络边界。
- 新增官方发布声明，标明唯一官方下载页、两个可安装语言 JAR、兼容范围，以及不可安装的 GitHub 源码压缩包。
- 新增可复用 Release 说明生成流程，今后每个 GitHub Release 都会自动在顶部加入该声明。

#### 调整

- 项目版本和文档兼容性标识更新为 `1.1.3`。

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

[1.2.5]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.2.5
[1.2.4]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.2.4
[1.2.3]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.2.3
[1.2.2]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.2.2
[1.2.1]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.2.1
[1.2.0]: https://github.com/Lazyzouo/WRTPKILL/releases/tag/v1.2.0
