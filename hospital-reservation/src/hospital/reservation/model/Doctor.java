package hospital.reservation.model;

import hospital.reservation.util.DateUtil; // DateUtil 사용
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Doctor extends User {
    private String departmentName;
    private String availability;

    // 요일 매핑 (파싱용)
    private static final Map<String, DayOfWeek> KOREAN_DAY_TO_DAY_OF_WEEK = new HashMap<>();
    static {
        KOREAN_DAY_TO_DAY_OF_WEEK.put("월", DayOfWeek.MONDAY);
        KOREAN_DAY_TO_DAY_OF_WEEK.put("화", DayOfWeek.TUESDAY);
        KOREAN_DAY_TO_DAY_OF_WEEK.put("수", DayOfWeek.WEDNESDAY);
        KOREAN_DAY_TO_DAY_OF_WEEK.put("목", DayOfWeek.THURSDAY);
        KOREAN_DAY_TO_DAY_OF_WEEK.put("금", DayOfWeek.FRIDAY);
        KOREAN_DAY_TO_DAY_OF_WEEK.put("토", DayOfWeek.SATURDAY);
        KOREAN_DAY_TO_DAY_OF_WEEK.put("일", DayOfWeek.SUNDAY);
    }


    public Doctor(String doctorId, String username, String password, String name, String departmentName, String availability) {
        super(doctorId, username, password, name);
        this.departmentName = departmentName;
        this.availability = (availability == null || availability.trim().isEmpty()) ? "" : availability; // 빈 문자열로 초기화 가능
    }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    @Override
    public String getUserType() { return "DOCTOR"; }

    @Override
    public String toCsvString() {
        return String.join(",", getUserId(), getUsername(), getPassword(), getName(), departmentName, availability);
    }

    @Override
    public String toString() {
        return "의사 정보 [ID: " + getUserId() + ", 이름: " + getName() + ", 진료과: " + departmentName + ", 진료시간: " + availability + ']';
    }

    /**
     * 특정 날짜와 시간에 의사가 진료 가능한지 확인합니다.
     * availability 문자열 형식 예: "월 09:00-12:00,14:00-17:00;수 09:00-12:00", "매일 09:00-11:00;2025-05-20 휴진"
     * @param requestedDate 요청된 날짜
     * @param requestedTime 요청된 시간
     * @return 진료 가능하면 true, 아니면 false
     */
    public boolean isAvailable(LocalDate requestedDate, LocalTime requestedTime) {
        if (this.availability == null || this.availability.trim().isEmpty()) {
            return false; // 스케줄 정보가 없으면 예약 불가 처리 (또는 기본값 설정에 따라 다름)
        }

        String[] rules = this.availability.split(";"); // 각 규칙을 세미콜론으로 분리

        // 1. 특정 날짜 휴진 규칙 확인 (우선순위 높음)
        for (String rule : rules) {
            rule = rule.trim();
            if (rule.contains("휴진")) {
                String[] parts = rule.split("\\s+"); // 공백으로 분리
                if (parts.length == 2 && parts[1].equals("휴진")) {
                    LocalDate offDate = DateUtil.B_parseDate(parts[0]);
                    if (offDate != null && offDate.equals(requestedDate)) {
                        return false; // 해당 날짜 휴진
                    }
                }
            }
        }

        // 2. 요일별 또는 "매일" 근무 시간 규칙 확인
        DayOfWeek requestedDayOfWeek = requestedDate.getDayOfWeek();
        String requestedDayKorean = DateUtil.B_getDayOfWeekKorean(requestedDate); // "월", "화" 등

        boolean dailyRuleApplicable = false;
        boolean specificDayRuleFound = false;

        for (String rule : rules) {
            rule = rule.trim();
            if (rule.contains("휴진")) continue; // 휴진 규칙은 이미 처리

            String[] dayAndTimeParts = rule.split("\\s+", 2); // 첫 공백으로 요일(또는 매일)과 시간 부분을 분리
            if (dayAndTimeParts.length < 2) continue;

            String dayInfo = dayAndTimeParts[0];
            String timeSlotsStr = dayAndTimeParts[1];

            boolean isRuleForThisDay = false;
            if (dayInfo.equals("매일")) {
                isRuleForThisDay = true;
                dailyRuleApplicable = true;
            } else if (KOREAN_DAY_TO_DAY_OF_WEEK.getOrDefault(dayInfo, null) == requestedDayOfWeek) {
                isRuleForThisDay = true;
                specificDayRuleFound = true;
            }


            if (isRuleForThisDay) {
                String[] timeSlots = timeSlotsStr.split(","); // 09:00-12:00,14:00-17:00 같은 경우
                for (String slot : timeSlots) {
                    slot = slot.trim();
                    String[] times = slot.split("-");
                    if (times.length == 2) {
                        LocalTime startTime = DateUtil.B_parseTime(times[0]);
                        LocalTime endTime = DateUtil.B_parseTime(times[1]);

                        if (startTime != null && endTime != null) {
                            // 요청 시간이 시작 시간과 같거나 이후이고, 종료 시간보다 이전인지 확인
                            if (!requestedTime.isBefore(startTime) && requestedTime.isBefore(endTime)) {
                                return true; // 해당 시간 슬롯에 포함됨
                            }
                        }
                    }
                }
                // 특정 요일 규칙이 있고, 그 규칙에 시간이 맞지 않으면 더 이상 매일 규칙은 보지 않음
                if(specificDayRuleFound) return false;
            }
        }
        
        // 특정 요일 규칙이 없고 매일 규칙도 적용되지 않았거나, 매일 규칙은 있었으나 시간이 안 맞은 경우
        // (위 로직상 specificDayRuleFound=true이고 시간이 안맞으면 이미 false 반환됨)
        // dailyRuleApplicable이 true인데 여기까지 왔다는 것은 매일 규칙의 시간대에 맞지 않았다는 의미.
        if (dailyRuleApplicable && !specificDayRuleFound) {
             // 이미 매일 규칙의 시간대를 위에서 검사했으므로, 여기까지 왔다면 매일 규칙에도 안맞는 것.
            return false;
        }

        return false; // 어떤 규칙에도 해당되지 않으면 예약 불가
    }
}