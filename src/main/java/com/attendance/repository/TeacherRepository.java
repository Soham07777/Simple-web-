package com.attendance.repository;

import com.attendance.model.Teacher;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findAllByOrderByNameAsc();
    boolean existsByEmployeeNumberIgnoreCase(String employeeNumber);
    boolean existsByEmailIgnoreCase(String email);
}
