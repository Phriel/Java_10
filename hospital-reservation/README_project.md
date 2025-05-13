# 병원 예약 시스템: 프로젝트 구조 및 주요 로직 설명

## 1. 프로젝트 목표

본 프로젝트는 환자, 의사, 관리자라는 세 가지 주요 사용자 역할을 가지는 병원 예약 시스템을 구현하는 것을 목표로 합니다. 주요 기능으로는 사용자 인증, 환자의 진료 예약 및 관리, 의사의 스케줄 관리, 관리자의 의사 및 예약 데이터 관리가 있습니다. 데이터는 CSV 파일을 통해 영속적으로 저장되며, 객체지향 프로그래밍 원칙(상속, 다형성, 추상화, 캡슐화)을 적용하여 설계되었습니다. 사용자 인터페이스는 콘솔(Console)과 기본적인 AWT GUI를 모두 지원하는 것을 목표로 합니다.

## 2. 디렉토리 구조

프로젝트의 전체적인 디렉토리 구조는 다음과 같습니다.
'''
hospital-reservation/           # 프로젝트 최상위 폴더   
├── data/                        # CSV 데이터 파일 저장 위치   
│   ├── patients.csv             # 환자 정보   
│   ├── doctors.csv              # 의사 정보   
│   ├── reservations.csv         # 예약 정보   
│   └── admins.csv               # 관리자 정보   
├── src/                         # 자바 소스 코드 루트 폴더   
│   └── hospital/reservation/    # 최상위 자바 패키지   
│       ├── model/               # 데이터 모델 클래스   
│       ├── dao/                 # 데이터 접근 객체 (CSV 처리)    
│       ├── controller/          # 비즈니스 로직 및 요청 처리   
│       ├── gui/                 # AWT GUI 컴포넌트    
│       │   ├── common/          # GUI 공통 요소   
│       │   ├── auth/            # 인증 관련 GUI   
│       │   ├── patient/         # 환자용 GUI    
│       │   ├── doctor/          # 의사용 GUI    
│       │   └── admin/           # 관리자용 GUI    
│       ├── view/                # 콘솔 UI     
│       ├── util/                # 유틸리티 클래스     
│       ├── exception/           # 사용자 정의 예외 클래스     
│       └── Main.java            # 애플리케이션 시작점     
└── README_project.md                    # 프로젝트 설명 파일     
'''
### 2.1. `data/` 폴더
CSV(Comma-Separated Values) 파일 형태로 애플리케이션의 데이터를 저장합니다.
   - `patients.csv`: 환자 정보 (ID, 사용자명, 비밀번호, 이름, 주민번호, 전화번호)
   - `doctors.csv`: 의사 정보 (ID, 사용자명, 비밀번호, 이름, 진료과, 진료가능시간 문자열)
   - `admins.csv`: 관리자 정보 (ID, 사용자명, 비밀번호, 이름)
   - `reservations.csv`: 예약 정보 (예약ID, 환자ID, 의사ID, 진료과, 예약날짜, 예약시간)

### 2.2. `src/hospital/reservation/` 패키지
모든 자바 소스 코드가 이 패키지 하위에 구성됩니다.

## 3. 주요 패키지 및 파일 설명

### 3.1. `hospital.reservation.model` 패키지
애플리케이션에서 사용되는 데이터의 구조를 정의하는 클래스(VO 또는 DTO 역할)들이 위치합니다.

   - **`User.java` (추상 클래스)**
     - **역할**: `Patient`, `Doctor`, `Admin` 클래스의 공통된 속성(ID, 사용자명, 비밀번호, 이름)과 행위를 정의하는 부모 클래스입니다.
     - **주요 필드**: `protected String userId`, `username`, `password`, `name`
     - **주요 추상 메소드**: `getUserType()`, `toCsvString()` (하위 클래스에서 구체적인 구현 제공)
     - **OOP 적용**: 추상화를 통해 사용자의 공통 개념을 표현하고, 상속의 기반을 제공합니다.

   - **`Patient.java`**
     - **역할**: 환자 정보를 나타내는 클래스 (`User` 상속).
     - **주요 필드**: `private String ssn` (주민번호), `phoneNumber` (연락처)
     - **주요 메소드**: `toCsvString()` (CSV 저장을 위한 문자열 변환), getter/setter.
     - **OOP 적용**: `User` 클래스로부터 공통 속성 상속.

   - **`Doctor.java`**
     - **역할**: 의사 정보를 나타내는 클래스 (`User` 상속).
     - **주요 필드**: `private String departmentName` (진료과), `availability` (진료 가능 시간 문자열)
     - **주요 메소드**:
        - `toCsvString()`
        - **`isAvailable(LocalDate requestedDate, LocalTime requestedTime)`**: 의사의 `availability` 문자열을 파싱하여 특정 날짜와 시간에 진료가 가능한지 판단하는 **중요 로직** 포함.
     - **OOP 적용**: `User` 클래스로부터 상속. `isAvailable` 메소드는 의사 스케줄 관련 로직을 캡슐화.

   - **`Admin.java`**
     - **역할**: 관리자 정보를 나타내는 클래스 (`User` 상속).
     - **주요 메소드**: `toCsvString()`
     - **OOP 적용**: `User` 클래스로부터 상속.

   - **`Reservation.java`**
     - **역할**: 예약 정보를 나타내는 클래스.
     - **주요 필드**: `reservationId`, `patientId`, `doctorId`, `departmentName`, `reservationDate`, `reservationTime`.
     - **주요 메소드**: `toCsvString()`, getter.

