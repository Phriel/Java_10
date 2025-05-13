package hospital.reservation;

import hospital.reservation.controller.AdminController;
import hospital.reservation.controller.AuthController;
import hospital.reservation.controller.DoctorController;
import hospital.reservation.controller.ReservationController;
import hospital.reservation.dao.AdminDao;
import hospital.reservation.dao.DoctorDao;
import hospital.reservation.dao.PatientDao;
import hospital.reservation.dao.ReservationDao; // ReservationDao import 추가
import hospital.reservation.gui.common.AppGUILauncher; // GUI 실행기 import
import hospital.reservation.model.User;
import hospital.reservation.view.ConsoleView; // ConsoleView import 추가

public class Main {
    public static User currentUser = null;

    private static PatientDao patientDao;
    private static DoctorDao doctorDao;
    private static AdminDao adminDao;
    private static ReservationDao reservationDao; // ReservationDao 필드 선언

    private static AuthController authController;
    private static ReservationController reservationController;
    private static AdminController adminController;
    private static DoctorController doctorController; 

    public static void main(String[] args) {
        // 1. DAO 객체 초기화
        patientDao = new PatientDao();
        doctorDao = new DoctorDao();
        adminDao = new AdminDao();
        reservationDao = new ReservationDao(); // ReservationDao 객체 생성

        // 2. Controller 객체 초기화 및 DAO 주입
        authController = new AuthController(patientDao, adminDao, doctorDao, reservationDao); // ReservationDao 전달
        reservationController = new ReservationController(reservationDao, doctorDao);
        adminController = new AdminController(doctorDao, reservationDao);
        doctorController = new DoctorController(doctorDao, reservationDao); 

        // --- 실행 모드 선택 ---
        // true: GUI 모드, false: 콘솔 모드
        boolean useGuiMode = true; 

        if (useGuiMode) {
            System.out.println("병원 예약 시스템 (GUI 모드)을 시작합니다.");
            // AppGUILauncher.main(null); // AppGUILauncher가 main을 가지고 있다면 이렇게 호출
            // 또는 직접 생성해서 시작 (AppGUILauncher의 main이 static이 아니라면)
             try {
                 // GUI 코드는 일반적으로 Event Dispatch Thread에서 실행하는 것이 권장됩니다.
                 // javax.swing.SwingUtilities.invokeLater(() -> {
                 //     AppGUILauncher launcher = new AppGUILauncher(); // 만약 main이 static이 아니라면
                 //     launcher.startGUI(); // startGUI() 같은 메소드가 AppGUILauncher에 있다고 가정
                 // });
                 // AWT의 경우에도 Thread 고려 가능하지만, 여기서는 직접 호출
                 AppGUILauncher.main(null); // AppGUILauncher의 main이 static이라고 가정
             } catch (Exception e) {
                 System.err.println("! GUI 시작 중 오류 발생: " + e.getMessage());
                 e.printStackTrace();
             }
        } else {
            System.out.println("병원 예약 시스템 (콘솔 모드)을 시작합니다.");
            ConsoleView consoleView = new ConsoleView(authController, reservationController, adminController);
            consoleView.start();
        }
    }

    // Controller Getters
    public static AuthController getAuthController() { return authController; }
    public static ReservationController getReservationController() { return reservationController; }
    public static AdminController getAdminController() { return adminController; }
    public static DoctorController getDoctorController() { return doctorController; } 
    
    // DAO Getters (필요시 MainFrame 등에서 직접 접근하지 않고 Controller를 통하는 것이 좋음)
    public static PatientDao getPatientDao() { return patientDao; }
    public static DoctorDao getDoctorDao() { return doctorDao; }
    public static AdminDao getAdminDao() { return adminDao; }
    public static ReservationDao getReservationDao() { return reservationDao; }

    public static void saveAllData() {
        System.out.println("데이터 저장 중...");
        if (patientDao != null) patientDao.saveData();
        if (doctorDao != null) doctorDao.saveData(); 
        if (adminDao != null) adminDao.saveData();
        if (reservationDao != null) reservationDao.saveData();
        System.out.println(">> 모든 데이터 저장이 완료되었습니다.");
    }
}