package com.charliechiang.wastesortinghelperserver.config;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

public class CreditSettings {
    public final double WEIGHT_THRESHOLD_PER_TIME_INTERVAL = 10;
    public final TemporalAdjuster PREVIOUS_TIME_INTERVAL_ADJUSTER = TemporalAdjusters.previous(DayOfWeek.SUNDAY);
    public final TemporalAdjuster NEXT_TIME_INTERVAL_ADJUSTER = TemporalAdjusters.next(DayOfWeek.SUNDAY);
}
