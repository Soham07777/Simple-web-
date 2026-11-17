package com.attendance.controller;

import com.attendance.model.AttendanceStatus;
import com.attendance.repository.SchoolClassRepository;
import com.attendance.service.AttendanceService;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final SchoolClassRepository schoolClassRepository;

    public AttendanceController(
            AttendanceService attendanceService,
            SchoolClassRepository schoolClassRepository) {
        this.attendanceService = attendanceService;
        this.schoolClassRepository = schoolClassRepository;
    }

    @GetMapping("/attendance")
    public String attendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long classId,
            Model model) {
        LocalDate selectedDate = date == null ? LocalDate.now() : date;
        model.addAttribute("date", selectedDate);
        model.addAttribute("classId", classId);
        model.addAttribute("classes", schoolClassRepository.findAll());
        model.addAttribute("students", attendanceService.getDailyRoster(selectedDate, classId));
        model.addAttribute("statuses", AttendanceStatus.values());
        return "attendance";
    }

    @PostMapping("/attendance")
    public String markAttendance(
            @RequestParam Long studentId,
            @RequestParam Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AttendanceStatus status,
            RedirectAttributes redirect) {
        try {
            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Attendance cannot be recorded for a future date.");
            }
            attendanceService.markAttendance(studentId, classId, date, status);
            redirect.addFlashAttribute("success", "Attendance saved.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/attendance?date=" + date + "&classId=" + classId;
    }

    @GetMapping("/reports")
    public String reports(@RequestParam(required = false) String month, Model model) {
        YearMonth selectedMonth;
        try {
            selectedMonth = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month);
        } catch (java.time.DateTimeException exception) {
            selectedMonth = YearMonth.now();
            model.addAttribute("reportNotice", "Invalid month format. Showing the current month.");
        }
        model.addAttribute("report", attendanceService.getMonthlyReport(selectedMonth));
        return "reports";
    }
}
