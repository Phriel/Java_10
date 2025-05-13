package hospital.reservation.dao;

import hospital.reservation.model.Patient;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDao {
    private final String filePath = "data/patients.csv";
    private final List<Patient> patients = new ArrayList<>();
    private int nextIdCounter;

    public PatientDao() {
        loadData();
        if (patients.isEmpty()) {
            nextIdCounter = 1;
        } else {
            nextIdCounter = patients.stream()
                                  .mapToInt(p -> {
                                      try {
                                          return Integer.parseInt(p.getUserId().substring(1)); // "P" 제거
                                      } catch (NumberFormatException | NullPointerException e) {
                                          System.err.println("!오류(PatientDao): 잘못된 환자 ID 형식 - " + p.getUserId());
                                          return 0; // 오류 시 ID 계산에서 제외 또는 기본값
                                      }
                                  })
                                  .filter(id -> id > 0) // 유효한 ID만 필터링
                                  .max()
                                  .orElse(0) + 1;
        }
    }

    private void loadData() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("경고(PatientDao): " + filePath + " 파일이 존재하지 않습니다. 빈 리스트로 시작합니다.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // 헤더 스킵
            if (line == null) return; // 빈 파일이거나 헤더만 있는 경우

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // 빈 줄은 건너뜁니다.
                String[] data = line.split(",", -1); // -1 to include trailing empty strings
                if (data.length == 6) {
                    patients.add(new Patient(data[0], data[1], data[2], data[3], data[4], data[5]));
                } else {
                     System.err.println("!오류(PatientDao): CSV 데이터 형식 오류 (필드 수 불일치: " + data.length + ") - " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("!오류(PatientDao): 환자 정보 로드 중 - " + e.getMessage());
        }
    }

    public void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("patientId,username,password,name,ssn,phoneNumber\n"); // 헤더
            for (Patient patient : patients) {
                bw.write(patient.toCsvString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("!오류(PatientDao): 환자 정보 저장 중 - " + e.getMessage());
        }
    }

    public Optional<Patient> findById(String patientId) {
        return patients.stream()
                .filter(p -> p.getUserId().equals(patientId))
                .findFirst();
    }

    public Optional<Patient> findByUsername(String username) {
        return patients.stream()
                .filter(p -> p.getUsername().equals(username))
                .findFirst();
    }

    public List<Patient> findAll() {
        return new ArrayList<>(patients); // 방어적 복사
    }

    public Patient addPatient(Patient patient) throws IllegalArgumentException {
        if (findByUsername(patient.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다: " + patient.getUsername());
        }
        patients.add(patient);
        saveData(); // 추가 후 즉시 저장
        return patient;
    }
    
    public String getNextPatientId() {
        // ID 생성 후 카운터 즉시 증가 보장
        String newId = "P" + String.format("%03d", nextIdCounter);
        nextIdCounter++; 
        return newId;
    }

    /**
     * 환자 정보를 삭제합니다.
     * @param patientId 삭제할 환자의 ID
     * @return 삭제 성공 시 true, 해당 환자가 없으면 false
     */
    public boolean deletePatient(String patientId) {
        boolean removed = patients.removeIf(p -> p.getUserId().equals(patientId));
        if (removed) {
            saveData(); // 삭제 후 즉시 저장
        }
        return removed;
    }
}