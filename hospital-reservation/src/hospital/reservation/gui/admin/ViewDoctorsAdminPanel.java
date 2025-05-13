package hospital.reservation.gui.admin;

import hospital.reservation.controller.AdminController;
import hospital.reservation.gui.common.MainFrame;
import hospital.reservation.model.Doctor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewDoctorsAdminPanel extends Panel implements ActionListener {
    private AdminController adminController;
    private MainFrame mainFrame;
    private Panel previousPanel; // 돌아갈 이전 패널 (AdminDashboardPanel)

    private TextArea taDoctors;
    private Button btnBack;

    public ViewDoctorsAdminPanel(AdminController adminController, MainFrame mainFrame, Panel previousPanel) {
        this.adminController = adminController;
        this.mainFrame = mainFrame;
        this.previousPanel = previousPanel;

        setLayout(new BorderLayout(10, 10));

        Label titleLabel = new Label("전체 의사 목록", Label.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        taDoctors = new TextArea(15, 70);
        taDoctors.setEditable(false);
        add(taDoctors, BorderLayout.CENTER);

        Panel bottomPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new Button("뒤로가기 (관리자메뉴)");
        btnBack.addActionListener(this);
        bottomPanel.add(btnBack);
        add(bottomPanel, BorderLayout.SOUTH);

        loadDoctors();
    }

    private void loadDoctors() {
        List<Doctor> doctors = adminController.getAllDoctors();
        taDoctors.setText(""); // Clear previous content
        if (doctors.isEmpty()) {
            taDoctors.append("등록된 의사가 없습니다.");
        } else {
            taDoctors.append(String.format("%-6s | %-12s | %-15s | %-15s\n", "의사ID", "사용자명", "이름", "진료과"));
            taDoctors.append("--------------------------------------------------------------------\n");
            for (Doctor doc : doctors) {
                taDoctors.append(String.format("%-6s | %-12s | %-15s | %-15s\n",
                        doc.getUserId(), doc.getUsername(), doc.getName(), doc.getDepartmentName()));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            mainFrame.switchPanel(previousPanel, "adminDashboard"); // 이전 패널로 돌아가기
        }
    }
}