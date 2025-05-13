package hospital.reservation.controller;

import hospital.reservation.dao.DoctorDao;
import hospital.reservation.dao.ReservationDao;
import hospital.reservation.exception.InvalidInputException;
import hospital.reservation.model.Doctor;
import hospital.reservation.model.Reservation;
import hospital.reservation.util.ValidationUtil;
import java.util.List;

public class AdminController {
    private DoctorDao doctorDao;
    private ReservationDao reservationDao;

    public AdminController(DoctorDao doctorDao, ReservationDao reservationDao) {
        this.doctorDao = doctorDao;
        this.reservationDao = reservationDao;
    }

    public Doctor addDoctor(String username, String password, String name, String departmentName)
            throws InvalidInputException, IllegalArgumentException {
        ValidationUtil.A_validateNotEmpty(username, "의사 사용자명");
        ValidationUtil.A_validateNotEmpty(password, "의사 비밀번호");
        ValidationUtil.A_validateNotEmpty(name, "의사 이름");
        ValidationUtil.A_validateNotEmpty(departmentName, "진료과");

        String doctorId = doctorDao.getNextDoctorId();
        // Doctor 생성자에 availability 인자 추가 (예: "미설정" 또는 빈 문자열)
        Doctor newDoctor = new Doctor(doctorId, username, password, name, departmentName, "미설정"); 
        return doctorDao.addDoctor(newDoctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorDao.findAll();
    }

    public List<Reservation> getAllReservations() {
        return reservationDao.findAll();
    }
}