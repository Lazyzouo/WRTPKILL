package com.lazyz.wrtpkill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TPASafeRadiusTest {
    @Test
    void clampsUnsafeConfigurationValuesAndFormatsTheEffectiveRadius() {
        assertEquals(32.0, TPAExecutor.sanitizeRadius(Double.NaN));
        assertEquals(1.0, TPAExecutor.sanitizeRadius(0.0));
        assertEquals(1024.0, TPAExecutor.sanitizeRadius(5000.0));
        assertEquals("32", TPAExecutor.formatRadius(32.0));
        assertEquals("12.5", TPAExecutor.formatRadius(12.5));
    }

    @Test
    void everyGeneratedCandidateRemainsOutsideTheConfiguredRadius() {
        double configuredRadius = 48.5;
        var offsets = TPAExecutor.candidateOffsets(configuredRadius);

        assertEquals(32, offsets.size());
        assertTrue(offsets.stream().allMatch(offset ->
                Math.hypot(offset.x(), offset.z()) >= configuredRadius));
    }
}
