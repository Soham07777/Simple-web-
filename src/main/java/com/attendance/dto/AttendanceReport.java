package com.attendance.dto;

import java.time.YearMonth;
import java.util.List;

public record AttendanceReport(
        YearMonth month,
        long presentCount,
        long absentCount,
        long lateCount,
        List<StudentReportRow> students) {}
