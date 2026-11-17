package com.attendance.repository;

import com.attendance.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {
    @EntityGraph(attributePaths = "schoolClass")
    List<Student> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = "schoolClass")
    List<Student> findBySchoolClassIdOrderByNameAsc(Long schoolClassId);

    @EntityGraph(attributePaths = "schoolClass")
    @Query("select student from Student student join fetch student.schoolClass where student.id = :id")
    Optional<Student> findDetailedById(@Param("id") Long id);

    boolean existsByStudentNumberIgnoreCase(String studentNumber);
    long countBySchoolClassId(Long schoolClassId);
}
