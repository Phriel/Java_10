package hospital.reservation.model;

public class Admin extends User { // User 클래스를 상속합니다.
    // Admin 클래스 고유의 필드가 없다면 생성자만 User 클래스에 맞게 정의합니다.
    public Admin(String adminId, String username, String password, String name) {
        super(adminId, username, password, name); // 부모 User 클래스의 생성자 호출
    }

    @Override
    public String getUserType() {
        return "ADMIN"; // User 클래스의 추상 메소드 구현
    }

    @Override
    public String toCsvString() {
        // User 클래스의 getter를 사용하여 CSV 문자열 생성
        return String.join(",", getUserId(), getUsername(), getPassword(), getName());
    }

    @Override
    public String toString() {
        // User 클래스의 getter를 사용하여 문자열 생성
        return "관리자 정보 [ID: " + getUserId() + ", 이름: " + getName() + ", 사용자명: " + getUsername() + "]";
    }
}