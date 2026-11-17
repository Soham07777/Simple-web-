package com.attendance.controller;

import com.attendance.repository.SchoolClassRepository;
import com.attendance.service.AttendanceService;
import com.attendance.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentsController {
    private final StudentService studentService;
    private final SchoolClassRepository schoolClassRepository;
    private final AttendanceService attendanceService;

    public StudentsController(
            StudentService studentService,
            SchoolClassRepository schoolClassRepository,
            AttendanceService attendanceService) {
        this.studentService = studentService;
        this.schoolClassRepository = schoolClassRepository;
        this.attendanceService = attendanceService;
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("classes", schoolClassRepository.findAll());
        return "students";
    }

    @PostMapping("/students")
    public String addStudent(
            @RequestParam String studentNumber,
            @RequestParam String name,
            @RequestParam Long classId,
            RedirectAttributes redirect) {
        try {
            if (studentNumber.isBlank() || studentNumber.length() > 30
                    || name.isBlank() || name.length() > 100) {
                throw new IllegalArgumentException("Enter a student ID (up to 30 characters) and name (up to 100).");
            }
            studentService.add(studentNumber, name, classId);
            redirect.addFlashAttribute("success", "Student added.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/students/{studentId}/delete")
    public String deleteStudent(@PathVariable Long studentId, RedirectAttributes redirect) {
        try {
            studentService.delete(studentId);
            redirect.addFlashAttribute("success", "Student and their attendance records were removed.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/students";
    }

    @GetMapping("/students/{studentId}/history")
    public String studentHistory(@PathVariable Long studentId, Model model) {
        var records = attendanceService.getStudentHistory(studentId);
        model.addAttribute("student", studentService.findById(studentId));
        model.addAttribute("records", records);
        return "student-history";
    }
}
