package hospital.reservation.gui.patient;

import hospital.reservation.Main; 
import hospital.reservation.controller.AuthController;
import hospital.reservation.exception.AuthenticationException;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.exception.RecordNotFoundException;
import hospital.reservation.gui.common.MainFrame; // MainFrame 참조 추가
import hospital.reservation.model.Patient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WithdrawConfirmDialog extends Dialog implements ActionListener {
    private final Patient patient;
    private final AuthController authController;
    private final MainFrame mainFrameOwner; 

    private final TextField tfPasswordConfirm;
    private final Button btnConfirmWithdraw;
    private final Button btnCancel;
    private final Label lblStatus;

    private boolean withdrawalSucceeded = false;

    public WithdrawConfirmDialog(Frame owner, boolean modal, Patient patient) {
        super(owner, "회원 탈퇴 확인", modal);
        this.mainFrameOwner = (owner instanceof MainFrame) ? (MainFrame) owner : null;
        this.patient = patient;
        this.authController = Main.getAuthController(); 

        setLayout(new BorderLayout(10,10));
        setSize(400, 220);
        setLocationRelativeTo(owner);

        Panel infoPanel = new Panel(new GridLayout(2,1,5,5));
        infoPanel.add(new Label("정말로 회원탈퇴를 진행하시겠습니까?", Label.CENTER));
        infoPanel.add(new Label("계정 보호를 위해 비밀번호를 다시 입력해주세요.", Label.CENTER));
        
        Panel inputPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.add(new Label("비밀번호:"));
        tfPasswordConfirm = new TextField(15);
        tfPasswordConfirm.setEchoChar('*');
        inputPanel.add(tfPasswordConfirm);
        
        lblStatus = new Label(" ", Label.CENTER);
        lblStatus.setForeground(Color.RED);

        Panel buttonPanel = new Panel(new FlowLayout(FlowLayout.CENTER));
        btnConfirmWithdraw = new Button("탈퇴 확인");
        btnCancel = new Button("취소");
        btnConfirmWithdraw.addActionListener(this);
        btnCancel.addActionListener(this);
        buttonPanel.add(btnConfirmWithdraw);
        buttonPanel.add(btnCancel);

        Panel centerContentPanel = new Panel(new BorderLayout(0,10));
        centerContentPanel.add(inputPanel, BorderLayout.CENTER);
        centerContentPanel.add(lblStatus, BorderLayout.SOUTH);

        Panel northPad = new Panel(); northPad.setPreferredSize(new Dimension(1,5)); // 간격 조절
        Panel westPad = new Panel(); westPad.setPreferredSize(new Dimension(10,1));
        Panel eastPad = new Panel(); eastPad.setPreferredSize(new Dimension(10,1));
        Panel southPad = new Panel(); southPad.setPreferredSize(new Dimension(1,5)); // 간격 조절

        add(northPad, BorderLayout.NORTH); // infoPanel을 North로 변경
        add(infoPanel, BorderLayout.NORTH); 
        add(westPad, BorderLayout.WEST);
        add(centerContentPanel, BorderLayout.CENTER);
        add(eastPad, BorderLayout.EAST);
        // buttonPanel을 southPad와 함께 묶어서 배치
        Panel bottomContainer = new Panel(new BorderLayout());
        bottomContainer.add(buttonPanel, BorderLayout.CENTER);
        bottomContainer.add(southPad, BorderLayout.SOUTH);
        add(bottomContainer, BorderLayout.SOUTH);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirmWithdraw) {
            String password = tfPasswordConfirm.getText();
            try {
                withdrawalSucceeded = authController.withdrawPatientAccount(patient.getUserId(), password);
                if (withdrawalSucceeded) {
                    lblStatus.setForeground(Color.BLUE);
                    lblStatus.setText("회원탈퇴가 성공적으로 처리되었습니다.");
                    
                    // MainFrame에 로그아웃 및 로그인 화면 전환 요청
                    if (mainFrameOwner != null) {
                         // 다이얼로그를 먼저 닫고 MainFrame의 메소드 호출
                        dispose(); 
                        mainFrameOwner.showLoginScreenAndRetryLogin(); 
                    } else {
                        dispose(); // MainFrame 참조가 없으면 그냥 닫기
                    }
                } 
                // 성공하지 않은 경우는 AuthController에서 예외를 던지므로,
                // else 블록은 거의 실행되지 않음. 예외가 catch 블록에서 처리됨.
            } catch (AuthenticationException | RecordNotFoundException | InvalidInputException ex) {
                lblStatus.setForeground(Color.RED);
                lblStatus.setText("탈퇴 오류: " + ex.getMessage());
                withdrawalSucceeded = false;
            }
        } else if (e.getSource() == btnCancel) {
            withdrawalSucceeded = false;
            dispose();
        }
    }

    // 이 메소드는 외부에서 탈퇴 성공 여부를 확인하기 위해 사용할 수 있으나,
    // 현재 로직에서는 다이얼로그가 닫힌 후 MainFrame에서 직접 상태를 변경하므로 필수 아님.
    public boolean didWithdrawalSucceed() {
        return withdrawalSucceeded;
    }
}