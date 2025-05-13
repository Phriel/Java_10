package hospital.reservation.gui.common;

import hospital.reservation.Main;

public class AppGUILauncher {
    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame(
            Main.getAuthController(),
            Main.getReservationController(),
            Main.getAdminController(),
            Main.getDoctorController() // DoctorController 전달 추가
        );
        mainFrame.setVisible(true);
        mainFrame.showLoginDialog();
    }
}