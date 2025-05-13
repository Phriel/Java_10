package hospital.reservation.gui.admin;

import hospital.reservation.controller.AdminController;
import hospital.reservation.exception.InvalidInputException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddDoctorDialog extends Dialog implements ActionListener {
    private TextField tfUsername, tfPassword, tfName, tfDepartmentName;
    private Button btnAdd, btnCancel;
    private Label lblStatus;
    private AdminController adminController;

    public AddDoctorDialog(Frame owner, boolean modal, AdminController adminController) {
        super(owner, "새 의사 추가", modal);
        this.adminController = adminController;

        setLayout(new BorderLayout(10,10));
        setSize(400, 300);
        setLocationRelativeTo(owner);

        Panel inputPanel = new Panel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(new Label(" 사용자명(ID):", Label.RIGHT)); // 의사 로그인 기능 없어도 ID는 필요
        tfUsername = new TextField(20);
        inputPanel.add(tfUsername);

        inputPanel.add(new Label(" 초기 비밀번호:", Label.RIGHT)); // 의사 로그인 기능 없어도 형식상 필요
        tfPassword = new TextField(20);
        tfPassword.setEchoChar('*');
        inputPanel.add(tfPassword);

        inputPanel.add(new Label(" 의사 이름:", Label.RIGHT));
        tfName = new TextField(20);
        inputPanel.add(tfName);

        inputPanel.add(new Label(" 진료과:", Label.RIGHT));
        tfDepartmentName = new TextField(20);
        inputPanel.add(tfDepartmentName);
        
        lblStatus = new Label(" ", Label.CENTER);
        lblStatus.setForeground(Color.RED);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        btnAdd = new Button("추가하기");
        btnCancel = new Button("취소");
        btnAdd.addActionListener(this);
        btnCancel.addActionListener(this);
        buttonPanel.add(btnAdd);
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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            try {
                adminController.addDoctor(
                        tfUsername.getText(),
                        tfPassword.getText(),
                        tfName.getText(),
                        tfDepartmentName.getText()
                );
                lblStatus.setForeground(Color.BLUE);
                lblStatus.setText("의사 정보 추가 성공!");
                // 성공 후 필드 초기화 또는 다이얼로그 닫기
                 try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                 dispose();

            } catch (InvalidInputException | IllegalArgumentException ex) {
                lblStatus.setForeground(Color.RED);
                lblStatus.setText("추가 실패: " + ex.getMessage());
            }
        } else if (e.getSource() == btnCancel) {
            dispose();
        }
    }
}