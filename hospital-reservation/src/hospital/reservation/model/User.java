package hospital.reservation.model;

public abstract class User {
    protected String userId;
    protected String username;
    protected String password;
    protected String name;

    public User(String userId, String username, String password, String name) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
    }

    // Getter 메소드들
    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    // Setter 메소드들 (필요에 따라)
    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // 각 사용자 유형을 식별하기 위한 추상 메소드
    public abstract String getUserType();

    // CSV 저장을 위한 추상 메소드
    public abstract String toCsvString();

    @Override
    public String toString() {
        return "ID: " + userId + ", 이름: " + name + ", 사용자명: " + username;
    }
}