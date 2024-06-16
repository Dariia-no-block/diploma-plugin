package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import edu.kpi.iasa.diplomaplugin.service.MenuService;
import edu.kpi.iasa.diplomaplugin.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import edu.kpi.iasa.diplomaplugin.entity.test.Question;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AddQuestionCommand implements SlashCommand {

    private final TestService testService;
    private final MenuService menuService;

    @Override
    public String getName() {
        return "add_question";
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return Mono.fromCallable(testService::getAllTests)
                .flatMap(tests -> menuService.showTestSelectionMenu(event, tests))
                .then();
    }
}