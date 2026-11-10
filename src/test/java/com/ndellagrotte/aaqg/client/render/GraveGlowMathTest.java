package com.ndellagrotte.aaqg.client.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GraveGlowMathTest {

    @Test
    void graveExactlyAtTheRangeGlows() {
        assertTrue(GraveGlowMath.inRange(50, 0, 0, 50));
        assertTrue(GraveGlowMath.inRange(0, 0, -50, 50));
    }

    @Test
    void graveJustPastTheRangeDoesNotGlow() {
        assertFalse(GraveGlowMath.inRange(50.01, 0, 0, 50));
        assertFalse(GraveGlowMath.inRange(0, 0, -50.01, 50));
    }

    @Test
    void rangeIsMeasuredInAStraightLine() {
        // 30-40-50 triangle: exactly 50 blocks away
        assertTrue(GraveGlowMath.inRange(30, 0, 40, 50));
        // within 50 blocks on every axis, but about 61 blocks away
        assertFalse(GraveGlowMath.inRange(35, 35, 35, 50));
    }

    @Test
    void heightCountsTowardsTheRange() {
        assertTrue(GraveGlowMath.inRange(0, -50, 0, 50));
        assertFalse(GraveGlowMath.inRange(0, -51, 0, 50));
    }

    @Test
    void largeRangesDoNotOverflow() {
        assertTrue(GraveGlowMath.inRange(60000, 0, 0, 100000));
    }

    @Test
    void pulseStaysBetweenItsMinimumAndFullStrength() {
        for (long time = -5000; time <= 5000; time += 7)
        {
            float pulse = GraveGlowMath.pulse(time);
            assertTrue(pulse >= GraveGlowMath.MIN_PULSE - 1e-6f, "too dim at " + time);
            assertTrue(pulse <= 1.0f + 1e-6f, "too bright at " + time);
        }
    }

    @Test
    void pulseReachesBothEnds() {
        assertEquals(1.0f, GraveGlowMath.pulse(GraveGlowMath.PULSE_PERIOD_MS / 4), 1e-6f);
        assertEquals(GraveGlowMath.MIN_PULSE, GraveGlowMath.pulse(GraveGlowMath.PULSE_PERIOD_MS * 3 / 4), 1e-6f);
    }

    @Test
    void pulseRepeatsEveryPeriod() {
        assertEquals(GraveGlowMath.pulse(300), GraveGlowMath.pulse(300 + GraveGlowMath.PULSE_PERIOD_MS), 1e-6f);
    }
}
