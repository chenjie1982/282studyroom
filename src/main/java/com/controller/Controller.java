package com.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.content.ResvInfo;
import com.content.Student;
import com.content.StudentResv;
import com.content.TimeSlot;
import com.content.TimeTable;
import com.model.StudentRepository;
import com.model.TimeTableRepository;

@RestController
public class Controller {
	
	@Autowired
	private TimeTableRepository repository;
	@Autowired
	private StudentRepository studentrepository;
	
	@RequestMapping(value="/studentall", method=RequestMethod.GET)
	public ResponseEntity<List<Student>> getAllStudent() {
		List<Student> listStudent = studentrepository.findAll();
    	if (listStudent == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(listStudent, HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/student/{id}", method=RequestMethod.GET)
	public ResponseEntity<Student> getStudentById(@PathVariable("id") String id ) {
		Student s = studentrepository.findByStudentId(id);
    	if (s == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Set-Cookie","sid="+id+"; path=/");
        return new ResponseEntity<>(s,headers, HttpStatus.OK);
	}
	
	@RequestMapping(value="/student/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Long> delStudentById(@PathVariable("id") String id ) {
		Long l = studentrepository.deleteByStudentId(id);
    	if (l == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(l, HttpStatus.OK);
	}	
	
	@RequestMapping(value="/student", method=RequestMethod.POST)
	public ResponseEntity<Student> createStudentInfo(@RequestBody Student input) {
		Student s = studentrepository.findByStudentId(input.getStudentId());
		if(s != null) {
			s.setPassword(input.getPassword());
			s.setStudentName(input.getStudentName());
			s.setMaxNum(input.getMaxNum());
			studentrepository.save(s);
		} else {
			studentrepository.save(new Student(input.getStudentId(), 0, input.getMaxNum(), input.getStudentName(), input.getPassword()));
		}
    	
        return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/timetable/{date}", method=RequestMethod.GET)
	public ResponseEntity<TimeTable> getResvInfoByDate(@PathVariable("date") String date) {
		TimeTable tt = repository.findByDate(date);
    	if (tt == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tt, HttpStatus.OK);
	}

	@RequestMapping(value="/timetable", method=RequestMethod.GET)
	public ResponseEntity<List<TimeTable>> getAllResvInfo() {
		List<TimeTable> tt = repository.findAll();
    	if (tt == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tt, HttpStatus.OK);
	}
//	@RequestMapping(value="/book?date={date}&sid={sid}&rid={rid}&slot={slot}")
//	public ResponseEntity<TimeTable> deleteRoom(@) {
//		
//	}
	@RequestMapping(value="/book", method=RequestMethod.POST)
	public ResponseEntity<TimeTable> resvRoom(@CookieValue("sid") String sid, @RequestBody StudentResv input) {
		//check student status
		Student s = studentrepository.findByStudentId(sid);
		if(s == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();        
		String curDate = df.format(today);
		Calendar cal = Calendar.getInstance();
		int curSlot = cal.get(Calendar.HOUR_OF_DAY)-8;
		 
		if(input.getDate().compareTo(curDate) < 0 
		|| (input.getDate().compareTo(curDate) == 0 && input.getSlot() <= curSlot)) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}

		System.out.println("Report Date: " + curDate + "hour:" + curSlot);
		for(int i = 0; i < s.getReservation().size(); i++) {
			StudentResv studentresv = s.getReservation().get(i);
			if(studentresv.getDate().compareTo(curDate) < 0) {
				s.getReservation().remove(i);
				continue;
			}
			if(studentresv.getSlot() < curSlot) {
				s.getReservation().remove(i);
				continue;
			}
		}
		s.setCurNum(s.getReservation().size());
		if(s.getCurNum() >= s.getMaxNum()) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
		s.getReservation().add(input);
		s.setCurNum(s.getCurNum()+1);
		
		//check room status
		TimeTable tt = repository.findByDate(input.getDate());
		if (tt == null) {
			tt = new TimeTable(input.getDate(), sid, input.getRoomid(), input.getSlot());
			repository.save(tt);
			studentrepository.save(s);
			return new ResponseEntity<>(HttpStatus.CREATED);
        }
		for(ResvInfo r : tt.getTimesheet()) {
			if(r.getRoomNumber().equals(input.getRoomid())) {
				for(TimeSlot ts : r.getTimeSlots()) {
					if(ts.getTimeSlot() == input.getSlot()) {
						return new ResponseEntity<>(HttpStatus.CONFLICT);
					}
				}
				r.getTimeSlots().add(new TimeSlot(sid,input.getSlot()));
				repository.save(tt);
				studentrepository.save(s);
				return new ResponseEntity<>(HttpStatus.CREATED);
			}
		}
		tt.getTimesheet().add(new ResvInfo(sid, input.getRoomid(), input.getSlot()));
		repository.save(tt);
		studentrepository.save(s);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
}
