package edu.kpi.iasa.diplomaplugin.controller;

import edu.kpi.iasa.diplomaplugin.entity.test.AssignedTest;
import edu.kpi.iasa.diplomaplugin.entity.test.Test;
import edu.kpi.iasa.diplomaplugin.entity.test.UserAnswer;
import edu.kpi.iasa.diplomaplugin.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

	private final TestService testService;

	@GetMapping()
	public ResponseEntity<List<Test>> getAll(){
		return new ResponseEntity<>(testService.getAllTests(), HttpStatus.OK);
	}

	@DeleteMapping()
	public ResponseEntity<Void> removeAllTests(){
		testService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/assigned")
	public ResponseEntity<List<AssignedTest>> getAllAssignTest(){
		return new ResponseEntity<>(testService.getAllAssignTest(), HttpStatus.OK);
	}

	@GetMapping("/results")
	public ResponseEntity<List<UserAnswer>> getAllResults(){
		return new ResponseEntity<>(testService.getResults(), HttpStatus.OK);
	}

}