### 3.2. `hospital.reservation.dao` 패키지
DAO(Data Access Object) 패턴을 사용하여 CSV 파일과의 데이터 CRUD(Create, Read, Update, Delete) 작업을 처리합니다. 각 DAO는 프로그램 시작 시 CSV 파일에서 데이터를 읽어 메모리 내 `List` 객체에 저장하고, 프로그램 종료 시 또는 데이터 변경 시 다시 파일에 저장합니다.

   - **`PatientDao.java`**
     - **역할**: `patients.csv` 파일에 대한 CRUD 및 환자 ID 생성을 담당.
     - **주요 메소드**: `loadData()`, `saveData()`, `findById()`, `findByUsername()`, `addPatient()`, `deletePatient()`, `getNextPatientId()`.

   - **`DoctorDao.java`**
     - **역할**: `doctors.csv` 파일에 대한 CRUD 및 의사 ID 생성 담당. `availability` 정보 포함.
     - **주요 메소드**: `loadData()`, `saveData()`, `findById()`, `findByUsername()` (의사 로그인용), `findByDepartment()`, `addDoctor()`, `updateDoctor()` (주로 `availability` 변경 시), `getNextDoctorId()`.

   - **`AdminDao.java`**
     - **역할**: `admins.csv` 파일에 대한 CRUD 담당.
     - **주요 메소드**: `loadData()`, `saveData()`, `findByUsername()` (관리자 로그인용).

   - **`ReservationDao.java`**
     - **역할**: `reservations.csv` 파일에 대한 CRUD 및 예약 ID 생성 담당.
     - **주요 메소드**: `loadData()`, `saveData()`, `findById()`, `findByPatientId()`, `findAll()`, `isTimeSlotAvailable()` (중복 예약 방지 로직), `addReservation()`, `deleteReservation()`, `deleteReservationsByPatientId()` (회원탈퇴 시 사용), `getNextReservationId()`.

### 3.3. `hospital.reservation.controller` 패키지
사용자 인터페이스(View/GUI)와 데이터 처리(DAO) 계층 사이에서 비즈니스 로직을 처리하고 흐름을 제어합니다.

   - **`AuthController.java`**
     - **역할**: 사용자 인증(로그인/로그아웃), 환자 회원가입, 환자 회원탈퇴 로직을 담당.
     - **주요 메소드**:
        - `login(username, password)`: `PatientDao`, `DoctorDao`, `AdminDao`를 순차적으로 확인하여 로그인 처리. 성공 시 해당 `User` 타입 객체 반환.
        - `registerPatient(...)`: 새 환자 정보 유효성 검사 후 `PatientDao` 통해 등록.
        - `withdrawPatientAccount(...)`: 환자 본인 확인(비밀번호) 후, `ReservationDao`에서 해당 환자 예약 삭제, `PatientDao`에서 환자 정보 삭제.
     - **중요 로직**: 여러 DAO와 상호작용하여 사용자 계정 및 인증 관련 핵심 기능 수행.

   - **`ReservationController.java`**
     - **역할**: 환자의 예약 생성, 조회, 취소 등 예약 관련 핵심 로직 처리.
     - **주요 메소드**: `getAllDepartmentNames()`, `getDoctorsByDepartment()`, `getDoctorById()`, `makeReservation(...)`, `getReservationsForPatient()`, `cancelReservation()`.
     - **중요 로직**:
        - `makeReservation(...)`: **의사 스케줄 검증 (`doctor.isAvailable()`)**과 **중복 예약 검증 (`ReservationDao.isTimeSlotAvailable()`)**을 모두 수행한 후 예약 생성.

   - **`AdminController.java`**
     - **역할**: 관리자 기능(의사 추가, 전체 의사/예약 목록 조회) 관련 로직 처리.
     - **주요 메소드**: `addDoctor(...)`, `getAllDoctors()`, `getAllReservations()`.

   - **`DoctorController.java`**
     - **역할**: 로그인한 의사 전용 기능(자신의 스케줄 조회, 진료 가능 시간 업데이트) 관련 로직 처리.
     - **주요 메소드**: `getDoctorSchedule(...)` (`ReservationDao` 사용), `updateDoctorAvailability(...)` (`DoctorDao` 사용), `getDoctorById()`.

