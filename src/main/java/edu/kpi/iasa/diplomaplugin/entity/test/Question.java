package edu.kpi.iasa.diplomaplugin.entity.test;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "questions")
@Data
public class Question {
    @Id
    private String id;
    private String testId;
    private String text;
    private List<String> options;
    private int correctOptionIndex;
}