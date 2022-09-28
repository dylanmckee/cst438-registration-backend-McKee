package com.cst438;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controller.ScheduleController;
import com.cst438.controller.StudentController;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

/* 
 * Example of using Junit with Mockito for mock objects
 *  the database repositories are mocked with test data.
 *  
 * Mockmvc is used to test a simulated REST call to the RestController
 * 
 * the http response and repository is verified.
 * 
 *   Note: This tests uses Junit 5.
 *  ContextConfiguration identifies the controller class to be tested
 *  addFilters=false turns off security.  (I could not get security to work in test environment.)
 *  WebMvcTest is needed for test environment to create Repository classes.
 */
@ContextConfiguration(classes = {  StudentController.class })

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {

	static final String URL = "http://localhost:8080";
	public static final int TEST_COURSE_ID = 40442;
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "test";
	public static final int TEST_YEAR = 2021;
	public static final String TEST_SEMESTER = "Fall";


	@MockBean
	StudentRepository studentRepository;


	@Autowired
	private MockMvc mvc;

	
	@Test
	public void addStudent() throws Exception {
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(1);
//		
		// stubs for database repositories that return test data
	    given(studentRepository.save(any(Student.class))).willReturn(student);

		// create DTO for student to add;
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.email = TEST_STUDENT_EMAIL;
		studentDTO.name = TEST_STUDENT_NAME;
		studentDTO.student_id = 1;
		
		System.out.println(studentDTO.email + " " + studentDTO.name);
		System.out.println("DTO: " + asJsonString(studentDTO));
		
		// http post request
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student")
				.content(asJsonString(studentDTO))
		        .contentType(MediaType.APPLICATION_JSON)
		        .accept(MediaType.APPLICATION_JSON))
			   .andReturn().getResponse();
		
		// verify that return status = OK (value 200)
		assertEquals(200, response.getStatus());

		StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
		// verify that returned data has non zero primary key
		assertNotEquals( 0  , result.student_id);	
		
		// verify that repository save method was called.
		verify(studentRepository).save(any(Student.class));
	} 
		
	@Test
	public void updateStatus() throws Exception{
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		student.setStudent_id(1);
		
		// given  -- stubs for database repositories that return test data
	    given(studentRepository.save(any(Student.class))).willReturn(student);
		given(studentRepository.findById(1)).willReturn(student);
		
		// ----------------add hold test-----------------//

		// http post get request
		response = mvc.perform(
				MockMvcRequestBuilders
				.put("/student/1?status=1"))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());

		StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
		// verify that returned data has non zero primary key
		assertNotEquals(0  , result.student_id);	
		// verify that the status code was changed from 0 to 1
		assertEquals(1, result.statusCode);
		// verify that repository save method was called.
		verify(studentRepository).save(any(Student.class));
		
		// ---------------remove hold test----------------//
		
		// http post get request
		response = mvc.perform(
				MockMvcRequestBuilders
				.put("/student/1?status=0"))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());

		result = fromJsonString(response.getContentAsString(), StudentDTO.class);
		// verify that returned data has non zero primary key
		assertNotEquals(0  , result.student_id);	
		// verify that the status code was changed from 1 to 0
		assertEquals(0, result.statusCode);
		// verify that repository save method was called.
		verify(studentRepository, times(2)).save(any(Student.class));
		
		
	}
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
