package hospital.reservation.controller;

import hospital.reservation.dao.AdminDao;
import hospital.reservation.dao.DoctorDao; 
import hospital.reservation.dao.PatientDao;
import hospital.reservation.dao.ReservationDao; // ReservationDao import
import hospital.reservation.exception.AuthenticationException;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.exception.RecordNotFoundException;
import hospital.reservation.model.Admin;
import hospital.reservation.model.Doctor; 
import hospital.reservation.model.Patient;
import hospital.reservation.model.User;
import hospital.reservation.util.ValidationUtil;
import java.util.Optional;

public class AuthController {
    private final PatientDao patientDao;
    private final AdminDao adminDao;    
    private final DoctorDao doctorDao;  
    private final ReservationDao reservationDao; // ReservationDao 필드 선언 및 final 추가

    public AuthController(PatientDao patientDao, AdminDao adminDao, DoctorDao doctorDao, ReservationDao reservationDao) { 
        this.patientDao = patientDao;
        this.adminDao = adminDao;
        this.doctorDao = doctorDao; 
        this.reservationDao = reservationDao; // 생성자에서 초기화
    }

    public User login(String username, String password) throws AuthenticationException, InvalidInputException {
        ValidationUtil.A_validateNotEmpty(username, "사용자 아이디");
        ValidationUtil.A_validateNotEmpty(password, "비밀번호");

        Optional<Patient> patientOpt = patientDao.findByUsername(username);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            if (patient.getPassword().equals(password)) {
                return patient;
            }
        }

        Optional<Doctor> doctorOpt = doctorDao.findByUsername(username);
        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            if (doctor.getPassword().equals(password)) {
                return doctor;
            }
        }
        
        Optional<Admin> adminOpt = adminDao.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getPassword().equals(password)) {
                return admin;
            }
        }

        throw new AuthenticationException("사용자 이름 또는 비밀번호가 일치하지 않거나 존재하지 않는 계정입니다.");
    }

    public Patient registerPatient(String username, String password, String name, String ssn, String phoneNumber)
            throws InvalidInputException, IllegalArgumentException {
        ValidationUtil.A_validateNotEmpty(username, "사용자 아이디");
        ValidationUtil.A_validateNotEmpty(password, "비밀번호");
        ValidationUtil.A_validateNotEmpty(name, "이름");
        ValidationUtil.A_validateSsnFormat(ssn);
        ValidationUtil.A_validatePhoneNumberFormat(phoneNumber);

        String patientId = patientDao.getNextPatientId();
        Patient newPatient = new Patient(patientId, username, password, name, ssn, phoneNumber);
        
        // addPatient 내부에서 중복 사용자 이름 체크 및 예외 발생
        return patientDao.addPatient(newPatient); 
    }

    /**
     * 환자 회원 탈퇴를 처리합니다.
     * @param patientId 탈퇴할 환자 ID
     * @param passwordToConfirm 비밀번호 확인
     * @return 탈퇴 성공 시 true
     * @throws AuthenticationException 비밀번호 불일치 시
     * @throws RecordNotFoundException 해당 환자 ID가 없을 경우
     * @throws InvalidInputException 입력값이 유효하지 않을 경우
     */
    public boolean withdrawPatientAccount(String patientId, String passwordToConfirm)
            throws AuthenticationException, RecordNotFoundException, InvalidInputException {
        ValidationUtil.A_validateNotEmpty(patientId, "환자 ID");
        ValidationUtil.A_validateNotEmpty(passwordToConfirm, "확인용 비밀번호");

        Patient patient = patientDao.findById(patientId)
                .orElseThrow(() -> new RecordNotFoundException("탈퇴할 환자 정보를 찾을 수 없습니다. ID: " + patientId));

        if (!patient.getPassword().equals(passwordToConfirm)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다. 탈퇴할 수 없습니다.");
        }

        // 1. 환자의 모든 예약 정보 삭제
        // deleteReservationsByPatientId는 boolean을 반환하지만, 여기서는 결과 처리가 필수는 아님
        reservationDao.deleteReservationsByPatientId(patientId);
        // (ReservationDao의 deleteReservationsByPatientId 내부에서 saveData 호출됨)

        // 2. 환자 정보 삭제
        boolean patientDeleted = patientDao.deletePatient(patientId);
        // (PatientDao의 deletePatient 내부에서 saveData 호출됨)
        
        if (!patientDeleted) {
            // 이론적으로는 patientDao.findById에서 환자를 찾았으므로 삭제는 성공해야 함
            // 만약의 경우를 대비한 로그 또는 예외 처리
            System.err.println("!경고(AuthController): 환자 삭제에 실패했습니다. ID: " + patientId);
            // throw new RuntimeException("환자 정보 삭제에 실패했습니다."); // 또는 다른 적절한 예외
        }
        
        return patientDeleted;
    }
}