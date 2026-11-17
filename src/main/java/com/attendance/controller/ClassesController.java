package com.attendance.controller;

import com.attendance.model.SchoolClass;
import com.attendance.repository.SchoolClassRepository;
import com.attendance.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClassesController {
    private final SchoolClassRepository schoolClassRepository;
    private final StudentRepository studentRepository;

    public ClassesController(
            SchoolClassRepository schoolClassRepository,
            StudentRepository studentRepository) {
        this.schoolClassRepository = schoolClassRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/classes")
    public String classes(Model model) {
        model.addAttribute("classes", schoolClassRepository.findAll().stream()
                .map(schoolClass -> new ClassSummary(
                        schoolClass, studentRepository.countBySchoolClassId(schoolClass.getId())))
                .toList());
        return "classes";
    }

    @PostMapping("/classes")
    public String addClass(@RequestParam String name, RedirectAttributes redirect) {
        String className = name.trim();
        if (className.isBlank() || className.length() > 60) {
            redirect.addFlashAttribute("error", "Enter a class name of up to 60 characters.");
        } else if (schoolClassRepository.existsByNameIgnoreCase(className)) {
            redirect.addFlashAttribute("error", "That class already exists.");
        } else {
            schoolClassRepository.save(new SchoolClass(className));
            redirect.addFlashAttribute("success", "Class added.");
        }
        return "redirect:/classes";
    }

    public record ClassSummary(SchoolClass schoolClass, long studentCount) {}
}
