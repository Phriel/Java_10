package hospital.reservation.gui.doctor;

import hospital.reservation.controller.DoctorController;
import hospital.reservation.exception.RecordNotFoundException;
import hospital.reservation.gui.common.MainFrame;
import hospital.reservation.model.Doctor;
import hospital.reservation.model.Reservation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewDoctorSchedulePanel extends Panel implements ActionListener {
    private Doctor doctor;
    private DoctorController doctorController;
    private MainFrame mainFrame;
    private Panel previousPanel; // DoctorDashboardPanel

    private TextArea taSchedule;
    private Button btnBack;
    private Label lblStatus;


    public ViewDoctorSchedulePanel(Doctor doctor, DoctorController doctorController, MainFrame mainFrame, Panel previousPanel) {
        this.doctor = doctor;
        this.doctorController = doctorController;
        this.mainFrame = mainFrame;
        this.previousPanel = previousPanel;

        setLayout(new BorderLayout(10, 10));

        Label titleLabel = new Label(doctor.getName() + " 의사님 예약 스케줄", Label.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        taSchedule = new TextArea(15, 80);
        taSchedule.setEditable(false);
        add(taSchedule, BorderLayout.CENTER);
        
        Panel bottomPanel = new Panel(new BorderLayout());
        lblStatus = new Label(" ", Label.CENTER);
        lblStatus.setForeground(Color.RED);
        bottomPanel.add(lblStatus, BorderLayout.CENTER);

        Panel buttonWrapper = new Panel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new Button("뒤로가기 (의사메뉴)");
        btnBack.addActionListener(this);
        buttonWrapper.add(btnBack);
        bottomPanel.add(buttonWrapper, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        loadSchedule();
    }

    private void loadSchedule() {
        try {
            List<Reservation> schedule = doctorController.getDoctorSchedule(doctor.getUserId());
            taSchedule.setText(""); // Clear previous content
            if (schedule.isEmpty()) {
                taSchedule.append("예약된 스케줄이 없습니다.");
            } else {
                taSchedule.append(String.format("%-10s | %-10s | %-15s | %-12s | %-5s\n",
                        "예약번호", "환자ID", "진료과", "예약일", "시간"));
                taSchedule.append("-------------------------------------------------------------------------\n");
                for (Reservation res : schedule) {
                    taSchedule.append(String.format("%-10s | %-10s | %-15s | %-12s | %-5s\n",
                            res.getReservationId(), res.getPatientId(),
                            res.getDepartmentName(), res.getReservationDate(), res.getReservationTime()));
                }
            }
            lblStatus.setText(" ");
        } catch (RecordNotFoundException e) {
            taSchedule.setText("스케줄을 불러오는 중 오류 발생: " + e.getMessage());
            lblStatus.setText("오류: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            mainFrame.switchPanel(previousPanel, "doctorDashboard");
        }
    }
}