package hospital.reservation.gui.patient;

import hospital.reservation.controller.ReservationController;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.exception.RecordNotFoundException;
import hospital.reservation.gui.common.MainFrame;
import hospital.reservation.model.Patient;
import hospital.reservation.model.Reservation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewPatientReservationsPanel extends Panel implements ActionListener {
    private Patient patient;
    private ReservationController reservationController;
    private MainFrame mainFrame;

    private TextArea taReservations;
    private TextField tfCancelReservationId;
    private Button btnCancel;
    private Button btnBack;
    private Label lblStatus;

    public ViewPatientReservationsPanel(Patient patient, ReservationController reservationController, MainFrame mainFrame) {
        this.patient = patient;
        this.reservationController = reservationController;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(10, 10));

        Label titleLabel = new Label(patient.getName() + "님의 예약 목록", Label.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        taReservations = new TextArea(10, 60); // 10줄, 60컬럼 크기
        taReservations.setEditable(false);
        add(taReservations, BorderLayout.CENTER);

        Panel bottomPanel = new Panel(new BorderLayout(5,5));
        Panel cancelInputPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        cancelInputPanel.add(new Label("취소할 예약 번호:"));
        tfCancelReservationId = new TextField(10);
        cancelInputPanel.add(tfCancelReservationId);
        btnCancel = new Button("선택 예약 취소");
        btnCancel.addActionListener(this);
        cancelInputPanel.add(btnCancel);
        
        bottomPanel.add(cancelInputPanel, BorderLayout.NORTH);

        lblStatus = new Label(" ", Label.CENTER);
        lblStatus.setForeground(Color.RED);
        bottomPanel.add(lblStatus, BorderLayout.CENTER);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new Button("뒤로가기 (환자메뉴)");
        btnBack.addActionListener(this);
        buttonPanel.add(btnBack);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);

        loadReservations();
    }

    private void loadReservations() {
        List<Reservation> reservations = reservationController.getReservationsForPatient(patient);
        taReservations.setText(""); // 기존 내용 지우기
        if (reservations.isEmpty()) {
            taReservations.append("예약 내역이 없습니다.");
        } else {
            taReservations.append(String.format("%-10s | %-10s | %-10s | %-15s | %-10s | %-5s\n",
                    "예약번호", "환자ID", "의사ID", "진료과", "예약일", "시간"));
            taReservations.append("------------------------------------------------------------------------\n");
            for (Reservation res : reservations) {
                taReservations.append(String.format("%-10s | %-10s | %-10s | %-15s | %-10s | %-5s\n",
                        res.getReservationId(), res.getPatientId(), res.getDoctorId(),
                        res.getDepartmentName(), res.getReservationDate(), res.getReservationTime()));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            String reservationId = tfCancelReservationId.getText();
            try {
                boolean success = reservationController.cancelReservation(patient, reservationId);
                if (success) {
                    lblStatus.setForeground(Color.BLUE);
                    lblStatus.setText("예약번호 " + reservationId + " 취소 성공.");
                    loadReservations(); // 목록 새로고침
                    tfCancelReservationId.setText(""); // 입력 필드 비우기
                } else {
                    // 컨트롤러에서 예외를 던지지 않고 false를 반환한 경우 (현재 로직에서는 예외 발생)
                    lblStatus.setForeground(Color.RED);
                    lblStatus.setText("예약 취소 실패. (이미 처리되었거나 알 수 없는 오류)");
                }
            } catch (InvalidInputException | RecordNotFoundException ex) {
                lblStatus.setForeground(Color.RED);
                lblStatus.setText("취소 오류: " + ex.getMessage());
            }
        } else if (e.getSource() == btnBack) {
            // 환자 대시보드 패널로 돌아가기
            PatientDashboardPanel patientDashboard = new PatientDashboardPanel(patient, mainFrame, reservationController);
            mainFrame.switchPanel(patientDashboard, "patientDashboard");
        }
    }
}
