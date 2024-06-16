package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import edu.kpi.iasa.diplomaplugin.service.MenuService;
import edu.kpi.iasa.diplomaplugin.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StartTestCommand implements SlashCommand {

    private final TestService testService;
    private final MenuService menuService;

    @Override
    public String getName() {
        return "start_test";
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return Mono.fromCallable(testService::getAllTests)
                .flatMap(tests -> menuService.showStartTestSelectionMenu(event, tests))
                .then();
    }
}