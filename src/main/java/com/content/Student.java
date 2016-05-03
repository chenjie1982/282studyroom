package com.content;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Student")
public class Student {
	@Id
	String studentId;
	int curNum;
	int maxNum;
	String studentName;
	String password;
	List<StudentResv> reservation;
	public Student() {
		
	}
	public Student(String id, int curNum, int maxNum, String name, String password) {
		this.studentId = id;
		this.curNum = curNum;
		this.maxNum = maxNum;
		this.studentName = name;
		this.password = password;
		this.reservation = new ArrayList<StudentResv>();
	}
	public String getStudentId() {
		return studentId;
	}
	
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public int getCurNum() {
		return curNum;
	}
	public void setCurNum(int curNum) {
		this.curNum = curNum;
	}
	public int getMaxNum() {
		return maxNum;
	}
	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<StudentResv> getReservation() {
		return reservation;
	}
	public void setReservation(List<StudentResv> reservation) {
		this.reservation = reservation;
	}

	
}
