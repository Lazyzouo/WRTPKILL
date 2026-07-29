package com.lazyz.wrtpkill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateCheckerTest {
    @Test
    void buildsImmutableEnglishAssetNameFromReleaseVersion() {
        assertEquals("WRTPKILL-1.2.0-en.us.jar", UpdateChecker.releaseAssetName("v1.2.0", true));
    }

    @Test
    void buildsImmutableChineseAssetNameFromReleaseVersion() {
        assertEquals("WRTPKILL-1.2.0-zh.cn.jar", UpdateChecker.releaseAssetName("1.2.0", false));
    }
}
