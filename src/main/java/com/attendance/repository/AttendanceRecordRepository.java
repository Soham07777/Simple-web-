package com.attendance.repository;

import com.attendance.model.AttendanceRecord;
import com.attendance.model.AttendanceStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findAllByAttendanceDateOrderByStudentNameAsc(LocalDate attendanceDate);
    List<AttendanceRecord> findAllByAttendanceDateAndStudentSchoolClassIdOrderByStudentNameAsc(
            LocalDate attendanceDate, Long schoolClassId);
    List<AttendanceRecord> findAllByStudentIdOrderByAttendanceDateDesc(Long studentId);
    List<AttendanceRecord> findAllByAttendanceDateBetweenOrderByAttendanceDateAsc(
            LocalDate startDate, LocalDate endDate);
    Optional<AttendanceRecord> findByStudentIdAndAttendanceDate(Long studentId, LocalDate attendanceDate);
    void deleteAllByStudentId(Long studentId);
    long countByAttendanceDate(LocalDate attendanceDate);
    long countByAttendanceDateAndStatus(LocalDate attendanceDate, AttendanceStatus status);
}
