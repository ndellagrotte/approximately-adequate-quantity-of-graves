package com.ndellagrotte.aaqg.client.render;

/**
 * The arithmetic behind the grave glow, kept free of Minecraft classes so it can be unit tested.
 */
public final class GraveGlowMath {

    /** The glow never fades below this fraction of its full strength. */
    public static final float MIN_PULSE = 0.55f;
    /** The length of one fade-out-and-back cycle. */
    public static final long PULSE_PERIOD_MS = 2000L;

    private GraveGlowMath()
    {
    }

    /**
     * @param dx    distance from the player to the grave along x
     * @param dy    distance from the player to the grave along y
     * @param dz    distance from the player to the grave along z
     * @param range the glow range in blocks
     * @return whether a grave that far away is close enough to glow
     */
    public static boolean inRange(double dx, double dy, double dz, int range)
    {
        return dx * dx + dy * dy + dz * dz <= (double) range * range;
    }

    /**
     * @param timeMs any steadily increasing clock, in milliseconds
     * @return a strength multiplier that swings smoothly between {@link #MIN_PULSE} and 1
     */
    public static float pulse(long timeMs)
    {
        double phase = Math.floorMod(timeMs, PULSE_PERIOD_MS) / (double) PULSE_PERIOD_MS;
        double wave = 0.5 + 0.5 * Math.sin(phase * 2.0 * Math.PI);
        return (float) (MIN_PULSE + (1.0 - MIN_PULSE) * wave);
    }
}
