package de.zebrajaeger.sphere2cube.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String durationToHumanString(Long durationMs) {

        if (durationMs == null) {
            return "na";
        }

        long days = TimeUnit.MILLISECONDS.toDays(durationMs);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60;
        long milliseconds = durationMs % 1000;

        StringBuilder sb = new StringBuilder();
        boolean forcePrint = false;

        if (days > 0) {
            sb.append(String.format("%d Days", days));
            forcePrint = true;
        }

        if (forcePrint || hours > 0) {
            if (forcePrint) {
                sb.append(" ");
            }
            sb.append(String.format("%d Hours", hours));
            forcePrint = true;
        }

        if (forcePrint || minutes > 0) {
            if (forcePrint) {
                sb.append(" ");
            }
            sb.append(String.format("%d Minutes", minutes));
            forcePrint = true;
        }

        if (forcePrint) {
            sb.append(" ");
        }
        sb.append(String.format("%d.%03d Seconds", seconds, milliseconds));

        return sb.toString();
    }

    public static String toIsoTime(Long timestamp) {
        return timestamp == null ? "na" : DateTimeFormatter.ISO_TIME.format(Instant.ofEpochMilli(timestamp));
    }
}
