package edu.kpi.iasa.diplomaplugin.entity.test;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_answers")
@Data
public class UserAnswer {
    @Id
    private String id;
    private String userId;
    private String testId;
    private int questionIndex;
    private int selectedOptionIndex;
    private boolean isCorrect;
}
