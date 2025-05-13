package hospital.reservation.util;

import hospital.reservation.exception.InvalidInputException;

public class ValidationUtil {

    public static void A_validateNotEmpty(String input, String fieldName) throws InvalidInputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + "은(는) 비워둘 수 없습니다.");
        }
    }

    public static void A_validateSsnFormat(String ssn) throws InvalidInputException {
        A_validateNotEmpty(ssn, "주민등록번호");
        if (!ssn.matches("\\d{6}-\\d{7}")) {
            throw new InvalidInputException("주민등록번호 형식이 올바르지 않습니다. (예: 900101-1234567)");
        }
    }

    public static void A_validatePhoneNumberFormat(String phoneNumber) throws InvalidInputException {
        A_validateNotEmpty(phoneNumber, "연락처");
        if (!phoneNumber.matches("\\d{3}-\\d{3,4}-\\d{4}")) {
            throw new InvalidInputException("연락처 형식이 올바르지 않습니다. (예: 010-1234-5678)");
        }
    }

    public static void A_validateDateFormat(String date) throws InvalidInputException {
        A_validateNotEmpty(date, "날짜");
        if (!DateUtil.B_isValidDateFormat(date)) { // DateUtil에 의존
            throw new InvalidInputException("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD)");
        }
    }

    public static void A_validateTimeFormat(String time) throws InvalidInputException {
        A_validateNotEmpty(time, "시간");
        if (!time.matches("^([01]\\d|2[0-3]):([0-5]\\d)$")) {
            throw new InvalidInputException("시간 형식이 올바르지 않습니다. (HH:MM, 예: 09:00)");
        }
    }
}