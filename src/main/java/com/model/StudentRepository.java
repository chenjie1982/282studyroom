package com.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.content.Student;

public interface StudentRepository extends MongoRepository<Student, String> {
	public Student findById(String sid);
	public Long deleteById(String sid);
	public List<Student> findAll();
}
