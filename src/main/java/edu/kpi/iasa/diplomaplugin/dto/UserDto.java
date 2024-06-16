package edu.kpi.iasa.diplomaplugin.dto;

import edu.kpi.iasa.diplomaplugin.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private String id;

    private String name;

    private String lastname;

    private String group;

    private String discordId;

    private Role role;

    private String googleDriveFolderId;

    private int googleSpreadsheetRowNumber;

    public UserDto(String name, String lastname, String group, String discordId, Role role, String googleDriveFolderId, int googleSpreadsheetRowNumber) {
        this.name = name;
        this.lastname = lastname;
        this.group = group;
        this.discordId = discordId;
        this.role = role;
        this.googleDriveFolderId = googleDriveFolderId;
        this.googleSpreadsheetRowNumber = googleSpreadsheetRowNumber;
    }
}