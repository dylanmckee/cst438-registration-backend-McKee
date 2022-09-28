package com.cst438.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentController {
	

	
	@Autowired
	StudentRepository studentRepository;
	
	@PostMapping("/student")
	@Transactional
	public StudentDTO createNewStudent(@RequestBody StudentDTO studentDTO) { 
		Student s = new Student();
		s.setEmail(studentDTO.email);
		s.setName(studentDTO.name);
		s.setStudent_id(studentDTO.student_id);
		s.setStatusCode(0);
		
		Student savedStudent = studentRepository.save(s);
		StudentDTO result = createStudentDTO(s);
		
		return result; 
	}
	@PutMapping("/student/{id}")
	public StudentDTO updateStatus(@PathVariable("id") int sid, @RequestParam("status") int status) {
		
		Student s = studentRepository.findById(sid);
		if (s.getStatusCode() == status) {
			System.out.println("Status code of student " + s.getStudent_id() + " is already " + status);
		}
		else
			s.setStatusCode(status);
		Student savedStudent = studentRepository.save(s);
		StudentDTO result = createStudentDTO(savedStudent);
		
		return result;
	}
	
	private StudentDTO createStudentDTO(Student d) {
		
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.email = d.getEmail();
		studentDTO.name = d.getName();
		studentDTO.student_id = d.getStudent_id();
		studentDTO.statusCode = d.getStatusCode();
		studentDTO.status = d.getStatus();
		return studentDTO;
	}
	 
}

