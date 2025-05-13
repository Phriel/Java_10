package hospital.reservation.dao;

import hospital.reservation.model.Admin;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminDao {
    private final String filePath = "data/admins.csv"; // final 추가 가능
    private final List<Admin> admins = new ArrayList<>(); // final 추가 (참조 불변)
    // 관리자는 수가 적고, CSV에서 직접 관리한다고 가정. ID 자동 생성은 생략.

    public AdminDao() {
        loadData();
    }

    private void loadData() {
        File file = new File(filePath);
        if (!file.exists()) { return; }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); 
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length == 4) {
                    admins.add(new Admin(data[0], data[1], data[2], data[3]));
                } else {
                    System.err.println("!오류(AdminDao): CSV 데이터 형식 오류 - " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("!오류(AdminDao): 관리자 정보 로드 중 - " + e.getMessage());
        }
    }

    public void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("adminId,username,password,name\n");
            for (Admin admin : admins) { 
                bw.write(admin.toCsvString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("!오류(AdminDao): 관리자 정보 저장 중 - " + e.getMessage());
        }
    }
    
    public Optional<Admin> findByUsername(String username) { 
        return admins.stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }
}