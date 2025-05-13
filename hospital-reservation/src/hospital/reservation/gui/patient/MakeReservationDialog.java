// src/hospital/reservation/gui/patient/MakeReservationDialog.java
package hospital.reservation.gui.patient;

import hospital.reservation.controller.ReservationController;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.exception.RecordNotFoundException;
import hospital.reservation.gui.common.MainFrame; // Frame 타입의 owner를 받기 위해 유지
import hospital.reservation.model.Doctor;
import hospital.reservation.model.Patient;
import hospital.reservation.util.DateUtil;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MakeReservationDialog extends Dialog implements ActionListener, ItemListener {
    private final Patient patient; // final 추가
    private final ReservationController reservationController; // final 추가
    // private MainFrame mainFrame; // 직접 사용하지 않으면 필드에서 제거 가능 (super 생성자에 owner 전달로 충분)

    private final Choice choiceDepartment; // final 추가
    private final Choice choiceDoctor;     // final 추가
    private final TextField tfDate;       // final 추가
    private final TextField tfTime;         // final 추가
    private final Button btnReserve;      // final 추가
    private final Button btnCancel;       // final 추가
    private final Label lblStatus;        // final 추가

    // private List<String> departmentNames; // 멤버 변수에서 제거
    private List<Doctor> currentDoctors; // 의사 목록은 동적으로 변경되므로 final 아님

    public MakeReservationDialog(Frame owner, boolean modal, Patient patient, ReservationController reservationController, List<String> deptNamesParam) {
        super(owner, "진료 예약하기", modal);
        this.patient = patient;
        this.reservationController = reservationController;
        // this.mainFrame = (owner instanceof MainFrame) ? (MainFrame) owner : null; // 직접 MainFrame의 메소드 호출 안하면 불필요
        // this.departmentNames = deptNamesParam; // 멤버 변수 할당 제거

        setLayout(new BorderLayout(10, 10));
        setSize(450, 350);
        setLocationRelativeTo(owner);

        Panel inputPanel = new Panel(new GridLayout(5, 2, 5, 10));

        inputPanel.add(new Label(" 진료과:", Label.RIGHT));
        choiceDepartment = new Choice();
        choiceDepartment.add("-- 선택 --");
        if (deptNamesParam != null) { // 생성자 파라미터 직접 사용
            for (String deptName : deptNamesParam) {
                choiceDepartment.add(deptName);
            }
        }
        choiceDepartment.addItemListener(this);
        inputPanel.add(choiceDepartment);

        inputPanel.add(new Label(" 의사:", Label.RIGHT));
        choiceDoctor = new Choice();
        choiceDoctor.add("-- 진료과 먼저 선택 --");
        inputPanel.add(choiceDoctor);

        inputPanel.add(new Label(" 예약 날짜 (YYYY-MM-DD):", Label.RIGHT));
        tfDate = new TextField(DateUtil.B_getCurrentDateString(), 15);
        inputPanel.add(tfDate);

        inputPanel.add(new Label(" 예약 시간 (HH:MM):", Label.RIGHT));
        tfTime = new TextField("09:00", 15);
        inputPanel.add(tfTime);
        
        lblStatus = new Label(" ", Label.CENTER);
        lblStatus.setForeground(Color.RED);
        
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        btnReserve = new Button("예약하기");
        btnCancel = new Button("취소");
        btnReserve.addActionListener(this);
        btnCancel.addActionListener(this);
        buttonPanel.add(btnReserve);
        buttonPanel.add(btnCancel);

        Panel centerPanel = new Panel(new BorderLayout(0,10));
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        centerPanel.add(lblStatus, BorderLayout.SOUTH);

        Panel northPad = new Panel(); northPad.setPreferredSize(new Dimension(1,15));
        Panel westPad = new Panel(); westPad.setPreferredSize(new Dimension(15,1));
        Panel eastPad = new Panel(); eastPad.setPreferredSize(new Dimension(15,1));

        add(northPad, BorderLayout.NORTH);
        add(westPad, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(eastPad, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == choiceDepartment) {
            String selectedDept = choiceDepartment.getSelectedItem();
            choiceDoctor.removeAll();
            if (selectedDept != null && !selectedDept.equals("-- 선택 --")) {
                currentDoctors = reservationController.getDoctorsByDepartment(selectedDept);
                if (currentDoctors != null && !currentDoctors.isEmpty()) {
                    for (Doctor doctor : currentDoctors) {
                        choiceDoctor.add(doctor.getName() + " (" + doctor.getUserId() + ")");
                    }
                } else {
                    choiceDoctor.add("해당 과 의사 없음");
                }
            } else {
                choiceDoctor.add("-- 진료과 먼저 선택 --");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnReserve) {
            try {
                String selectedDoctorItem = choiceDoctor.getSelectedItem();
                if (selectedDoctorItem == null || selectedDoctorItem.contains("--") || selectedDoctorItem.contains("없음")) {
                    throw new InvalidInputException("의사를 선택해주세요.");
                }
                String doctorId = selectedDoctorItem.substring(selectedDoctorItem.lastIndexOf("(") + 1, selectedDoctorItem.lastIndexOf(")"));
                
                String department = choiceDepartment.getSelectedItem(); // department 변수는 현재 makeReservation에 직접 전달 안함
                 if (department == null || department.equals("-- 선택 --")) {
                    throw new InvalidInputException("진료과를 선택해주세요.");
                }

                reservationController.makeReservation(
                        patient,
                        doctorId,
                        tfDate.getText(),
                        tfTime.getText()
                );
                lblStatus.setForeground(Color.BLUE);
                lblStatus.setText("예약 성공! (예약번호는 나의 예약조회에서 확인)");
                 try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                 dispose();

            } catch (InvalidInputException | RecordNotFoundException ex) {
                lblStatus.setForeground(Color.RED);
                lblStatus.setText("예약 실패: " + ex.getMessage());
            }
        } else if (e.getSource() == btnCancel) {
            dispose();
        }
    }
}