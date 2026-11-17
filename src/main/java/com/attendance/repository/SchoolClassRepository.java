package com.attendance.repository;

import com.attendance.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    boolean existsByNameIgnoreCase(String name);
}
