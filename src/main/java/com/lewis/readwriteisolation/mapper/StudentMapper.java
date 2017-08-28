package com.lewis.readwriteisolation.mapper;


import com.lewis.readwriteisolation.entity.Student;

import java.util.List;

public interface StudentMapper {
    
    Integer insert(Student s);
    
    List<Student> findAll();
    
    List<Student> findByStudentIds(List<Integer> studentIds);

}
