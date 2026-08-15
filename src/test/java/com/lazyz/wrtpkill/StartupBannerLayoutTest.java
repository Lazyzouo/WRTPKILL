package com.lazyz.wrtpkill;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StartupBannerLayoutTest {
    @Test
    void officialBannerLinesShareOneCompleteRightBorder() {
        List<String> lines = List.of(
                StartupBannerLayout.border(),
                StartupBannerLayout.centeredLine(
                        "&b&lWRTPKILL TELEPORT MANAGEMENT v1.4.1"),
                StartupBannerLayout.centeredLine(
                        "&f&lTELEPORT & RESPAWN CONTROL &8/ &f&l传送与复活管理"),
                StartupBannerLayout.sectionDivider(),
                StartupBannerLayout.line("&fVersion / 版本 &8: &a1.4.1"),
                StartupBannerLayout.line("&fAuthor  / 作者 &8: &eLazyz"),
                StartupBannerLayout.line(
                        "&fTested  / 测试 &8: &aPaper & Folia 1.21.11"),
                StartupBannerLayout.line("&fLanguage/ 语言 &8: &bzh_CN"),
                StartupBannerLayout.line(
                        "&fGitHub         &8: &9https://github.com/Lazyzouo/WRTPKILL"),
                StartupBannerLayout.line(
                        "&aOpen source. &fNo telemetry or server-data upload."));

        for (String line : lines) {
            assertEquals(StartupBannerLayout.INNER_WIDTH + 2,
                    StartupBannerLayout.visibleWidth(line));
        }
        assertEquals(4, StartupBannerLayout.visibleWidth("&f\u7248\u672c"));

        for (int index = 0; index < lines.size(); index++) {
            String expectedSuffix = index == 0 || index == 3 ? "+" : "&3&l|";
            assertTrue(lines.get(index).endsWith(expectedSuffix));
        }
    }
}
