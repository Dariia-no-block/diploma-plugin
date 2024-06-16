package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import edu.kpi.iasa.diplomaplugin.entity.test.Test;
import edu.kpi.iasa.diplomaplugin.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class CreateTestCommand implements SlashCommand {

    private final TestService testService;

    @Override
    public String getName() {
        return "create_test";
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        String testName = event.getOption("name").get().getValue().get().asString();
        String createdBy = event.getInteraction().getUser().getId().asString();
        Test test = new Test();
        test.setName(testName);
        test.setCreatedBy(createdBy);
        test.setQuestionIds(new ArrayList<>());
        return Mono.fromCallable(() -> testService.createTest(test))
                .flatMap(savedTest -> event.reply("Test \"" + savedTest.getName() + "\" successfully created."))
                .then();
    }
}