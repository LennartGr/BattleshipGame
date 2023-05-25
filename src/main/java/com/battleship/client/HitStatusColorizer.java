package com.battleship.client;

import java.util.Map;

/**
 * The HitStatusColorizer class provides mapping of hit statuses to their
 * respective colors.
 * It also includes an explanation of the color codes used for hit statuses.
 */
public class HitStatusColorizer {

    private static Map<HitStatus, String> hitStatusMap = Map.of(
            HitStatus.NOT_ATTTACKED, "default",
            HitStatus.HIT, "yellow",
            HitStatus.DESTROYED, "red",
            HitStatus.MISSED, "blue");

    /**
     * Explanation of the color codes used for hit statuses.
     */
    public static final String EXPLICATION = String.format("[%s --- %s --- %s]",
            JansiHelper.colorize("yellow: ship hit", "yellow"),
            JansiHelper.colorize("red: ship destroyed", "red"),
            JansiHelper.colorize("blue: missed attack", "blue"));

    /**
     * Retrieves the color string associated with the specified hit status.
     *
     * @param hitStatus The hit status for which to retrieve the color string.
     * @return The color string associated with the hit status.
     */
    public static String getColorString(HitStatus hitStatus) {
        return hitStatusMap.get(hitStatus);
    }
}
