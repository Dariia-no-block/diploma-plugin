package edu.kpi.iasa.diplomaplugin.service;

import edu.kpi.iasa.diplomaplugin.dto.GroupDto;
import edu.kpi.iasa.diplomaplugin.entity.Group;
import edu.kpi.iasa.diplomaplugin.google.drive.GoogleDriveService;
import edu.kpi.iasa.diplomaplugin.google.sheets.GoogleSheetsService;
import edu.kpi.iasa.diplomaplugin.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final GroupRepository groupRepository;

	private final ModelMapper modelMapper = new ModelMapper();

	private final GoogleDriveService googleDriveService;

	private final GoogleSheetsService googleSheetsService;

	public GroupDto createGroup(GroupDto groupDto) throws Exception {
		Group group = modelMapper.map(groupDto, Group.class);

		System.out.println(group.getName());

		Optional<Group> existed = groupRepository.findByName(groupDto.getName());

		if(existed.isPresent()){
			return modelMapper.map(existed.get(), GroupDto.class);
		}

		String groupFolderID = googleDriveService.createFolderForGroup(groupDto.getName());

		String groupSpreadsheetId = googleSheetsService.createSpreadsheet(groupDto.getName());

		group.setGoogleDriveGroupFolderId(groupFolderID);
		group.setGoogleSpreadsheetId(groupSpreadsheetId);
		return modelMapper.map(groupRepository.save(group), GroupDto.class);
	}

	public void deleteAll() throws Exception {
		List<Group> groupList = groupRepository.findAll();

		for(var v : groupList){
			googleDriveService.deleteFolderWithId(v.getGoogleDriveGroupFolderId());
		}
		groupRepository.deleteAll();
	}

	public List<Group> findAllGroups(){
		return groupRepository.findAll();
	}
}
