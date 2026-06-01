package net.javaguides.service.impl;

import net.javaguides.dto.StudentDto;
import net.javaguides.entity.Student;
import net.javaguides.mapper.StudentMapper;
import net.javaguides.repository.StudentRepository;
import net.javaguides.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    private StudentRepository studentRepository;

    @Override
    public StudentDto createStudent(StudentDto studentDto) {
        Student student = StudentMapper.mapToStudent(studentDto);
        Student savedStudent = studentRepository.save(student);
        return StudentMapper.mapToStudentDto(savedStudent);
    }
}
