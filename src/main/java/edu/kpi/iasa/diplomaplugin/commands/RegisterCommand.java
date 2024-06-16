package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import edu.kpi.iasa.diplomaplugin.entity.Role;
import edu.kpi.iasa.diplomaplugin.entity.User;
import edu.kpi.iasa.diplomaplugin.repository.GroupRepository;
import edu.kpi.iasa.diplomaplugin.repository.UserRepository;
import edu.kpi.iasa.diplomaplugin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegisterCommand implements SlashCommand {

    private final UserRepository userRepository;

    private final UserService userService;
    private final GroupRepository groupRepository;


    @Override
    public String getName() {
        return "register";
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        String discordId = event.getInteraction().getUser().getId().asString();
        String name = event.getOption("name").orElseThrow().getValue().orElseThrow().asString();
        String lastname = event.getOption("lastname").orElseThrow().getValue().orElseThrow().asString();

        if(userService.isUserWithDiscordIdExist(discordId))
            return event.reply("You are already registered!");

        User user = new User();
        user.setDiscordId(discordId);

        user.setName(name);
        user.setLastname(lastname);
        user.setRole(Role.STUDENT);
        userRepository.save(user);

        List<Button> buttons = groupRepository.findAll().stream()
                .map(group -> Button.primary("group_" + group.getId(), group.getName()))
                .collect(Collectors.toList());

        return event.reply("Choose your group:")
                .withComponents(ActionRow.of(buttons));
    }
}