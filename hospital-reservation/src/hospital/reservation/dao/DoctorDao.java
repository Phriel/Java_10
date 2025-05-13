package hospital.reservation.dao;

import hospital.reservation.model.Doctor;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DoctorDao {
    private final String filePath = "data/doctors.csv";
    private List<Doctor> doctors = new ArrayList<>();
    private int nextIdCounter;

    public DoctorDao() {
        loadData();
        if (doctors.isEmpty()) {
            nextIdCounter = 1;
        } else {
            nextIdCounter = doctors.stream()
                                  .mapToInt(d -> {
                                      try {
                                          return Integer.parseInt(d.getUserId().substring(1)); // "D" 제거
                                      } catch (NumberFormatException | NullPointerException e) {
                                          return 0; // ID 형식이 잘못된 경우 기본값 처리
                                      }
                                  })
                                  .max()
                                  .orElse(0) + 1;
        }
    }

    private void loadData() {
        File file = new File(filePath);
        if (!file.exists()) { return; }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // 헤더 스킵
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1); // 모든 필드 포함 (빈 필드도)
                if (data.length == 6) { // availability 컬럼 추가로 6개
                    doctors.add(new Doctor(data[0], data[1], data[2], data[3], data[4], data[5]));
                } else {
                     System.err.println("!오류(DoctorDao): doctors.csv 데이터 형식 오류 - " + line + " (필드 개수: " + data.length + ")");
                }
            }
        } catch (IOException e) {
            System.err.println("!오류(DoctorDao): 의사 정보 로드 중 - " + e.getMessage());
        }
    }

    public void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("doctorId,username,password,name,departmentName,availability\n"); // 헤더에 availability 추가
            for (Doctor doctor : doctors) {
                bw.write(doctor.toCsvString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("!오류(DoctorDao): 의사 정보 저장 중 - " + e.getMessage());
        }
    }

    public Optional<Doctor> findById(String doctorId) {
        return doctors.stream()
                .filter(d -> d.getUserId().equals(doctorId))
                .findFirst();
    }

    public Optional<Doctor> findByUsername(String username) { // 의사 로그인용
        return doctors.stream()
                .filter(d -> d.getUsername().equals(username))
                .findFirst();
    }

    public List<Doctor> findByDepartment(String departmentName) {
        return doctors.stream()
                .filter(d -> d.getDepartmentName().equalsIgnoreCase(departmentName))
                .collect(Collectors.toList());
    }

    public List<Doctor> findAll() {
        return new ArrayList<>(doctors);
    }

    public Doctor addDoctor(Doctor doctor) throws IllegalArgumentException {
        // username 중복은 로그인 기능 시 중요하나, 현재는 간단히 ID 기준으로만 관리
        if (findById(doctor.getUserId()).isPresent()){
             throw new IllegalArgumentException("이미 존재하는 의사 ID입니다: " + doctor.getUserId());
        }
        doctors.add(doctor);
        return doctor;
    }
    
    public String getNextDoctorId() {
        return "D" + String.format("%03d", nextIdCounter);
    }

    public boolean updateDoctor(Doctor updatedDoctor) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getUserId().equals(updatedDoctor.getUserId())) {
                doctors.set(i, updatedDoctor); // 객체 교체
                return true;
            }
        }
        return false; // 해당 ID의 의사가 없음
    }
}