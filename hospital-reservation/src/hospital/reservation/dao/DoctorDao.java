package hospital.reservation.dao;

import hospital.reservation.model.Doctor;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DoctorDao {
    private final String filePath = "data/doctors.csv";
    private final List<Doctor> doctors = new ArrayList<>();
    private int nextIdCounter;

    public DoctorDao() {
        loadData(); // 수정된 loadData 호출
        if (doctors.isEmpty()) {
            nextIdCounter = 1;
        } else {
            nextIdCounter = doctors.stream()
                                  .mapToInt(d -> {
                                      try {
                                          return Integer.parseInt(d.getUserId().substring(1)); // "D" 제거
                                      } catch (NumberFormatException | NullPointerException e) {
                                          System.err.println("!오류(DoctorDao): 잘못된 의사 ID 형식 - " + d.getUserId());
                                          return 0;
                                      }
                                  })
                                  .filter(id -> id > 0)
                                  .max()
                                  .orElse(0) + 1;
        }
    }

    // CSV 한 줄을 파싱하는 헬퍼 메소드
    private List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // 큰따옴표 시작 또는 끝
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0); // 다음 필드를 위해 초기화
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString()); // 마지막 필드 추가
        return fields;
    }

    private void loadData() {
        doctors.clear(); // 기존 리스트를 비우고 새로 로드
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("경고(DoctorDao): " + filePath + " 파일이 존재하지 않습니다. 빈 리스트로 시작합니다.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // 헤더 스킵
            if (line == null) return; 

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; 

                List<String> dataList = parseCsvLine(line); // 수정된 파싱 방식 사용
                String[] data = dataList.toArray(new String[0]);

                if (data.length == 6) { // 컬럼 개수: doctorId,username,password,name,departmentName,availability
                    // availability 필드가 큰따옴표로 묶여있었다면, parseCsvLine에서 이미 처리됨.
                    // 추가적으로 앞뒤 큰따옴표 제거 (parseCsvLine에서 완벽히 제거 안될 수도 있어서 방어 코드)
                    String availability = data[5];
                    if (availability.startsWith("\"") && availability.endsWith("\"")) {
                        availability = availability.substring(1, availability.length() - 1);
                    }
                    
                    doctors.add(new Doctor(data[0], data[1], data[2], data[3], data[4], availability));
                } else {
                     System.err.println("!오류(DoctorDao): CSV 데이터 형식 오류 (파싱 후 필드 수: " + data.length + ") - " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("!오류(DoctorDao): 의사 정보 로드 중 - " + e.getMessage());
        }
    }

    public void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("doctorId,username,password,name,departmentName,availability\n"); 
            for (Doctor doctor : doctors) {
                bw.write(doctor.toCsvString()); // Doctor.toCsvString() 에서 availability를 큰따옴표로 감싸도록 수정 필요 가능성
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("!오류(DoctorDao): 의사 정보 저장 중 - " + e.getMessage());
        }
    }

    // ... (findById, findByUsername, findByDepartment, findAll, addDoctor, getNextDoctorId, updateDoctor 메소드는 이전과 동일하게 유지) ...
    public Optional<Doctor> findById(String doctorId) {
        return doctors.stream()
                .filter(d -> d.getUserId().equals(doctorId))
                .findFirst();
    }

    public Optional<Doctor> findByUsername(String username) { 
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
        if (findById(doctor.getUserId()).isPresent()){
             throw new IllegalArgumentException("이미 존재하는 의사 ID입니다: " + doctor.getUserId());
        }
        doctors.add(doctor);
        saveData(); // 추가 후 즉시 저장
        return doctor;
    }
    
    public String getNextDoctorId() {
        String newId = "D" + String.format("%03d", nextIdCounter);
        nextIdCounter++;
        return newId;
    }

    public boolean updateDoctor(Doctor updatedDoctor) {
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getUserId().equals(updatedDoctor.getUserId())) {
                doctors.set(i, updatedDoctor); 
                saveData(); // 업데이트 후 즉시 저장
                return true;
            }
        }
        return false; 
    }
}
