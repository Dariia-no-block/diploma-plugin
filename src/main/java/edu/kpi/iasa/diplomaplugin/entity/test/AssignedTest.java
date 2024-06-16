package edu.kpi.iasa.diplomaplugin.entity.test;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "assigned_tests")
@Data
public class AssignedTest {
    @Id
    private String id;
    private String testId;
    private String groupId;
    private List<String> studentsPassedTestList = new ArrayList<>();
    private long startTime;
    private long endTime;
}