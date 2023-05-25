package com.battleship.client;

import java.util.Map;

public class HitStatusColorizer {

    private static Map<HitStatus, String> hitStatusMap = Map.of(
            HitStatus.NOT_ATTTACKED, "default",
            HitStatus.HIT, "yellow",
            HitStatus.DESTROYED, "red",
            HitStatus.MISSED, "blue");

    public static final String EXPLICATION = String.format("[%s --- %s --- %s]",
            JansiHelper.colorize("yellow: ship hit", "yellow"),
            JansiHelper.colorize("red: ship destroyed", "red"),
            JansiHelper.colorize("blue: missed attack", "blue"));

    public static String getColorString(HitStatus hitStatus) {
        return hitStatusMap.get(hitStatus);
    }

}
