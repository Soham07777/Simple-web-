package com.attendance.dto;

import com.attendance.model.AttendanceStatus;
import com.attendance.model.Student;

public record AttendanceRow(Student student, AttendanceStatus status) {}
