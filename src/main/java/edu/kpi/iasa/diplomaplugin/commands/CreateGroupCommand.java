package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import edu.kpi.iasa.diplomaplugin.dto.GroupDto;
import edu.kpi.iasa.diplomaplugin.dto.UserDto;
import edu.kpi.iasa.diplomaplugin.entity.Group;
import edu.kpi.iasa.diplomaplugin.entity.Role;
import edu.kpi.iasa.diplomaplugin.entity.User;
import edu.kpi.iasa.diplomaplugin.service.GroupService;
import edu.kpi.iasa.diplomaplugin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CreateGroupCommand implements SlashCommand {

    private final UserService userService;

    private final GroupService groupService;

    @Override
    public String getName() {
        return "create_group";
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        String discordId = event.getInteraction().getUser().getId().asString();
        UserDto userDto = userService.findByDiscordId(discordId);

        if (userDto == null || userDto.getRole() != Role.TEACHER) {
            return event.reply()
                    .withEphemeral(true)
                    .withContent("You do not have permission to execute this command.");
        }

        GroupDto groupDto = new GroupDto();
        groupDto.setName(event.getOption("group_name")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get());
		try {
			groupService.createGroup(groupDto);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return event.reply()
                .withEphemeral(true)
                .withContent("The group has been successfully created.");
    }
}