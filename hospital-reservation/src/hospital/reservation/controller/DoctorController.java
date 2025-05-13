package hospital.reservation.controller;

import hospital.reservation.dao.DoctorDao;
import hospital.reservation.dao.ReservationDao;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.exception.RecordNotFoundException;
import hospital.reservation.model.Doctor;
import hospital.reservation.model.Reservation;
import hospital.reservation.util.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

public class DoctorController {
    private DoctorDao doctorDao;
    private ReservationDao reservationDao;

    public DoctorController(DoctorDao doctorDao, ReservationDao reservationDao) {
        this.doctorDao = doctorDao;
        this.reservationDao = reservationDao;
    }

    /**
     * 특정 의사의 예약 목록을 가져옵니다.
     * @param doctorId 의사 ID
     * @return 해당 의사의 예약 리스트
     * @throws RecordNotFoundException 의사 ID가 유효하지 않을 경우
     */
    public List<Reservation> getDoctorSchedule(String doctorId) throws RecordNotFoundException {
        // 의사 존재 여부 확인 (선택적, 호출 전에 확인되었을 수 있음)
        doctorDao.findById(doctorId)
                .orElseThrow(() -> new RecordNotFoundException("ID에 해당하는 의사를 찾을 수 없습니다: " + doctorId));
        
        return reservationDao.findAll().stream()
                .filter(r -> r.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    /**
     * 의사의 진료 가능 시간/스케줄 정보를 업데이트합니다.
     * @param doctorId 의사 ID
     * @param availability 새로운 진료 가능 시간 문자열
     * @return 업데이트 성공 시 true, 실패 시 false
     * @throws RecordNotFoundException 의사 ID가 유효하지 않을 경우
     * @throws InvalidInputException 입력값이 유효하지 않을 경우
     */
    public boolean updateDoctorAvailability(String doctorId, String availability) 
            throws RecordNotFoundException, InvalidInputException {
        ValidationUtil.A_validateNotEmpty(doctorId, "의사 ID");
        // availability 문자열 자체에 대한 복잡한 검증은 여기서는 생략, 필요시 추가
        ValidationUtil.A_validateNotEmpty(availability, "진료 가능 시간 정보");


        Doctor doctor = doctorDao.findById(doctorId)
                .orElseThrow(() -> new RecordNotFoundException("ID에 해당하는 의사를 찾을 수 없습니다: " + doctorId));
        
        doctor.setAvailability(availability); // Doctor 객체의 정보 업데이트
        boolean success = doctorDao.updateDoctor(doctor); // DAO를 통해 파일/DB에 반영
        if(success) {
             // 필요시 DoctorDao.saveData()를 여기서 호출하거나 Main에서 일괄 처리
        }
        return success;
    }
    
    /**
     * ID로 의사 정보를 가져옵니다.
     * @param doctorId 의사 ID
     * @return Doctor 객체
     * @throws RecordNotFoundException 해당 의사가 없을 경우
     */
    public Doctor getDoctorById(String doctorId) throws RecordNotFoundException {
        return doctorDao.findById(doctorId)
                .orElseThrow(() -> new RecordNotFoundException("ID에 해당하는 의사를 찾을 수 없습니다: " + doctorId));
    }
}