package hospital.reservation.model;

public class Patient extends User {
    private String ssn; // 주민등록번호
    private String phoneNumber;

    public Patient(String patientId, String username, String password, String name, String ssn, String phoneNumber) {
        super(patientId, username, password, name);
        this.ssn = ssn;
        this.phoneNumber = phoneNumber;
    }

    public String getSsn() {
        return ssn;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getUserType() {
        return "PATIENT";
    }

    @Override
    public String toCsvString() {
        return String.join(",", userId, username, password, name, ssn, phoneNumber);
    }

    @Override
    public String toString() {
        return "환자 정보 [" + super.toString() + ", 연락처: " + phoneNumber + "]";
    }
}