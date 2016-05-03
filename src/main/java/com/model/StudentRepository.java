package com.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.content.Student;

public interface StudentRepository extends MongoRepository<Student, String> {
	public Student findByStudentId(String sid);
	public Long deleteByStudentId(String sid);
	public List<Student> findAll();
}
