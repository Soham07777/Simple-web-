package com.attendance.service;

import com.attendance.model.Teacher;
import com.attendance.repository.TeacherRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public void add(String employeeNumber, String name, String email, String subject) {
        if (teacherRepository.existsByEmployeeNumberIgnoreCase(employeeNumber.trim())) {
            throw new IllegalArgumentException("That employee ID is already registered.");
        }
        if (!email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid teacher email address.");
        }
        if (teacherRepository.existsByEmailIgnoreCase(email.trim())) {
            throw new IllegalArgumentException("That teacher email is already registered.");
        }
        teacherRepository.save(new Teacher(
                employeeNumber.trim(), name.trim(), email.trim(), subject.trim()));
    }

    @Transactional
    public void delete(Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new IllegalArgumentException("Teacher not found.");
        }
        teacherRepository.deleteById(teacherId);
    }
}
