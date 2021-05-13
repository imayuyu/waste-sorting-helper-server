package com.charliechiang.wastesortinghelperserver.config;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;

public class CreditUpdateSettings {
    public static final double WEIGHT_THRESHOLD_KG = 10;
    public static final TemporalAdjuster PREVIOUS_TIME_INTERVAL_ADJUSTER = TemporalAdjusters.previous(DayOfWeek.SUNDAY);
    public static final TemporalAdjuster NEXT_TIME_INTERVAL_ADJUSTER = TemporalAdjusters.next(DayOfWeek.SUNDAY);

    public static final long RANKING_UPDATE_DELAY = 1;
    public static final TemporalUnit RANKING_UPDATE_DELAY_UNIT = ChronoUnit.MINUTES;
}
