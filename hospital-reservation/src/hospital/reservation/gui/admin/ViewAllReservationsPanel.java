package hospital.reservation.gui.admin;

import hospital.reservation.controller.AdminController;
import hospital.reservation.gui.common.MainFrame;
import hospital.reservation.model.Reservation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewAllReservationsPanel extends Panel implements ActionListener {
    private final AdminController adminController; // final 추가
    private final MainFrame mainFrame;             // final 추가
    private final Panel previousPanel;           // final 추가

    private final TextArea taReservations;       // final 추가
    private final Button btnBack;                // final 추가

    public ViewAllReservationsPanel(AdminController adminController, MainFrame mainFrame, Panel previousPanel) {
        this.adminController = adminController;
        this.mainFrame = mainFrame;
        this.previousPanel = previousPanel;

        setLayout(new BorderLayout(10, 10));

        Label titleLabel = new Label("전체 예약 목록", Label.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        taReservations = new TextArea(15, 80);
        taReservations.setEditable(false);
        add(taReservations, BorderLayout.CENTER);

        Panel bottomPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new Button("뒤로가기 (관리자메뉴)");
        btnBack.addActionListener(this); // 생성자에서 this를 리스너로 등록
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        loadReservations();
    }

    private void loadReservations() {
        List<Reservation> reservations = adminController.getAllReservations();
        taReservations.setText(""); 
        if (reservations.isEmpty()) {
            taReservations.append("예약 내역이 없습니다.");
        } else {
            taReservations.append(String.format("%-10s | %-10s | %-10s | %-15s | %-12s | %-5s\n",
                    "예약번호", "환자ID", "의사ID", "진료과", "예약일", "시간"));
            taReservations.append("----------------------------------------------------------------------------------\n");
            for (Reservation res : reservations) {
                taReservations.append(String.format("%-10s | %-10s | %-10s | %-15s | %-12s | %-5s\n",
                        res.getReservationId(), res.getPatientId(), res.getDoctorId(),
                        res.getDepartmentName(), res.getReservationDate(), res.getReservationTime()));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            mainFrame.switchPanel(previousPanel, "adminDashboard");
        }
    }
}