package hospital.reservation.gui.patient;

import hospital.reservation.controller.ReservationController;
import hospital.reservation.gui.common.MainFrame;
import hospital.reservation.model.Patient;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PatientDashboardPanel extends Panel implements ActionListener {
    private final Patient patient;
    private final MainFrame mainFrame; 
    private final ReservationController reservationController;

    private final Button btnViewDoctorsAndReserve;
    private final Button btnViewMyReservations;   
    private final Button btnWithdrawAccount; 
    private final Button btnLogout;               

    public PatientDashboardPanel(Patient patient, MainFrame mainFrame, ReservationController reservationController) {
        this.patient = patient;
        this.mainFrame = mainFrame;
        this.reservationController = reservationController;

        setLayout(new BorderLayout(10, 10));

        Panel topPanel = new Panel(new BorderLayout());
        Label lblWelcome = new Label(patient.getName() + "님, 환영합니다.", Label.CENTER);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(lblWelcome, BorderLayout.CENTER);

        btnLogout = new Button("로그아웃");
        btnLogout.addActionListener(this);
        Panel logoutButtonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        logoutButtonPanel.add(btnLogout);
        topPanel.add(logoutButtonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        Panel buttonPanel = new Panel(new GridLayout(3, 1, 10, 10)); 
        btnViewDoctorsAndReserve = new Button("진료과/의사 조회 및 예약하기");
        btnViewMyReservations = new Button("나의 예약 조회/취소");
        btnWithdrawAccount = new Button("회원탈퇴"); 

        btnViewDoctorsAndReserve.addActionListener(this);
        btnViewMyReservations.addActionListener(this);
        btnWithdrawAccount.addActionListener(this); 

        buttonPanel.add(btnViewDoctorsAndReserve);
        buttonPanel.add(btnViewMyReservations);
        buttonPanel.add(btnWithdrawAccount); 
        
        Panel centerWrapperPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        centerWrapperPanel.add(buttonPanel);
        add(centerWrapperPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnViewDoctorsAndReserve) {
            MakeReservationDialog reservationDialog = new MakeReservationDialog(
                mainFrame, 
                true, 
                patient, 
                reservationController, 
                // MainFrame을 통해 ReservationController에 접근하거나, Main에서 직접 ReservationController의 메소드 호출
                mainFrame.getReservationController().getAllDepartmentNames() 
            );
            reservationDialog.setVisible(true);
        } else if (source == btnViewMyReservations) {
            ViewPatientReservationsPanel reservationsPanel = new ViewPatientReservationsPanel(patient, reservationController, mainFrame);
            mainFrame.switchPanel(reservationsPanel, "viewMyReservations");
        } else if (source == btnWithdrawAccount) {
            WithdrawConfirmDialog withdrawDialog = new WithdrawConfirmDialog(mainFrame, true, patient);
            withdrawDialog.setVisible(true);
            // 실제 로그아웃 처리는 WithdrawConfirmDialog가 성공적으로 닫힌 후
            // MainFrame의 showLoginScreenAndRetryLogin() 호출을 통해 이루어짐
        } else if (source == btnLogout) {
            mainFrame.showLoginScreenAndRetryLogin();
        }
    }
}