### 3.4. `hospital.reservation.exception` 패키지
애플리케이션 내에서 특정 오류 상황을 나타내기 위한 사용자 정의 예외 클래스들이 위치합니다.
   - **`AuthenticationException.java`**: 로그인 실패 등 인증 관련 오류.
   - **`InvalidInputException.java`**: 사용자 입력값이 유효하지 않은 경우.
   - **`RecordNotFoundException.java`**: 조회하려는 데이터가 존재하지 않는 경우.

### 3.5. `hospital.reservation.util` 패키지
여러 곳에서 공통적으로 사용될 수 있는 보조 기능 클래스들이 위치합니다.
   - **`DateUtil.java`**: 날짜 및 시간 문자열 파싱, 형식 변환, 유효성 검사, 한국어 요일 변환 등의 기능을 제공. `Doctor.isAvailable()` 로직에 활용.
   - **`InputUtil.java`**: 콘솔 환경에서 사용자 입력을 `Scanner`를 통해 안전하게 받기 위한 유틸리티.
   - **`ValidationUtil.java`**: 입력값의 기본적인 유효성(빈 값 여부, 특정 형식 등)을 검사하는 정적 메소드 제공.

### 3.6. `hospital.reservation.view` 패키지 (콘솔 UI)
   - **`ConsoleView.java`**
     - **역할**: 콘솔(터미널) 환경에서 사용자 인터페이스를 제공. 사용자 입력을 받아 해당 컨트롤러에 전달하고, 그 결과를 출력.
     - **주요 메소드**: `start()` (메인 루프), `showMainMenu()`, `showPatientMenu()`, `showAdminMenu()`, `showDoctorMenu()`, 각 메뉴 선택에 따른 `handle...()` 메소드들.
     - **중요 로직**: `Main.currentUser` 객체의 실제 타입(`instanceof`)을 확인하여 해당 사용자 역할에 맞는 메뉴를 동적으로 보여줌. 컨트롤러 호출 시 발생할 수 있는 예외를 `try-catch`로 처리하여 사용자에게 오류 메시지 표시.

