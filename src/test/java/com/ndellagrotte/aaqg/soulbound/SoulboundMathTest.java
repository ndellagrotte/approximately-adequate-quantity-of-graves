package com.ndellagrotte.aaqg.soulbound;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SoulboundMathTest {

    // A diamond sword has a maximum damage value of 1561.
    private static final int DIAMOND = 1561;

    @Test
    void durabilityPenaltyIsAPercentageOfMaximumDurability() {
        // 10% of 1561 is 156.1, so an undamaged sword takes 156 damage
        assertEquals(156, SoulboundMath.penalizedDamage(0, DIAMOND, 10));
        // the penalty is based on the maximum, not on what is left
        assertEquals(1156, SoulboundMath.penalizedDamage(1000, DIAMOND, 10));
    }

    @Test
    void durabilityPenaltyLeavesOneDurability() {
        assertEquals(DIAMOND - 1, SoulboundMath.penalizedDamage(1500, DIAMOND, 10));
        assertEquals(DIAMOND - 1, SoulboundMath.penalizedDamage(0, DIAMOND, 100));
    }

    @Test
    void durabilityPenaltyNeverRepairsAnItem() {
        // already on its last point of durability: left alone rather than "healed" up to the floor
        assertEquals(DIAMOND, SoulboundMath.penalizedDamage(DIAMOND, DIAMOND, 10));
        assertEquals(DIAMOND - 1, SoulboundMath.penalizedDamage(DIAMOND - 1, DIAMOND, 10));
    }

    @Test
    void zeroDurabilityPenaltyChangesNothing() {
        assertEquals(400, SoulboundMath.penalizedDamage(400, DIAMOND, 0));
        assertEquals(0, SoulboundMath.penalizedDamage(0, 0, 10));
    }

    @Test
    void expPenaltyRemovesAPercentageOfLevels() {
        assertEquals(24, SoulboundMath.keptLevel(30, 0.0f, 20));
        assertEquals(0.0f, SoulboundMath.keptProgress(30, 0.0f, 20), 1.0e-6f);
    }

    @Test
    void expPenaltyCountsBarProgressAsPartOfALevel() {
        // 30.5 levels * 0.8 = 24.4
        assertEquals(24, SoulboundMath.keptLevel(30, 0.5f, 20));
        assertEquals(0.4f, SoulboundMath.keptProgress(30, 0.5f, 20), 1.0e-4f);
        // one level * 0.8 = 0.8 of the first bar
        assertEquals(0, SoulboundMath.keptLevel(1, 0.0f, 20));
        assertEquals(0.8f, SoulboundMath.keptProgress(1, 0.0f, 20), 1.0e-4f);
    }

    @Test
    void expPenaltyExtremes() {
        assertEquals(30, SoulboundMath.keptLevel(30, 0.25f, 0));
        assertEquals(0.25f, SoulboundMath.keptProgress(30, 0.25f, 0), 1.0e-6f);
        assertEquals(0, SoulboundMath.keptLevel(30, 0.25f, 100));
        assertEquals(0.0f, SoulboundMath.keptProgress(30, 0.25f, 100), 1.0e-6f);
    }

    @Test
    void totalExperienceMatchesVanillaLevelCosts() {
        assertEquals(0, SoulboundMath.totalExperience(0, 0.0f));
        // levels 0 and 1 cost 7 and 9 points
        assertEquals(16, SoulboundMath.totalExperience(2, 0.0f));
        // vanilla: 1395 points to reach level 30
        assertEquals(1395, SoulboundMath.totalExperience(30, 0.0f));
        // half of the 112 points level 30 needs
        assertEquals(1451, SoulboundMath.totalExperience(30, 0.5f));
    }
}
