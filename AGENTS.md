# Project Instructions

- Increment the plugin version for every code, resource, or behavior update.
- Use semantic versioning. Increment the patch version for fixes and small changes (for example, `1.0.1` to `1.0.2`).
- For a large feature update, carry the version forward by incrementing the minor version and resetting the patch version to zero (for example, `1.0.9` to `1.1.0`).
- Increment the major version and reset the other components for an incompatible or foundational rewrite (for example, `1.9.9` to `2.0.0`).
- Rebuild the plugin after changing the version so `plugin.yml` and the generated JAR filename contain the new version.
- Keep English documentation and messages before their Chinese equivalents.
- Update `CHANGELOG.md` for every release and update `README.md` plus `WRTPKILL.md` whenever behavior, configuration, compatibility, limits, or administration changes.
- Keep tracked configuration values as official defaults only. Never commit server-specific player names, coordinates, world names, tokens, logs, or runtime data.
- After verification, commit and push every completed update to the `main` branch of `https://github.com/Lazyzouo/WRTPKILL` so the release workflow publishes the matching GitHub Release and changelog automatically.
- Confirm that the GitHub Release contains exactly the manually uploaded assets `en.us.jar` and `zh.cn.jar`. GitHub's automatic source archives are platform-provided and excluded from this check.
- Keep `RELEASE_NOTICE.md` at the top of every GitHub Release. Update its official-source, compatibility, privacy, data-handling, and updater-network statements whenever those guarantees change.
