package com.lazyz.wrtpkill;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public final class UpdateChecker {
    public static final String REPOSITORY_URL = "https://github.com/Lazyzouo/WRTPKILL";
    private static final URI LATEST_RELEASE_API = URI.create(
            "https://api.github.com/repos/Lazyzouo/WRTPKILL/releases/latest");
    private static final String STABLE_ASSET_NAME = "WRTPKILL.jar";

    private final WRTPKILL plugin;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public UpdateChecker(WRTPKILL plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        if (!plugin.getConfig().getBoolean("updater.enabled", true)) {
            log("Updater disabled by configuration.", "自动更新检查已由配置关闭。");
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(LATEST_RELEASE_API)
                    .timeout(Duration.ofSeconds(20))
                    .header("Accept", "application/vnd.github+json")
                    .header("User-Agent", "WRTPKILL/" + plugin.getDescription().getVersion())
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException("GitHub API returned HTTP " + response.statusCode());
            }

            JsonObject release = JsonParser.parseString(response.body()).getAsJsonObject();
            String latestVersion = cleanVersion(release.get("tag_name").getAsString());
            String currentVersion = cleanVersion(plugin.getDescription().getVersion());
            if (SemanticVersion.compare(latestVersion, currentVersion) <= 0) {
                log("WRTPKILL " + currentVersion + " is the latest version.",
                        "WRTPKILL " + currentVersion + " 已是最新版本。");
                return;
            }

            log("New version " + latestVersion + " is available.",
                    "发现新版本 " + latestVersion + "。");
            if (!plugin.getConfig().getBoolean("updater.auto-download", true)) {
                log("Automatic download is disabled. Download: " + REPOSITORY_URL + "/releases/latest",
                        "自动下载已关闭，请前往下载：" + REPOSITORY_URL + "/releases/latest");
                return;
            }

            URI assetUri = findStableAsset(release.getAsJsonArray("assets"));
            if (assetUri == null) throw new IOException("Release asset " + STABLE_ASSET_NAME + " is missing");
            download(assetUri, latestVersion);
        } catch (Exception exception) {
            plugin.getLogger().warning("Update check/download failed: " + exception.getMessage());
            plugin.getLogger().warning("更新检查或下载失败，可手动下载：" + REPOSITORY_URL + "/releases/latest");
        }
    }

    private URI findStableAsset(JsonArray assets) {
        if (assets == null) return null;
        for (JsonElement element : assets) {
            JsonObject asset = element.getAsJsonObject();
            if (STABLE_ASSET_NAME.equals(asset.get("name").getAsString())) {
                return URI.create(asset.get("browser_download_url").getAsString());
            }
        }
        return null;
    }

    private void download(URI assetUri, String latestVersion) throws IOException, InterruptedException {
        Path updateDirectory = Bukkit.getUpdateFolderFile().toPath();
        Files.createDirectories(updateDirectory);
        Path temporaryFile = Files.createTempFile(updateDirectory, "wrtpkill-", ".download");

        try {
            HttpRequest request = HttpRequest.newBuilder(assetUri)
                    .timeout(Duration.ofMinutes(2))
                    .header("User-Agent", "WRTPKILL/" + plugin.getDescription().getVersion())
                    .GET()
                    .build();
            HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(temporaryFile));
            if (response.statusCode() < 200 || response.statusCode() >= 300 || Files.size(temporaryFile) == 0) {
                throw new IOException("Release download returned HTTP " + response.statusCode());
            }

            Path target = updateDirectory.resolve(STABLE_ASSET_NAME);
            Files.move(temporaryFile, target, StandardCopyOption.REPLACE_EXISTING);
            log("Downloaded WRTPKILL " + latestVersion + " to " + target + ". Restart to apply it.",
                    "已下载 WRTPKILL " + latestVersion + " 至 " + target + "，重启服务器后生效。");
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    private static String cleanVersion(String version) {
        if (version == null) return "0.0.0";
        return version.trim().replaceFirst("^[vV]", "");
    }

    private void log(String english, String chinese) {
        plugin.getLogger().info(plugin.getLanguageManager().isEnglish() ? english : chinese);
    }
}
