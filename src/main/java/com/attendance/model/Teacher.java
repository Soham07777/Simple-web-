package com.attendance.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 30)
    @Column(name = "employee_number", nullable = false, unique = true, length = 30)
    private String employeeNumber;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String subject;

    protected Teacher() {}

    public Teacher(String employeeNumber, String name, String email, String subject) {
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.email = email;
        this.subject = subject;
    }

    public Long getId() { return id; }
    public String getEmployeeNumber() { return employeeNumber; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSubject() { return subject; }
}
