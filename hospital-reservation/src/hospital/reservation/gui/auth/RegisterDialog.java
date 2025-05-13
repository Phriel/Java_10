package hospital.reservation.gui.auth;

import hospital.reservation.controller.AuthController;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.Main; // AuthController를 가져오기 위함

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RegisterDialog extends Dialog implements ActionListener {
    private TextField tfUsername, tfPassword, tfName, tfSsn, tfPhoneNumber;
    private Button btnRegister, btnCancel;
    private Label lblStatus;
    private AuthController authController;

    public RegisterDialog(Frame owner, boolean modal) {
        super(owner, "환자 회원가입", modal);
        this.authController = Main.getAuthController(); // Main 클래스에서 AuthController 가져오기

        setLayout(new BorderLayout(10,10));
        setSize(400, 350);
        setLocationRelativeTo(owner);

        Panel inputPanel = new Panel(new GridLayout(5, 2, 5, 5));
        inputPanel.add(new Label(" 아이디:", Label.RIGHT));
        tfUsername = new TextField(20);
        inputPanel.add(tfUsername);

        inputPanel.add(new Label(" 비밀번호:", Label.RIGHT));
        tfPassword = new TextField(20);
        tfPassword.setEchoChar('*');
        inputPanel.add(tfPassword);

        inputPanel.add(new Label(" 이름:", Label.RIGHT));
        tfName = new TextField(20);
        inputPanel.add(tfName);

        inputPanel.add(new Label(" 주민등록번호:", Label.RIGHT));
        tfSsn = new TextField(20);
        inputPanel.add(tfSsn);

        inputPanel.add(new Label(" 전화번호:", Label.RIGHT));
        tfPhoneNumber = new TextField(20);
        inputPanel.add(tfPhoneNumber);

        lblStatus = new Label("", Label.CENTER);
        lblStatus.setForeground(Color.RED);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRegister = new Button("회원가입");
        btnCancel = new Button("취소");
        btnRegister.addActionListener(this);
        btnCancel.addActionListener(this);
        buttonPanel.add(btnRegister);
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
        if (e.getSource() == btnRegister) {
            try {
                authController.registerPatient(
                        tfUsername.getText(),
                        tfPassword.getText(),
                        tfName.getText(),
                        tfSsn.getText(),
                        tfPhoneNumber.getText()
                );
                lblStatus.setForeground(Color.BLUE);
                lblStatus.setText("회원가입 성공! 로그인해주세요.");
                // 성공 후 다이얼로그 바로 닫기 또는 메시지 확인 후 닫도록 타이머 설정 가능
                // 여기서는 메시지 표시 후 사용자가 직접 닫거나, 부모창에서 다른 액션 유도
                // btnRegister.setEnabled(false); // 중복 등록 방지
                // 잠시 후 다이얼로그 닫기
                 try {
                     Thread.sleep(1500); // 1.5초 대기
                 } catch (InterruptedException ignored) {}
                 dispose();

            } catch (InvalidInputException | IllegalArgumentException ex) {
                lblStatus.setForeground(Color.RED);
                lblStatus.setText(ex.getMessage());
            }
        } else if (e.getSource() == btnCancel) {
            dispose();
        }
    }
}