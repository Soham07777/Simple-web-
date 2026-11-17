package com.attendance;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.attendance.model.AttendanceStatus;
import com.attendance.model.SchoolClass;
import com.attendance.model.Student;
import com.attendance.repository.AttendanceRecordRepository;
import com.attendance.repository.SchoolClassRepository;
import com.attendance.repository.StudentRepository;
import com.attendance.repository.TeacherRepository;
import com.attendance.service.AttendanceService;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentAttendanceIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    void protectedPagesRedirectVisitorsToLogin() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void administratorCanOpenDashboardAndMonthlyReport() throws Exception {
        mockMvc.perform(get("/").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Good morning, teacher")));

        mockMvc.perform(get("/students").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("STUDENT ROSTER")));
        mockMvc.perform(get("/teachers").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("STAFF DIRECTORY")));
        mockMvc.perform(get("/classes").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CLASS DIRECTORY")));

        mockMvc.perform(get("/reports").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Attendance by student")));
    }

    @Test
    void administratorCanManageStudentsAndRecordAttendance() throws Exception {
        SchoolClass schoolClass = schoolClassRepository.findAll().get(0);
        String studentNumber = "IT-" + System.nanoTime();

        mockMvc.perform(post("/students")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("studentNumber", studentNumber)
                        .param("name", "Integration Student")
                        .param("classId", schoolClass.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attribute("success", "Student added."));

        Student student = studentRepository.findAll().stream()
                .filter(item -> item.getStudentNumber().equals(studentNumber))
                .findFirst()
                .orElseThrow();
        LocalDate date = LocalDate.now();

        mockMvc.perform(post("/attendance")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("studentId", student.getId().toString())
                        .param("classId", schoolClass.getId().toString())
                        .param("date", date.toString())
                        .param("status", AttendanceStatus.PRESENT.name()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/attendance")
                        .with(user("admin").roles("ADMIN"))
                        .param("date", date.toString())
                        .param("classId", schoolClass.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(studentNumber)))
                .andExpect(content().string(containsString("PRESENT")));

        mockMvc.perform(get("/reports")
                        .with(user("admin").roles("ADMIN"))
                        .param("month", YearMonth.from(date).toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(studentNumber)));
    }

    @Test
    void administratorCanAddTeacherAndStudentIdsMustBeUnique() throws Exception {
        mockMvc.perform(post("/teachers")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("employeeNumber", "IT-T-" + System.nanoTime())
                        .param("name", "Integration Teacher")
                        .param("email", "integration-" + System.nanoTime() + "@example.test")
                        .param("subject", "Science"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teachers"))
                .andExpect(flash().attribute("success", "Teacher added."));
        org.junit.jupiter.api.Assertions.assertTrue(teacherRepository.count() >= 2);

        SchoolClass schoolClass = schoolClassRepository.findAll().get(0);
        String studentNumber = "DUP-" + System.nanoTime();
        mockMvc.perform(post("/students")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .param("studentNumber", studentNumber)
                .param("name", "First Student")
                .param("classId", schoolClass.getId().toString()));
        mockMvc.perform(post("/students")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("studentNumber", studentNumber)
                        .param("name", "Duplicate Student")
                        .param("classId", schoolClass.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "That student ID is already registered."));
    }

    @Test
    void monthlyReportCalculatesAttendancePercentageAndStudentHistory() throws Exception {
        SchoolClass schoolClass = schoolClassRepository.findAll().get(0);
        String studentNumber = "REPORT-" + System.nanoTime();
        Student student = studentRepository.save(
                new Student(studentNumber, "Report Student", schoolClass));
        LocalDate firstDay = YearMonth.now().minusMonths(1).atDay(1);
        attendanceService.markAttendance(
                student.getId(), schoolClass.getId(), firstDay, AttendanceStatus.PRESENT);
        attendanceService.markAttendance(
                student.getId(), schoolClass.getId(), firstDay.plusDays(1), AttendanceStatus.ABSENT);

        var reportRow = attendanceService.getMonthlyReport(YearMonth.from(firstDay)).students().stream()
                .filter(row -> row.studentNumber().equals(studentNumber))
                .findFirst()
                .orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(50.0, reportRow.attendancePercentage());

        mockMvc.perform(get("/students/{studentId}/history", student.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(studentNumber)));
        mockMvc.perform(get("/reports")
                        .with(user("admin").roles("ADMIN"))
                        .param("month", YearMonth.from(firstDay).toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("50.0%")));
    }

    @Test
    void removingStudentAlsoRemovesTheirAttendance() throws Exception {
        SchoolClass schoolClass = schoolClassRepository.findAll().get(0);
        Student student = studentRepository.save(
                new Student("REMOVE-" + System.nanoTime(), "Remove Me", schoolClass));
        LocalDate today = LocalDate.now();
        attendanceService.markAttendance(
                student.getId(), schoolClass.getId(), today, AttendanceStatus.PRESENT);

        mockMvc.perform(post("/students/{studentId}/delete", student.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"));

        org.junit.jupiter.api.Assertions.assertFalse(studentRepository.existsById(student.getId()));
        org.junit.jupiter.api.Assertions.assertTrue(
                attendanceRecordRepository.findAllByAttendanceDateOrderByStudentNameAsc(today).stream()
                        .noneMatch(record -> record.getStudent().getId().equals(student.getId())));
    }
}
