package hospital.reservation.gui.doctor;

import hospital.reservation.controller.DoctorController;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.exception.RecordNotFoundException;
import hospital.reservation.model.Doctor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ManageAvailabilityDialog extends Dialog implements ActionListener {
    private Doctor doctor;
    private DoctorController doctorController;

    private TextArea taAvailability; // 여러 줄 입력 가능하도록 TextArea 사용
    private Button btnSave;
    private Button btnCancel;
    private Label lblStatus;

    public ManageAvailabilityDialog(Frame owner, boolean modal, Doctor doctor, DoctorController doctorController) {
        super(owner, "진료 가능 시간/휴진 설정", modal);
        this.doctor = doctor;
        this.doctorController = doctorController;

        setLayout(new BorderLayout(10, 10));
        setSize(450, 300);
        setLocationRelativeTo(owner);

        Panel topPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new Label("현재 설정: " + doctor.getName() + " 의사님"));
        add(topPanel, BorderLayout.NORTH);
        
        taAvailability = new TextArea(doctor.getAvailability(), 5, 50, TextArea.SCROLLBARS_VERTICAL_ONLY); // 기존 정보 표시
        add(taAvailability, BorderLayout.CENTER);

        lblStatus = new Label(" ", Label.CENTER);
        lblStatus.setForeground(Color.RED);
        
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        btnSave = new Button("저장");
        btnCancel = new Button("취소");
        btnSave.addActionListener(this);
        btnCancel.addActionListener(this);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        Panel southPanel = new Panel(new BorderLayout());
        southPanel.add(lblStatus, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) {
            String newAvailability = taAvailability.getText();
            try {
                doctorController.updateDoctorAvailability(doctor.getUserId(), newAvailability);
                doctor.setAvailability(newAvailability); // 현재 Doctor 객체에도 반영 (UI 즉시 반영 위함)
                lblStatus.setForeground(Color.BLUE);
                lblStatus.setText("진료 가능 시간이 성공적으로 업데이트되었습니다.");
                 try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                dispose();
            } catch (RecordNotFoundException | InvalidInputException ex) {
                lblStatus.setForeground(Color.RED);
                lblStatus.setText("업데이트 실패: " + ex.getMessage());
            }
        } else if (e.getSource() == btnCancel) {
            dispose();
        }
    }
}