package edu.kpi.iasa.diplomaplugin.entity.test;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tests")
@Data
public class Test {
    @Id
    private String id;
    private String name;
    private String createdBy;
    private List<String> questionIds;
}
