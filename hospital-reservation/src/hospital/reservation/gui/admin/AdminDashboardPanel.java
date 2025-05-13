package hospital.reservation.gui.admin;

import hospital.reservation.controller.AdminController;
import hospital.reservation.gui.common.MainFrame;
import hospital.reservation.model.Admin;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboardPanel extends Panel implements ActionListener {
    private Admin admin; // 환영 메시지에 사용
    private MainFrame mainFrame;
    private AdminController adminController;
    // private DoctorDao doctorDao; // 제거
    // private ReservationDao reservationDao; // 제거

    private Button btnAddDoctor;
    private Button btnViewAllDoctors;
    private Button btnViewAllReservations;
    private Button btnLogout;

    // 생성자에서 DoctorDao, ReservationDao 파라미터 제거
    public AdminDashboardPanel(Admin admin, MainFrame mainFrame, AdminController adminController) {
        this.admin = admin;
        this.mainFrame = mainFrame;
        this.adminController = adminController;
        // this.doctorDao = doctorDao; // 제거
        // this.reservationDao = reservationDao; // 제거

        setLayout(new BorderLayout(10, 10));

        Panel topPanel = new Panel(new BorderLayout());
        Label lblWelcome = new Label("관리자: " + this.admin.getName() + "님, 환영합니다.", Label.CENTER); // this.admin 사용
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(lblWelcome, BorderLayout.CENTER);

        btnLogout = new Button("로그아웃");
        btnLogout.addActionListener(this);
        Panel logoutButtonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        logoutButtonPanel.add(btnLogout);
        topPanel.add(logoutButtonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        Panel buttonPanel = new Panel(new GridLayout(3, 1, 10, 10));
        btnAddDoctor = new Button("의사 추가");
        btnViewAllDoctors = new Button("전체 의사 목록 조회");
        btnViewAllReservations = new Button("전체 예약 목록 조회");

        btnAddDoctor.addActionListener(this);
        btnViewAllDoctors.addActionListener(this);
        btnViewAllReservations.addActionListener(this);

        buttonPanel.add(btnAddDoctor);
        buttonPanel.add(btnViewAllDoctors);
        buttonPanel.add(btnViewAllReservations);
        
        Panel centerWrapperPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centerWrapperPanel.add(buttonPanel);
        add(centerWrapperPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddDoctor) {
            AddDoctorDialog addDoctorDialog = new AddDoctorDialog(mainFrame, true, adminController);
            addDoctorDialog.setVisible(true);
        } else if (e.getSource() == btnViewAllDoctors) {
            ViewDoctorsAdminPanel doctorsPanel = new ViewDoctorsAdminPanel(adminController, mainFrame, this);
            mainFrame.switchPanel(doctorsPanel, "viewAllDoctorsAdmin");
        } else if (e.getSource() == btnViewAllReservations) {
            ViewAllReservationsPanel reservationsPanel = new ViewAllReservationsPanel(adminController, mainFrame, this);
            mainFrame.switchPanel(reservationsPanel, "viewAllReservationsAdmin");
        } else if (e.getSource() == btnLogout) {
            mainFrame.showLoginScreenAndRetryLogin();
        }
    }
}