package net.javaguides.service;

import net.javaguides.dto.StudentDto;

import java.util.List;


public interface StudentService {
    StudentDto createStudent(StudentDto studentDto);
    StudentDto getStudentById(Long studentId);
    List<StudentDto> getAllStudents();
    StudentDto updateStudent(Long studentId, StudentDto updateStudent);
    void deleteStudent(Long studentId);
}
