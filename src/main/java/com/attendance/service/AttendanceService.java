package com.attendance.service;

import com.attendance.dto.AttendanceReport;
import com.attendance.dto.AttendanceRow;
import com.attendance.dto.StudentReportRow;
import com.attendance.model.AttendanceRecord;
import com.attendance.model.AttendanceStatus;
import com.attendance.model.Student;
import com.attendance.repository.AttendanceRecordRepository;
import com.attendance.repository.StudentRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceService {
    private final AttendanceRecordRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public AttendanceService(
            AttendanceRecordRepository attendanceRepository,
            StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<AttendanceRow> getDailyRoster(LocalDate date, Long classId) {
        List<Student> students = classId == null
                ? studentRepository.findAllByOrderByNameAsc()
                : studentRepository.findBySchoolClassIdOrderByNameAsc(classId);
        Map<Long, AttendanceStatus> marked = (classId == null
                ? attendanceRepository.findAllByAttendanceDateOrderByStudentNameAsc(date)
                : attendanceRepository.findAllByAttendanceDateAndStudentSchoolClassIdOrderByStudentNameAsc(
                        date, classId))
                .stream()
                .collect(Collectors.toMap(record -> record.getStudent().getId(), AttendanceRecord::getStatus));
        return students.stream()
                .map(student -> new AttendanceRow(student, marked.get(student.getId())))
                .toList();
    }

    @Transactional
    public void markAttendance(Long studentId, Long classId, LocalDate date, AttendanceStatus status) {
        Student student = studentRepository.findDetailedById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
        if (!student.getSchoolClass().getId().equals(classId)) {
            throw new IllegalArgumentException("The selected student does not belong to this class.");
        }
        AttendanceRecord existing = attendanceRepository.findByStudentIdAndAttendanceDate(studentId, date)
                .orElse(null);
        if (status == null) {
            if (existing != null) {
                attendanceRepository.delete(existing);
            }
            return;
        }
        AttendanceRecord record = existing == null
                ? new AttendanceRecord(student, date, status)
                : existing;
        record.setStatus(status);
        attendanceRepository.save(record);
    }

    @Transactional(readOnly = true)
    public AttendanceReport getMonthlyReport(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        List<AttendanceRecord> records =
                attendanceRepository.findAllByAttendanceDateBetweenOrderByAttendanceDateAsc(start, end);

        Map<Long, List<AttendanceRecord>> byStudent = records.stream()
                .collect(Collectors.groupingBy(record -> record.getStudent().getId()));
        List<StudentReportRow> students = studentRepository.findAllByOrderByNameAsc().stream()
                .map(student -> {
                    List<AttendanceRecord> studentRecords =
                            byStudent.getOrDefault(student.getId(), List.of());
                    long present = countStatus(studentRecords, AttendanceStatus.PRESENT);
                    long absent = countStatus(studentRecords, AttendanceStatus.ABSENT);
                    long late = countStatus(studentRecords, AttendanceStatus.LATE);
                    long marked = present + absent + late;
                    double percentage = marked == 0 ? 0 : (present + late) * 100.0 / marked;
                    return new StudentReportRow(
                            student.getId(),
                            student.getStudentNumber(),
                            student.getName(),
                            student.getSchoolClass().getName(),
                            present,
                            absent,
                            late,
                            percentage);
                })
                .toList();
        return new AttendanceReport(
                month,
                countStatus(records, AttendanceStatus.PRESENT),
                countStatus(records, AttendanceStatus.ABSENT),
                countStatus(records, AttendanceStatus.LATE),
                students);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getStudentHistory(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found.");
        }
        return attendanceRepository.findAllByStudentIdOrderByAttendanceDateDesc(studentId);
    }

    private long countStatus(List<AttendanceRecord> records, AttendanceStatus status) {
        return records.stream().filter(record -> record.getStatus() == status).count();
    }
}
