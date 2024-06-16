package edu.kpi.iasa.diplomaplugin.dto;

import lombok.Data;

@Data
public class GroupDto {
	private String id;

	private String name;

	private String googleDriveGroupFolderId;

	private String googleSpreadsheetId;
}
