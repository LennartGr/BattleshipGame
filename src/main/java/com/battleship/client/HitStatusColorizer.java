package com.battleship.client;

import java.util.Map;

public class HitStatusColorizer {

    private static Map<HitStatus, String> hitStatusMap = Map.of(
        HitStatus.NOT_ATTTACKED, "bg_default",
        HitStatus.HIT, "bg_yellow",
        HitStatus.DESTROYED, "bg_red",
        HitStatus.MISSED, "bg_blue"
    );

    public static final String EXPLICATION = "[yellow: ship hit --- red: ship destroyed --- blue: missed attack]";
    
    public static String getColorString(HitStatus hitStatus) {
        return hitStatusMap.get(hitStatus);
    }

}