### 3.7. `hospital.reservation.gui` 패키지 (AWT GUI)
AWT(Abstract Window Toolkit)를 사용한 그래픽 사용자 인터페이스 관련 클래스들이 위치합니다.

   - **`gui.common`**
     - **`AppGUILauncher.java`**: GUI 애플리케이션의 실제 시작점. `MainFrame`을 생성하고 화면에 표시.
     - **`MainFrame.java`**: 애플리케이션의 주 윈도우(`Frame`). 로그인 상태 및 사용자 유형에 따라 중앙 패널의 내용을 `CardLayout`을 이용해 동적으로 교체 (`PatientDashboardPanel`, `AdminDashboardPanel`, `DoctorDashboardPanel` 등). `LoginDialog` 호출, 로그아웃 처리.

   - **`gui.auth`**
     - **`LoginDialog.java`**: 사용자(환자, 의사, 관리자)의 아이디와 비밀번호를 입력받는 모달 다이얼로그. `AuthController.login()` 호출.
     - **`RegisterDialog.java`**: 환자 회원가입 정보를 입력받는 모달 다이얼로그. `AuthController.registerPatient()` 호출.

   - **`gui.patient`**
     - **`PatientDashboardPanel.java`**: 환자로 로그인 시 보여지는 메인 패널. 예약 관련 기능(조회, 생성, 취소) 및 회원탈퇴, 로그아웃 버튼 제공.
     - **`MakeReservationDialog.java`**: 진료 예약을 위한 정보(진료과, 의사, 날짜, 시간)를 선택/입력하는 모달 다이얼로그. `ReservationController.makeReservation()` 호출.
     - **`ViewPatientReservationsPanel.java`**: 환자 자신의 예약 목록을 `TextArea` 등에 표시하고, 특정 예약을 취소할 수 있는 인터페이스 제공 (또는 `PatientDashboardPanel`에 통합).
     - **`WithdrawConfirmDialog.java`**: 환자 회원탈퇴 시 비밀번호를 재입력받아 확인하는 모달 다이얼로그. `AuthController.withdrawPatientAccount()` 호출.

   - **`gui.admin`**
     - **`AdminDashboardPanel.java`**: 관리자로 로그인 시 보여지는 메인 패널. 의사 추가, 전체 의사 목록 조회, 전체 예약 목록 조회, 로그아웃 버튼 제공.
     - **`AddDoctorDialog.java`**: 새로운 의사 정보(이름, 아이디/비번, 진료과, 초기 `availability`)를 입력받는 모달 다이얼로그. `AdminController.addDoctor()` 호출.
     - **`ViewDoctorsAdminPanel.java`**: 시스템에 등록된 모든 의사 목록을 `TextArea` 등에 표시.
     - **`ViewAllReservationsPanel.java`**: 시스템의 모든 예약 목록을 `TextArea` 등에 표시.

   - **`gui.doctor`**
     - **`DoctorDashboardPanel.java`**: 의사로 로그인 시 보여지는 메인 패널. 자신의 스케줄 조회, 진료 가능 시간 설정, 로그아웃 버튼 제공.
     - **`ViewDoctorSchedulePanel.java`**: 해당 의사에게 예약된 스케줄 목록을 `TextArea` 등에 표시.
     - **`ManageAvailabilityDialog.java`**: 의사가 자신의 `availability` 문자열을 수정하고 저장하는 모달 다이얼로그. `DoctorController.updateDoctorAvailability()` 호출.

### 3.8. `hospital.reservation.Main.java` (애플리케이션 진입점)
   - **역할**: 프로그램의 시작점 (`public static void main(String[] args)` 메소드 포함).
     - 모든 DAO 객체와 Controller 객체를 중앙에서 생성하고 의존성을 주입.
     - 현재 로그인한 사용자를 저장하는 `public static User currentUser` 변수 관리.
     - `useGuiMode`와 같은 플래그를 통해 콘솔 모드로 실행할지 GUI 모드(`AppGUILauncher` 호출)로 실행할지 결정.
     - 프로그램 종료 시 `saveAllData()` 메소드를 호출하여 메모리상의 모든 변경사항(환자, 의사, 관리자, 예약 정보)을 각 CSV 파일에 일괄 저장.
   - **중요 로직**: 애플리케이션 전역에서 사용될 핵심 객체들의 생명주기를 관리하고, 프로그램의 시작과 종료 시 필요한 작업을 수행.

## 4. 주요 실행 흐름 (예시: 환자의 의사 스케줄 확인 후 예약)

1.  **애플리케이션 시작 (`Main.java`)**: `patientDao`, `doctorDao`, `reservationDao`, `adminDao` 및 모든 컨트롤러 객체들이 초기화됩니다. `useGuiMode`가 `true`이면 `AppGUILauncher.main()`을 호출합니다.
2.  **GUI 실행 (`AppGUILauncher.java` -> `MainFrame.java`)**: `MainFrame` 인스턴스가 생성되고 화면에 표시됩니다. `MainFrame`은 즉시 `showLoginDialog()`를 호출합니다.
3.  **로그인 (`LoginDialog.java`)**: 환자가 아이디/비밀번호를 입력하고 "로그인" 버튼을 클릭합니다.
    - `LoginDialog`는 `AuthController.login()`을 호출합니다.
    - `AuthController`는 `PatientDao`를 통해 사용자 정보를 확인하고, 성공 시 `Patient` 객체를 반환합니다.
    - `Main.currentUser`에 로그인한 `Patient` 객체가 저장됩니다.
    - `LoginDialog`는 닫히고, `MainFrame`은 `loggedInUser`가 `Patient`임을 확인한 후 `PatientDashboardPanel`을 중앙 패널에 표시합니다.
