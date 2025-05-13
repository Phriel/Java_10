package hospital.reservation.gui.doctor;

import hospital.reservation.controller.DoctorController;
import hospital.reservation.gui.common.MainFrame;
import hospital.reservation.model.Doctor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DoctorDashboardPanel extends Panel implements ActionListener {
    private final Doctor doctor; // final 추가
    private final MainFrame mainFrame; // final 추가
    private final DoctorController doctorController; // final 추가

    private final Button btnViewSchedule;       // final 추가
    private final Button btnManageAvailability; // final 추가
    private final Button btnLogout;             // final 추가

    public DoctorDashboardPanel(Doctor doctor, MainFrame mainFrame, DoctorController doctorController) {
        this.doctor = doctor;
        this.mainFrame = mainFrame;
        this.doctorController = doctorController;

        setLayout(new BorderLayout(10, 10));

        Panel topPanel = new Panel(new BorderLayout());
        Label lblWelcome = new Label("의사: " + doctor.getName() + "님 (" + doctor.getDepartmentName() + ")", Label.CENTER);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(lblWelcome, BorderLayout.CENTER);

        btnLogout = new Button("로그아웃");
        btnLogout.addActionListener(this); // 생성자에서 this를 리스너로 등록 (Leaking this 경고 발생 가능성 있음)
        Panel logoutButtonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        logoutButtonPanel.add(btnLogout);
        topPanel.add(logoutButtonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        Panel buttonPanel = new Panel(new GridLayout(2, 1, 10, 10));
        btnViewSchedule = new Button("나의 예약 스케줄 보기");
        btnManageAvailability = new Button("진료 가능 시간/휴진 설정");

        btnViewSchedule.addActionListener(this); // 생성자에서 this를 리스너로 등록
        btnManageAvailability.addActionListener(this); // 생성자에서 this를 리스너로 등록

        buttonPanel.add(btnViewSchedule);
        buttonPanel.add(btnManageAvailability);
        
        Panel centerWrapperPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centerWrapperPanel.add(buttonPanel);
        add(centerWrapperPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnViewSchedule) {
            ViewDoctorSchedulePanel schedulePanel = new ViewDoctorSchedulePanel(doctor, doctorController, mainFrame, this);
            mainFrame.switchPanel(schedulePanel, "viewDoctorSchedule");
        } else if (e.getSource() == btnManageAvailability) {
            ManageAvailabilityDialog availabilityDialog = new ManageAvailabilityDialog(mainFrame, true, doctor, doctorController);
            availabilityDialog.setVisible(true);
        } else if (e.getSource() == btnLogout) {
            mainFrame.showLoginScreenAndRetryLogin();
        }
    }
}