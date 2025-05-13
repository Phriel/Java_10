package hospital.reservation.controller;

import hospital.reservation.dao.DoctorDao;
import hospital.reservation.dao.ReservationDao;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.exception.RecordNotFoundException;
import hospital.reservation.model.Doctor;
import hospital.reservation.model.Patient;
import hospital.reservation.model.Reservation;
import hospital.reservation.util.DateUtil;
import hospital.reservation.util.ValidationUtil;
import java.time.LocalDate; // LocalDate 사용
import java.time.LocalTime; // LocalTime 사용
import java.util.List;
import java.util.stream.Collectors;

public class ReservationController {
    private final ReservationDao reservationDao;
    private final DoctorDao doctorDao;

    public ReservationController(ReservationDao reservationDao, DoctorDao doctorDao) {
        this.reservationDao = reservationDao;
        this.doctorDao = doctorDao;
    }

    public List<String> getAllDepartmentNames() {
        return doctorDao.findAll().stream()
                .map(Doctor::getDepartmentName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Doctor> getDoctorsByDepartment(String departmentName) {
        if (departmentName == null || departmentName.trim().equalsIgnoreCase("전체") || departmentName.trim().isEmpty()) {
            return doctorDao.findAll();
        } else {
            return doctorDao.findByDepartment(departmentName);
        }
    }
    
    public Doctor getDoctorById(String doctorId) throws RecordNotFoundException, InvalidInputException {
        ValidationUtil.A_validateNotEmpty(doctorId, "의사 ID");
        return doctorDao.findById(doctorId)
                .orElseThrow(() -> new RecordNotFoundException("ID에 해당하는 의사를 찾을 수 없습니다: " + doctorId));
    }

    public Reservation makeReservation(Patient patient, String doctorId, String dateStr, String timeStr)
            throws InvalidInputException, RecordNotFoundException {
        
        ValidationUtil.A_validateNotEmpty(doctorId, "의사 ID");
        ValidationUtil.A_validateDateFormat(dateStr); // 형식만 검증
        ValidationUtil.A_validateTimeFormat(timeStr); // 형식만 검증

        LocalDate requestedDate = DateUtil.B_parseDate(dateStr);
        LocalTime requestedTime = DateUtil.B_parseTime(timeStr);

        if (requestedDate == null || requestedTime == null) { // 파싱 실패 시
            throw new InvalidInputException("날짜 또는 시간 형식이 잘못되었습니다.");
        }

        if (!DateUtil.B_isFutureOrTodayDate(dateStr)) { // 오늘 포함 미래 날짜인지 검증
            throw new InvalidInputException("예약 날짜는 오늘 또는 현재 날짜 이후여야 합니다.");
        }
        
        Doctor doctor = getDoctorById(doctorId); 

        // *** 의사의 진료 가능 시간인지 먼저 확인 ***
        if (!doctor.isAvailable(requestedDate, requestedTime)) {
            throw new InvalidInputException("선택하신 시간(" + dateStr + " " + timeStr + ")은 " + doctor.getName() + " 의사님의 진료 시간이 아니거나 휴진일입니다.");
        }

        // 그 다음, 해당 시간에 다른 예약이 있는지 (중복 예약) 확인
        if (!reservationDao.isTimeSlotAvailable(doctorId, dateStr, timeStr)) {
            throw new InvalidInputException("해당 시간에 이미 다른 예약이 있습니다. 다른 시간을 선택해주세요.");
        }
        
        String reservationId = reservationDao.getNextReservationId();
        Reservation newReservation = new Reservation(reservationId, patient.getUserId(), doctorId, doctor.getDepartmentName(), dateStr, timeStr);
        return reservationDao.addReservation(newReservation);
    }

    public List<Reservation> getReservationsForPatient(Patient patient) {
        return reservationDao.findByPatientId(patient.getUserId());
    }

    public boolean cancelReservation(Patient patient, String reservationId) 
            throws RecordNotFoundException, InvalidInputException {
        ValidationUtil.A_validateNotEmpty(reservationId, "예약 번호");

        Reservation reservation = reservationDao.findById(reservationId)
                .orElseThrow(() -> new RecordNotFoundException("해당 예약번호를 찾을 수 없습니다: " + reservationId));

        if (!reservation.getPatientId().equals(patient.getUserId())) {
            throw new InvalidInputException("본인의 예약만 취소할 수 있습니다.");
        }
        // TODO: 예약 취소 시, 지난 예약은 취소 불가 등의 로직 추가 가능

        return reservationDao.deleteReservation(reservationId);
    }
}