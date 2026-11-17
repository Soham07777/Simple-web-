package com.attendance.controller;

import com.attendance.model.AttendanceStatus;
import com.attendance.repository.AttendanceRecordRepository;
import com.attendance.repository.SchoolClassRepository;
import com.attendance.repository.StudentRepository;
import com.attendance.repository.TeacherRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final AttendanceRecordRepository attendanceRepository;

    public DashboardController(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            SchoolClassRepository schoolClassRepository,
            AttendanceRecordRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today);
        model.addAttribute("studentCount", studentRepository.count());
        model.addAttribute("teacherCount", teacherRepository.count());
        model.addAttribute("classCount", schoolClassRepository.count());
        model.addAttribute("markedCount", attendanceRepository.countByAttendanceDate(today));
        model.addAttribute("presentCount",
                attendanceRepository.countByAttendanceDateAndStatus(today, AttendanceStatus.PRESENT));
        model.addAttribute("absentCount",
                attendanceRepository.countByAttendanceDateAndStatus(today, AttendanceStatus.ABSENT));
        model.addAttribute("lateCount",
                attendanceRepository.countByAttendanceDateAndStatus(today, AttendanceStatus.LATE));
        return "dashboard";
    }
}
