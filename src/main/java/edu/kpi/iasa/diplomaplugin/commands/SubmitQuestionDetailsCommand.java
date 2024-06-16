package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import edu.kpi.iasa.diplomaplugin.entity.test.Question;
import edu.kpi.iasa.diplomaplugin.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubmitQuestionDetailsCommand implements SlashCommand {

    private final TestService testService;

    @Override
    public String getName() {
        return "submit_question";
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        String userId = event.getInteraction().getUser().getId().asString();
        return Mono.fromCallable(() -> testService.getSelectedTestId(userId))
                .flatMap(testId -> {
                    String questionText = event.getOption("question").get().getValue().get().asString();
                    String optionsString = event.getOption("options").get().getValue().get().asString();
                    int correctOptionIndex = (int) event.getOption("correct_option").get().getValue().get().asLong();

                    List<String> options = Arrays.asList(optionsString.split(","));

                    Question question = new Question();
                    question.setTestId(testId);
                    question.setText(questionText);
                    question.setOptions(options);
                    question.setCorrectOptionIndex(correctOptionIndex);

                    return Mono.fromCallable(() -> testService.addQuestion(question))
                            .flatMap(savedQuestion -> event.reply("Question successfully added to test " + testId));
                });
    }
}