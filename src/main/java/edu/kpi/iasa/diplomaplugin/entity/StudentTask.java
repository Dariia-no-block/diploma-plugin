package edu.kpi.iasa.diplomaplugin.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "tasks")
public class StudentTask {

	@Id
	private String id;

	private String name;

	private String googleDrivePath;

}