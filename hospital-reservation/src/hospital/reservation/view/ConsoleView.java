package hospital.reservation.view;

import hospital.reservation.Main;
import hospital.reservation.controller.AdminController;
import hospital.reservation.controller.AuthController;
import hospital.reservation.controller.ReservationController;
import hospital.reservation.exception.*;
import hospital.reservation.model.*;
import hospital.reservation.util.InputUtil;
import java.util.List;

public class ConsoleView {
    private final AuthController authController;
    private final ReservationController reservationController;
    private final AdminController adminController;

    public ConsoleView(AuthController authController, ReservationController reservationController, AdminController adminController) {
        this.authController = authController;
        this.reservationController = reservationController;
        this.adminController = adminController;
    }

    public void start() {
        while (true) {
            if (Main.currentUser == null) {
                showMainMenu();
            } else if (Main.currentUser instanceof Patient) {
                showPatientMenu((Patient) Main.currentUser);
            } else if (Main.currentUser instanceof Admin) {
                showAdminMenu((Admin) Main.currentUser);
            } else if (Main.currentUser instanceof Doctor) {
                // Doctor 메뉴는 여기서 구현하지 않았지만, 확장 가능하다면
                showDoctorMenu((Doctor) Main.currentUser);
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.println("│      병원 예약 시스템 (콘솔)      │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│ 1. 로그인                         │");
        System.out.println("│ 2. 환자 회원가입                  │");
        System.out.println("│ 0. 종료                           │");
        System.out.println("└───────────────────────────────────┘");
        int choice = InputUtil.getInt("메뉴 선택 ▶ ");

        switch (choice) {
            case 1: handleLogin(); break;
            case 2: handlePatientRegister(); break;
            case 0: 
                System.out.println(">> 시스템을 종료합니다. 모든 데이터를 저장합니다.");
                Main.saveAllData();
                InputUtil.closeScanner();
                System.exit(0);
                break;
            default: System.out.println("!오류: 잘못된 메뉴 번호입니다.");
        }
    }

    private void showPatientMenu(Patient patient) {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.printf ("│ %-12s님 환영합니다!     │\n", patient.getName());
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│            <환자 메뉴>            │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│ 1. 진료과/의사 조회               │");
        System.out.println("│ 2. 진료 예약하기                  │");
        System.out.println("│ 3. 나의 예약 조회                 │");
        System.out.println("│ 4. 예약 취소                      │");
        System.out.println("│ 5. 회원탈퇴                       │"); 
        System.out.println("│ 0. 로그아웃                       │");
        System.out.println("└───────────────────────────────────┘");
        int choice = InputUtil.getInt("메뉴 선택 ▶ ");

        switch (choice) {
            case 1: handleViewDoctorsAndDepartmentsForPatient(); break;
            case 2: handleMakeReservation(patient); break;
            case 3: handleViewMyReservations(patient); break;
            case 4: handleCancelReservation(patient); break;
            case 5: handleWithdrawPatient(patient); break; 
            case 0: Main.currentUser = null; System.out.println(">> 로그아웃 되었습니다."); break;
            default: System.out.println("!오류: 잘못된 메뉴 번호입니다.");
        }
    }

    private void showAdminMenu(Admin admin) {
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.printf ("│ 관리자: %-10s님 접속중      │\n", admin.getName());
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│           <관리자 메뉴>           │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│ 1. 의사 추가                      │");
        System.out.println("│ 2. 전체 의사 목록 조회            │");
        System.out.println("│ 3. 전체 예약 목록 조회            │");
        System.out.println("│ 0. 로그아웃                       │");
        System.out.println("└───────────────────────────────────┘");
        int choice = InputUtil.getInt("메뉴 선택 ▶ ");

        switch (choice) {
            case 1: handleAddDoctor(); break;
            case 2: handleViewAllDoctorsAdmin(); break;
            case 3: handleViewAllReservationsAdmin(); break;
            case 0: Main.currentUser = null; System.out.println(">> 로그아웃 되었습니다."); break;
            default: System.out.println("!오류: 잘못된 메뉴 번호입니다.");
        }
    }
    
    private void showDoctorMenu(Doctor doctor) { // 의사 메뉴 예시 (기능은 미구현)
        System.out.println("\n┌───────────────────────────────────┐");
        System.out.printf ("│ 의사: %-12s님 환영합니다!   │\n", doctor.getName());
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│            <의사 메뉴>            │");
        System.out.println("├───────────────────────────────────┤");
        System.out.println("│ 1. 나의 스케줄 보기 (구현 안됨)   │");
        System.out.println("│ 2. 진료 시간 설정 (구현 안됨)   │");
        System.out.println("│ 0. 로그아웃                       │");
        System.out.println("└───────────────────────────────────┘");
        int choice = InputUtil.getInt("메뉴 선택 ▶ ");
        switch (choice) {
            case 0: Main.currentUser = null; System.out.println(">> 로그아웃 되었습니다."); break;
            default: System.out.println("!오류: 잘못된 메뉴 번호 또는 미구현 기능입니다.");
        }
    }


    private void handleLogin() {
        System.out.println("\n--- 로그인 ---");
        String username = InputUtil.getString("사용자 아이디: ");
        String password = InputUtil.getString("비밀번호: ");
        try {
            User user = authController.login(username, password);
            Main.currentUser = user;
            System.out.println(">> " + user.getName() + "님, 로그인 성공! (" + user.getUserType() + " 모드)");
        } catch (AuthenticationException | InvalidInputException e) {
            System.out.println("! 로그인 오류: " + e.getMessage());
        }
    }

    private void handlePatientRegister() {
        System.out.println("\n--- 환자 회원가입 ---");
        String username = InputUtil.getString("사용할 아이디: ");
        String password = InputUtil.getString("비밀번호: ");
        String name = InputUtil.getString("이름: ");
        String ssn = InputUtil.getString("주민등록번호 (예: 900101-1234567): ");
        String phoneNumber = InputUtil.getString("연락처 (예: 010-1234-5678): ");
        try {
            authController.registerPatient(username, password, name, ssn, phoneNumber);
            System.out.println(">> 회원가입 성공! 이제 로그인해주세요.");
        } catch (InvalidInputException | IllegalArgumentException e) {
            System.out.println("! 회원가입 오류: " + e.getMessage());
        }
    }

    private void handleViewDoctorsAndDepartmentsForPatient() {
        System.out.println("\n--- 진료과 / 의사 조회 (환자용) ---");
        displayDepartmentsAndDoctors();
    }
    
    private void displayDepartmentsAndDoctors() { 
        List<String> departments = reservationController.getAllDepartmentNames();
        if (departments.isEmpty()) {
            System.out.println("등록된 진료과 정보가 없습니다.");
            return;
        }
        System.out.println("<조회할 진료과를 선택하세요>");
        for (int i = 0; i < departments.size(); i++) {
            System.out.println((i + 1) + ". " + departments.get(i));
        }
        System.out.println("0. 전체 의사 조회");
        
        int deptChoiceNum = InputUtil.getInt("진료과 번호 선택 (전체는 0): ");
        String selectedDepartment;

        if(deptChoiceNum == 0) {
            selectedDepartment = "전체";
        } else if (deptChoiceNum > 0 && deptChoiceNum <= departments.size()) {
            selectedDepartment = departments.get(deptChoiceNum - 1);
        } else {
            System.out.println("!오류: 잘못된 번호입니다.");
            return;
        }
        
        List<Doctor> doctors = reservationController.getDoctorsByDepartment(selectedDepartment);
        displayDoctorList(doctors, "--- " + selectedDepartment + " 의사 목록 ---");
    }
    
    private void displayDoctorList(List<Doctor> doctors, String title) {
        if (doctors.isEmpty()) {
            System.out.println(">> 해당 조건의 의사가 없습니다.");
        } else {
            System.out.println("\n" + title);
            System.out.println("-----------------------------------------------------");
            System.out.printf("%-6s | %-10s | %-15s\n", "의사ID", "이름", "진료과");
            System.out.println("-----------------------------------------------------");
            for (Doctor doc : doctors) {
                System.out.printf("%-6s | %-10s | %-15s\n", doc.getUserId(), doc.getName(), doc.getDepartmentName());
            }
            System.out.println("-----------------------------------------------------");
        }
    }

    private void handleMakeReservation(Patient patient) {
        System.out.println("\n--- 진료 예약하기 ---");
        displayDepartmentsAndDoctors(); 

        String doctorId = InputUtil.getString("예약할 의사의 ID를 입력하세요: ");
        String dateStr = InputUtil.getString("예약 날짜 (YYYY-MM-DD 형식): ");
        String timeStr = InputUtil.getString("예약 시간 (HH:MM 형식, 예: 09:00, 14:30): ");

        try {
            reservationController.makeReservation(patient, doctorId, dateStr, timeStr);
        } catch (InvalidInputException | RecordNotFoundException e) {
            System.out.println("! 예약 오류: " + e.getMessage());
        }
    }

    private void handleViewMyReservations(Patient patient) {
        System.out.println("\n--- 나의 예약 조회 ---");
        List<Reservation> reservations = reservationController.getReservationsForPatient(patient);
        displayReservationList(reservations, "--- " + patient.getName() + "님의 예약 목록 ---");
    }
    
    private void displayReservationList(List<Reservation> reservations, String title) {
         if (reservations.isEmpty()) {
            System.out.println(">> 예약 내역이 없습니다.");
        } else {
            System.out.println("\n" + title);
            System.out.println("----------------------------------------------------------------------------------");
            System.out.printf("%-10s | %-10s | %-10s | %-15s | %-12s | %-5s\n", "예약번호", "환자ID", "의사ID", "진료과", "예약일", "시간");
            System.out.println("----------------------------------------------------------------------------------");
            for (Reservation res : reservations) {
                System.out.printf("%-10s | %-10s | %-10s | %-15s | %-12s | %-5s\n",
                        res.getReservationId(), res.getPatientId(), res.getDoctorId(),
                        res.getDepartmentName(), res.getReservationDate(), res.getReservationTime());
            }
            System.out.println("----------------------------------------------------------------------------------");
        }
    }

    private void handleCancelReservation(Patient patient) {
        System.out.println("\n--- 예약 취소 ---");
        handleViewMyReservations(patient); 
        List<Reservation> reservations = reservationController.getReservationsForPatient(patient);
        if(reservations.isEmpty()){ return; }

        String reservationIdToCancel = InputUtil.getString("취소할 예약 번호를 입력하세요: ");
        try {
            reservationController.cancelReservation(patient, reservationIdToCancel);
        } catch (InvalidInputException | RecordNotFoundException e) {
            System.out.println("! 예약 취소 오류: " + e.getMessage());
        }
    }
    
    private void handleWithdrawPatient(Patient patient) {
        System.out.println("\n--- 회원탈퇴 ---");
        String confirm = InputUtil.getString("정말로 회원탈퇴를 하시겠습니까? 모든 예약 정보도 함께 삭제됩니다. (y/n): ");
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println(">> 회원탈퇴가 취소되었습니다.");
            return;
        }
        String password = InputUtil.getString("계정 보호를 위해 비밀번호를 다시 입력해주세요: ");
        try {
            boolean success = authController.withdrawPatientAccount(patient.getUserId(), password);
            if (success) {
                System.out.println(">> 회원탈퇴가 성공적으로 처리되었습니다. 안녕히 가십시오.");
                Main.currentUser = null; // 자동 로그아웃
            } 
            // AuthController에서 예외를 던지므로, else 블록은 거의 실행되지 않음
        } catch (AuthenticationException | RecordNotFoundException | InvalidInputException e) {
            System.out.println("! 회원탈퇴 오류: " + e.getMessage());
        }
    }

    // --- Admin Menu Handlers ---
    private void handleAddDoctor() {
        System.out.println("\n--- 의사 추가 ---");
        String username = InputUtil.getString("의사 사용자명 (로그인용): ");
        String password = InputUtil.getString("의사 비밀번호 (로그인용): ");
        String name = InputUtil.getString("의사 이름: ");
        String departmentName = InputUtil.getString("진료과: ");
        
        try {
            adminController.addDoctor(username, password, name, departmentName);
            System.out.println(">> 의사 정보가 성공적으로 추가되었습니다.");
        } catch (InvalidInputException | IllegalArgumentException e) {
             System.out.println("! 의사 추가 오류: " + e.getMessage());
        }
    }

    private void handleViewAllDoctorsAdmin() {
        System.out.println("\n--- 전체 의사 목록 조회 (관리자) ---");
        List<Doctor> doctors = adminController.getAllDoctors();
        displayDoctorList(doctors, "--- 전체 의사 목록 ---");
    }

    private void handleViewAllReservationsAdmin() {
        System.out.println("\n--- 전체 예약 목록 조회 (관리자) ---");
        List<Reservation> reservations = adminController.getAllReservations();
        displayReservationList(reservations, "--- 전체 예약 목록 ---");
    }
}