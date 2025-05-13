package hospital.reservation.dao;

import hospital.reservation.model.Reservation;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationDao {
    private final String filePath = "data/reservations.csv";
    private final List<Reservation> reservations = new ArrayList<>();
    private int nextIdCounter;

    public ReservationDao() {
        loadData();
        if (reservations.isEmpty()) {
            nextIdCounter = 1;
        } else {
            nextIdCounter = reservations.stream()
                                     .mapToInt(r -> {
                                         try {
                                             return Integer.parseInt(r.getReservationId().substring(1)); // "R" 제거
                                         } catch (NumberFormatException | NullPointerException e) {
                                             System.err.println("!오류(ReservationDao): 잘못된 예약 ID 형식 - " + r.getReservationId());
                                             return 0;
                                         }
                                     })
                                     .filter(id -> id > 0)
                                     .max()
                                     .orElse(0) + 1;
        }
    }

    private void loadData() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("경고(ReservationDao): " + filePath + " 파일이 존재하지 않습니다. 빈 리스트로 시작합니다.");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); 
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",", -1);
                if (data.length == 6) {
                    reservations.add(new Reservation(data[0], data[1], data[2], data[3], data[4], data[5]));
                } else {
                    System.err.println("!오류(ReservationDao): CSV 데이터 형식 오류 (필드 수 불일치: " + data.length + ") - " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("!오류(ReservationDao): 예약 정보 로드 중 - " + e.getMessage());
        }
    }

    public void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("reservationId,patientId,doctorId,departmentName,reservationDate,reservationTime\n");
            for (Reservation reservation : reservations) {
                bw.write(reservation.toCsvString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("!오류(ReservationDao): 예약 정보 저장 중 - " + e.getMessage());
        }
    }

    public Optional<Reservation> findById(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst();
    }

    public List<Reservation> findByPatientId(String patientId) {
        return reservations.stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public List<Reservation> findAll() {
        return new ArrayList<>(reservations);
    }

    public boolean isTimeSlotAvailable(String doctorId, String date, String time) {
        return reservations.stream()
                .noneMatch(r -> r.getDoctorId().equals(doctorId) &&
                                r.getReservationDate().equals(date) &&
                                r.getReservationTime().equals(time));
    }

    public Reservation addReservation(Reservation reservation) {
        reservations.add(reservation);
        saveData(); // 추가 후 즉시 저장
        return reservation;
    }
    
    public String getNextReservationId() {
        String newId = "R" + String.format("%04d", nextIdCounter);
        nextIdCounter++;
        return newId;
    }

    public boolean deleteReservation(String reservationId) {
        boolean removed = reservations.removeIf(r -> r.getReservationId().equals(reservationId));
        if (removed) {
            saveData(); // 삭제 후 즉시 저장
        }
        return removed;
    }

    /**
     * 특정 환자의 모든 예약을 삭제합니다.
     * @param patientId 삭제할 예약들의 환자 ID
     * @return 하나 이상의 예약이 삭제되었으면 true, 아니면 false
     */
    public boolean deleteReservationsByPatientId(String patientId) {
        boolean removed = reservations.removeIf(r -> r.getPatientId().equals(patientId));
        if (removed) {
            saveData(); // 변경사항이 있으면 즉시 저장
        }
        return removed;
    }
}