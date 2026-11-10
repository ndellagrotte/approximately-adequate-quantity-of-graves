package com.ndellagrotte.aaqg.soulbound;

/**
 * The arithmetic behind the soulbound death penalties. Deliberately free of Minecraft classes so it can be unit tested.
 */
public final class SoulboundMath {

    private SoulboundMath() {
    }

    /**
     * Applies the durability penalty to an item.
     * <p>
     * The penalty is {@code percent}% of the item's <em>maximum</em> durability, taken off its current durability. Minecraft
     * 1.12 stores damage taken rather than durability left, so this adds to the damage value instead. The item is never
     * destroyed: it is left with at least 1 durability, unless it already had less than that.
     *
     * @param damage    the item's current damage value (0 = undamaged)
     * @param maxDamage the item's maximum damage value
     * @param percent   the penalty, 0-100
     * @return the new damage value
     */
    public static int penalizedDamage(int damage, int maxDamage, int percent) {
        if (maxDamage <= 0 || percent <= 0) {
            return damage;
        }
        long penalty = Math.round((double) maxDamage * percent / 100.0);
        long penalized = Math.min(damage + penalty, maxDamage - 1L);
        return (int) Math.max(damage, penalized);
    }

    /**
     * The whole experience level a player keeps after the penalty. Partial progress towards the next level counts as a
     * fraction of a level, so level 1 with a 20% penalty becomes level 0 with 80% of the bar filled.
     */
    public static int keptLevel(int level, float progress, int percent) {
        return (int) Math.floor(keptLevels(level, progress, percent));
    }

    /**
     * The progress (0 inclusive to 1 exclusive) towards the next level a player keeps after the penalty.
     */
    public static float keptProgress(int level, float progress, int percent) {
        double kept = keptLevels(level, progress, percent);
        float fraction = (float) (kept - Math.floor(kept));
        return fraction >= 1.0f ? 0.0f : fraction;
    }

    /**
     * The total number of experience points that a level plus progress is worth, for the player's score-keeping total.
     */
    public static int totalExperience(int level, float progress) {
        long total = 0;
        for (int i = 0; i < level; i++) {
            total += xpBarCap(i);
        }
        total += Math.round((double) progress * xpBarCap(level));
        return (int) Math.min(total, Integer.MAX_VALUE);
    }

    private static double keptLevels(int level, float progress, int percent) {
        int clamped = Math.max(0, Math.min(100, percent));
        double levels = Math.max(0, level) + Math.max(0.0f, Math.min(1.0f, progress));
        return levels * (100 - clamped) / 100.0;
    }

    /** Same formula as vanilla's EntityPlayer#xpBarCap: the points needed to get from the given level to the next. */
    private static int xpBarCap(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        }
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }
}
