package com.attendance.service;

import com.attendance.model.SchoolClass;
import com.attendance.model.Student;
import com.attendance.repository.AttendanceRecordRepository;
import com.attendance.repository.SchoolClassRepository;
import com.attendance.repository.StudentRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final AttendanceRecordRepository attendanceRepository;

    public StudentService(
            StudentRepository studentRepository,
            SchoolClassRepository schoolClassRepository,
            AttendanceRecordRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public Student findById(Long studentId) {
        return studentRepository.findDetailedById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Student not found."));
    }

    @Transactional
    public void add(String studentNumber, String name, Long classId) {
        if (studentRepository.existsByStudentNumberIgnoreCase(studentNumber.trim())) {
            throw new IllegalArgumentException("That student ID is already registered.");
        }
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Select a valid class."));
        studentRepository.save(new Student(studentNumber.trim(), name.trim(), schoolClass));
    }

    @Transactional
    public void delete(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found.");
        }
        attendanceRepository.deleteAllByStudentId(studentId);
        studentRepository.deleteById(studentId);
    }
}
