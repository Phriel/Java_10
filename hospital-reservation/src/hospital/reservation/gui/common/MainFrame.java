package hospital.reservation.gui.common;

import hospital.reservation.Main;
import hospital.reservation.controller.AdminController;
import hospital.reservation.controller.AuthController;
import hospital.reservation.controller.DoctorController; // DoctorController import
import hospital.reservation.controller.ReservationController;
import hospital.reservation.gui.auth.LoginDialog;
import hospital.reservation.gui.patient.PatientDashboardPanel;
import hospital.reservation.gui.admin.AdminDashboardPanel;
import hospital.reservation.gui.doctor.DoctorDashboardPanel; // DoctorDashboardPanel import
import hospital.reservation.model.Admin;
import hospital.reservation.model.Doctor; // Doctor model import
import hospital.reservation.model.Patient;
import hospital.reservation.model.User;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends Frame {
    private AuthController authController;
    private ReservationController reservationController;
    private AdminController adminController;
    private DoctorController doctorController; // DoctorController 필드 추가

    private Panel mainPanel; 
    private Label statusLabel; 

    // 생성자에 DoctorController 추가
    public MainFrame(AuthController auth, ReservationController res, AdminController adm, DoctorController docCtrl) {
        this.authController = auth;
        this.reservationController = res;
        this.adminController = adm;
        this.doctorController = docCtrl; // 초기화

        setTitle("병원 예약 시스템 (GUI)");
        setSize(800, 600);
        setLayout(new BorderLayout()); 

        statusLabel = new Label("로그인 해주세요.", Label.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        mainPanel = new Panel(new CardLayout()); 
        showPlaceholderPanel("병원 예약 시스템에 오신 것을 환영합니다.");
        add(mainPanel, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Main.saveAllData(); 
                dispose();          
                System.exit(0);     
            }
        });
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
    }

    private void showPlaceholderPanel(String message) {
        Panel placeholder = new Panel(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        Label welcomeLabel = new Label(message, Label.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        placeholder.add(welcomeLabel, gbc);
        switchPanel(placeholder, "placeholder");
    }
    
    public void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this, true, authController);
        loginDialog.setVisible(true); 

        User loggedInUser = loginDialog.getLoggedInUser();
        if (loggedInUser != null) {
            Main.currentUser = loggedInUser; 
            statusLabel.setText(loggedInUser.getName() + "님 ("+ loggedInUser.getUserType() +") 로그인 성공");
            
            if (loggedInUser instanceof Patient) {
                PatientDashboardPanel patientPanel = new PatientDashboardPanel((Patient) loggedInUser, this, reservationController);
                switchPanel(patientPanel, "patientDashboard");
            } else if (loggedInUser instanceof Admin) {
                AdminDashboardPanel adminPanel = new AdminDashboardPanel(
                    (Admin) loggedInUser, this, adminController
                );
                switchPanel(adminPanel, "adminDashboard");
            } else if (loggedInUser instanceof Doctor) { // 의사 로그인 처리 추가
                DoctorDashboardPanel doctorPanel = new DoctorDashboardPanel(
                    (Doctor) loggedInUser, this, doctorController
                );
                switchPanel(doctorPanel, "doctorDashboard");
            } else {
                showPlaceholderPanel("알 수 없는 사용자 유형입니다.");
            }
        } else {
            statusLabel.setText("로그인 실패 또는 취소되었습니다. 다시 시도해주세요.");
            showPlaceholderPanel("로그인이 필요합니다."); 
        }
    }

    public void showLoginScreenAndRetryLogin() {
        Main.currentUser = null;
        statusLabel.setText("로그아웃되었습니다. 다시 로그인해주세요.");
        showPlaceholderPanel("로그인이 필요합니다.");
        showLoginDialog(); 
    }

    public void switchPanel(Panel newPanel, String panelName) {
        mainPanel.removeAll(); 
        mainPanel.add(panelName, newPanel); 
        ((CardLayout)mainPanel.getLayout()).show(mainPanel, panelName); 
        mainPanel.revalidate(); 
        mainPanel.repaint();    
    }
    
    // Controller Getter들
    public AuthController getAuthController() { return authController; }
    public ReservationController getReservationController() { return reservationController; }
    public AdminController getAdminController() { return adminController; }
    public DoctorController getDoctorController() { return doctorController; } // DoctorController getter 추가
}