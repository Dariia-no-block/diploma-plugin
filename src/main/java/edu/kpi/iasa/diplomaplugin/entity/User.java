package edu.kpi.iasa.diplomaplugin.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "users")
public class User {

    @Id
    private String id;

    private String name;

    private String lastname;

    private String group;

    private String discordId;

    private Role role;

    private String googleDriveFolderId;

    private int googleSpreadsheetRowNumber;
}