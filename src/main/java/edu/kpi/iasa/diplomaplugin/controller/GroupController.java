package edu.kpi.iasa.diplomaplugin.controller;

import edu.kpi.iasa.diplomaplugin.dto.GroupDto;
import edu.kpi.iasa.diplomaplugin.entity.Group;
import edu.kpi.iasa.diplomaplugin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
@RequiredArgsConstructor
public class GroupController {

	private final GroupService groupService;

	@PostMapping("/create")
	public ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto groupDto) throws Exception {
		return new ResponseEntity<>(groupService.createGroup(groupDto), HttpStatus.CREATED);
	}

	@DeleteMapping("/delete-all")
	public ResponseEntity<Void> deleteAll() throws Exception {
		groupService.deleteAll();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping
	public ResponseEntity<List<Group>> findAllGroups(){
		return new ResponseEntity<>(groupService.findAllGroups(), HttpStatus.OK);
	}
}