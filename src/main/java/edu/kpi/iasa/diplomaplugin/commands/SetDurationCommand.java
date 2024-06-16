package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import edu.kpi.iasa.diplomaplugin.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SetDurationCommand implements SlashCommand {

    private final TestService testService;

    @Override
    public String getName() {
        return "set_duration";
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        String userId = event.getInteraction().getUser().getId().asString();
        return Mono.fromCallable(() -> {
            String testId = testService.getSelectedTestId(userId);
            String groupId = testService.getSelectedGroupId(userId);
            long startTime = System.currentTimeMillis();
            long endTime = startTime + event.getOption("duration").get().getValue().get().asLong() * 60 * 1000;

            return testService.assignTest(testId, groupId, startTime, endTime);
        }).flatMap(assignedTest -> event.reply("The test was successfully assigned to the group: " + assignedTest.getGroupId()))
                .then();
    }
}
