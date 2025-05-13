package hospital.reservation.model;

public class Reservation {
    private String reservationId;
    private String patientId;
    private String doctorId;
    private String departmentName;
    private String reservationDate; // YYYY-MM-DD
    private String reservationTime; // HH:MM

    public Reservation(String reservationId, String patientId, String doctorId, String departmentName, String reservationDate, String reservationTime) {
        this.reservationId = reservationId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.departmentName = departmentName;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
    }

    // Getters
    public String getReservationId() { return reservationId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getDepartmentName() { return departmentName; }
    public String getReservationDate() { return reservationDate; }
    public String getReservationTime() { return reservationTime; }

    public String toCsvString() {
        return String.join(",", reservationId, patientId, doctorId, departmentName, reservationDate, reservationTime);
    }

    @Override
    public String toString() {
        return "예약 정보 [예약번호: " + reservationId +
               ", 환자ID: " + patientId +
               ", 의사ID: " + doctorId +
               ", 진료과: " + departmentName +
               ", 날짜: " + reservationDate +
               ", 시간: " + reservationTime +
               ']';
    }
}
