package com.attendance.controller;

import com.attendance.service.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TeachersController {
    private final TeacherService teacherService;

    public TeachersController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/teachers")
    public String teachers(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "teachers";
    }

    @PostMapping("/teachers")
    public String addTeacher(
            @RequestParam String employeeNumber,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String subject,
            RedirectAttributes redirect) {
        try {
            if (employeeNumber.isBlank() || employeeNumber.length() > 30
                    || name.isBlank() || name.length() > 100
                    || email.isBlank() || email.length() > 120
                    || subject.isBlank() || subject.length() > 80) {
                throw new IllegalArgumentException("Complete each teacher field within its character limit.");
            }
            teacherService.add(employeeNumber, name, email, subject);
            redirect.addFlashAttribute("success", "Teacher added.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/teachers";
    }

    @PostMapping("/teachers/{teacherId}/delete")
    public String deleteTeacher(@PathVariable Long teacherId, RedirectAttributes redirect) {
        try {
            teacherService.delete(teacherId);
            redirect.addFlashAttribute("success", "Teacher removed.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/teachers";
    }
}