4.  **예약하기 선택 (`PatientDashboardPanel.java`)**: 환자가 "진료과/의사 조회 및 예약하기" 버튼을 클릭합니다.
    - `MakeReservationDialog`가 생성되어 모달로 표시됩니다. 이때 `ReservationController.getAllDepartmentNames()`를 호출하여 진료과 목록을 가져와 `Choice` 컴포넌트에 채웁니다.
5.  **의사 및 시간 선택 (`MakeReservationDialog.java`)**:
    - 환자가 진료과를 선택하면, 해당 진료과의 의사 목록(`ReservationController.getDoctorsByDepartment()`)이 `Choice` 컴포넌트에 업데이트됩니다. 이때 의사의 `availability` 정보도 함께 (또는 별도로) 표시될 수 있습니다(현재 GUI 구현에서는 의사 이름과 ID만 표시).
    - 환자가 의사, 날짜, 시간을 선택합니다.
6.  **예약 실행 (`MakeReservationDialog.java` -> `ReservationController.makeReservation()`)**:
    - "예약하기" 버튼을 클릭하면, `ReservationController.makeReservation()` 메소드가 호출됩니다.
    - **(핵심 로직)**:
        1.  입력된 날짜/시간의 유효성 검사 (`DateUtil`, `ValidationUtil`).
        2.  선택된 `doctorId`로 `DoctorDao.findById()`를 통해 `Doctor` 객체를 가져옵니다.
        3.  `doctor.isAvailable(요청날짜, 요청시간)`을 호출하여 의사의 `availability` 문자열(근무 요일/시간, 휴진일)을 기준으로 1차 예약 가능 여부를 판단합니다. 가능하지 않으면 `InvalidInputException` 발생.
        4.  1차 통과 시, `ReservationDao.isTimeSlotAvailable(doctorId, 날짜, 시간)`를 호출하여 해당 시간대에 이미 다른 예약이 있는지 (중복 예약) 2차 검증합니다. 중복 시 `InvalidInputException` 발생.
        5.  모든 검증 통과 시, `Reservation` 객체를 생성하고 `ReservationDao.addReservation()`을 호출하여 예약을 저장합니다 (내부에서 `saveData()` 호출로 `reservations.csv` 즉시 업데이트).
    - `MakeReservationDialog`는 성공 또는 실패 메시지를 `Label`에 표시하고, 성공 시 다이얼로그를 닫습니다.
7.  **로그아웃 (`PatientDashboardPanel.java`)**: "로그아웃" 버튼 클릭 시 `MainFrame.showLoginScreenAndRetryLogin()`이 호출되어 `Main.currentUser`를 `null`로 만들고 다시 `LoginDialog`를 표시합니다.
8.  **애플리케이션 종료 (`MainFrame.java`)**: 창닫기 버튼 클릭 시 `Main.saveAllData()`가 호출되어 모든 변경사항이 CSV 파일에 저장된 후 프로그램이 종료됩니다.

## 5. 객체지향 설계 원칙 적용 요약

- **추상화 (Abstraction)**: `User` 추상 클래스를 통해 사용자의 공통적인 개념을 정의하고, 각 클래스(DAO, Controller, GUI 컴포넌트 등)는 자신의 역할에 맞는 인터페이스(public 메소드)를 외부에 제공하여 내부 구현을 숨깁니다.
- **캡슐화 (Encapsulation)**: 모든 모델 클래스의 필드(데이터)는 `private` 또는 `protected`로 보호되며, 오직 `public`으로 공개된 메소드를 통해서만 접근 및 수정이 가능합니다. 각 클래스는 자신의 상태와 행위를 스스로 관리합니다.
- **상속 (Inheritance)**: `Patient`, `Doctor`, `Admin` 클래스가 `User` 추상 클래스를 상속받아 `User`의 공통 필드와 메소드를 재사용하고, 각자의 고유한 특성을 확장합니다.
- **다형성 (Polymorphism)**: `Main.currentUser` 변수는 `User` 타입으로 선언되지만, 실제로는 `Patient`, `Doctor`, `Admin` 등 다양한 하위 타입의 객체를 가리킬 수 있습니다. `instanceof` 연산자를 통해 실제 객체 타입을 확인하여 분기 처리를 하거나, `User`에 정의된 (추상) 메소드를 하위 클래스에서 오버라이드하여 `currentUser.메소드()` 호출 시 각 객체 타입에 맞는 행위가 실행되도록 할 수 있습니다 (예: `getUserType()`, `toCsvString()`). GUI에서 사용자 유형에 따라 다른 대시보드 패널을 보여주는 것도 다형성의 한 예입니다.
