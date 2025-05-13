package hospital.reservation.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static boolean B_isValidDateFormat(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public static boolean B_isValidTimeFormat(String timeStr) { // 시간 형식 검증 추가
        try {
            LocalTime.parse(timeStr, TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean B_isFutureOrTodayDate(String dateStr) { // 오늘 포함 미래 날짜
        if (!B_isValidDateFormat(dateStr)) {
            return false;
        }
        try {
            LocalDate reservationDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            return !reservationDate.isBefore(LocalDate.now()); 
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public static String B_getCurrentDateString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    public static LocalDate B_parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null; // 또는 예외 throw
        }
    }

    public static LocalTime B_parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null; // 또는 예외 throw
        }
    }

    public static String B_getDayOfWeekKorean(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN); // "월", "화", ...
    }
}