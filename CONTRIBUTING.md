# Contributing / 贡献指南

## English

Thank you for improving WRTPKILL. Open an issue before a large behavioral change so compatibility and configuration migration can be discussed first.

### Development requirements

- Java 21
- Paper API target `1.21.11`
- Gradle Wrapper included in this repository

### Pull requests

1. Keep changes focused and preserve Folia compatibility.
2. Update both English and Chinese user-facing text when behavior changes.
3. Update `CHANGELOG.md`, `README.md`, and `WRTPKILL.md` when relevant.
4. Increment the semantic version for every code, resource, or behavior update.
5. Run `./gradlew clean build` and report the result in the pull request.
6. Do not commit server-specific player names, coordinates, world names, tokens, logs, or runtime data.

By contributing, you agree that your contribution is licensed under the MIT License.

## 中文

感谢参与 WRTPKILL。较大的行为改动请先提交 Issue，以便先讨论兼容性和配置迁移。

### 开发要求

- Java 21
- Paper API 目标版本 `1.21.11`
- 使用仓库自带的 Gradle Wrapper

### Pull Request 要求

1. 保持改动集中，并维持 Folia 兼容性。
2. 行为变化时同步更新英文和中文文本。
3. 必要时更新 `CHANGELOG.md`、`README.md` 和 `WRTPKILL.md`。
4. 每次代码、资源或行为更新都必须提升语义版本。
5. 执行 `./gradlew clean build`，并在 PR 中说明结果。
6. 禁止提交服务器专用玩家名、坐标、世界名、令牌、日志或运行数据。

提交贡献即表示同意按 MIT License 授权。
