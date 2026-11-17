package com.attendance.config;

import com.attendance.model.AttendanceRecord;
import com.attendance.model.AttendanceStatus;
import com.attendance.model.SchoolClass;
import com.attendance.model.Student;
import com.attendance.model.Teacher;
import com.attendance.repository.AttendanceRecordRepository;
import com.attendance.repository.SchoolClassRepository;
import com.attendance.repository.StudentRepository;
import com.attendance.repository.TeacherRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {
    private final SchoolClassRepository schoolClassRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    public DemoDataInitializer(
            SchoolClassRepository schoolClassRepository,
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            AttendanceRecordRepository attendanceRecordRepository) {
        this.schoolClassRepository = schoolClassRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    @Override
    public void run(String... args) {
        if (schoolClassRepository.count() != 0) {
            return;
        }
        SchoolClass grade8a = schoolClassRepository.save(new SchoolClass("Grade 8A"));
        SchoolClass grade8b = schoolClassRepository.save(new SchoolClass("Grade 8B"));
        teacherRepository.save(
                new Teacher("TCH-001", "Taylor Morgan", "taylor.morgan@example.test", "Mathematics"));
        Student maya = studentRepository.save(new Student("STU-1001", "Maya Chen", grade8a));
        Student noah = studentRepository.save(new Student("STU-1002", "Noah Patel", grade8a));
        studentRepository.save(new Student("STU-1003", "Ava Morgan", grade8b));
        LocalDate today = LocalDate.now();
        attendanceRecordRepository.saveAll(List.of(
                new AttendanceRecord(maya, today, AttendanceStatus.PRESENT),
                new AttendanceRecord(noah, today, AttendanceStatus.LATE)));
    }
}
