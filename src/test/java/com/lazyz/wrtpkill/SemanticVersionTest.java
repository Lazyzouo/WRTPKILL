package com.lazyz.wrtpkill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemanticVersionTest {
    @Test
    void comparesReleaseVersionsNumerically() {
        assertTrue(SemanticVersion.compare("1.10.0", "1.9.9") > 0);
        assertTrue(SemanticVersion.compare("v2.0.0", "1.99.99") > 0);
        assertEquals(0, SemanticVersion.compare("1.1", "1.1.0"));
    }

    @Test
    void ignoresPrereleaseAndBuildSuffixesForUpdateOrdering() {
        assertEquals(0, SemanticVersion.compare("1.1.0-beta.1", "1.1.0"));
        assertEquals(0, SemanticVersion.compare("1.1.0+build.4", "v1.1.0"));
    }
}
