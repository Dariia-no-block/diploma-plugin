package edu.kpi.iasa.diplomaplugin.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "groups")
public class Group {

	@Id
	private String id;

	private String name;

	private String googleDriveGroupFolderId;

	private String googleSpreadsheetId;
}
