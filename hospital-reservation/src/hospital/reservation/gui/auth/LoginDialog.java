package hospital.reservation.gui.auth;

import hospital.reservation.controller.AuthController;
import hospital.reservation.exception.AuthenticationException;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.model.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginDialog extends Dialog implements ActionListener {
    private TextField tfUsername;
    private TextField tfPassword;
    private Button btnLogin;
    private Button btnRegister; // 회원가입 버튼 추가
    private Button btnCancel;
    private Label lblStatus;

    private AuthController authController;
    private User loggedInUser = null;
    private Frame ownerFrame; // RegisterDialog를 띄울 때 부모 프레임으로 사용

    public LoginDialog(Frame owner, boolean modal, AuthController authController) {
        super(owner, "로그인", modal);
        this.ownerFrame = owner; // 부모 프레임 저장
        this.authController = authController;

        setLayout(new BorderLayout(10, 10));
        setSize(350, 250); // 버튼 추가로 높이 약간 늘림
        setLocationRelativeTo(owner);

        // 입력 패널
        Panel inputPanel = new Panel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new Label(" 아이디:", Label.RIGHT));
        tfUsername = new TextField(20);
        inputPanel.add(tfUsername);
        inputPanel.add(new Label(" 비밀번호:", Label.RIGHT));
        tfPassword = new TextField(20);
        tfPassword.setEchoChar('*');
        inputPanel.add(tfPassword);
        
        // 상태 메시지 레이블
        lblStatus = new Label("", Label.CENTER);
        lblStatus.setForeground(Color.RED);

        // 버튼 패널 - FlowLayout으로 변경하여 여러 버튼 배치 용이하게
        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // 간격 조정
        btnLogin = new Button("로그인");
        btnRegister = new Button("환자 회원가입"); // 버튼 생성
        btnCancel = new Button("취소");

        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this); // 리스너 추가
        btnCancel.addActionListener(this);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister); // 패널에 버튼 추가
        buttonPanel.add(btnCancel);

        // 엔터 키로 로그인 액션 리스너 (performLogin 호출)
        ActionListener loginAction = e -> performLogin();
        tfUsername.addActionListener(loginAction);
        tfPassword.addActionListener(loginAction);
        // btnLogin에는 이미 this로 ActionListener가 등록되어 performLogin이 호출됨

        // 패널들을 다이얼로그에 추가
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
        add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널을 SOUTH에 배치

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loggedInUser = null;
                dispose();
            }
        });
    }
    
    private void performLogin() {
        String username = tfUsername.getText();
        String password = tfPassword.getText();
        try {
            loggedInUser = authController.login(username, password);
            if (loggedInUser != null) {
                dispose(); 
            } else {
                // 현재 AuthController.login은 실패 시 예외를 던지므로 이 부분은 도달하지 않을 수 있음
                lblStatus.setText("알 수 없는 오류로 로그인 실패.");
            }
        } catch (AuthenticationException | InvalidInputException ex) {
            lblStatus.setText(ex.getMessage());
            loggedInUser = null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnLogin) {
            performLogin();
        } else if (source == btnRegister) {
            // 회원가입 버튼 클릭 시
            RegisterDialog registerDialog = new RegisterDialog(this.ownerFrame, true); // ownerFrame 사용
            // 로그인 다이얼로그를 잠시 숨김 (선택적)
            // setVisible(false); 
            registerDialog.setVisible(true);
            // 회원가입 다이얼로그가 닫힌 후, 로그인 다이얼로그를 다시 보이게 하거나,
            // 아니면 사용자가 직접 로그인 다이얼로그를 닫고 다시 열도록 유도할 수 있음.
            // 여기서는 RegisterDialog가 닫히면 LoginDialog는 그대로 남아있는 상태가 됨.
            // setVisible(true); // 필요하다면 다시 보이게
        } else if (source == btnCancel) {
            loggedInUser = null;
            dispose();
        }
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}