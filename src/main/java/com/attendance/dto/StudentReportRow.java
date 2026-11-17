package com.attendance.dto;

public record StudentReportRow(
        Long studentId,
        String studentNumber,
        String studentName,
        String className,
        long presentCount,
        long absentCount,
        long lateCount,
        double attendancePercentage) {}
