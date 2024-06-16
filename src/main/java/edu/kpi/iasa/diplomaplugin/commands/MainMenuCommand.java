package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import edu.kpi.iasa.diplomaplugin.service.MenuService;
import edu.kpi.iasa.diplomaplugin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MainMenuCommand implements SlashCommand{

	private final UserService userService;

	private final MenuService menuService;

	@Override
	public String getName() {
		return "menu";
	}

	@Override
	public Mono<Void> execute(ChatInputInteractionEvent event) {

		String discordId = event.getInteraction().getUser().getId().asString();

		if(!userService.isUserWithDiscordIdExist(discordId))
			return event.reply("To get started, you need to register. Write the /register command.");


		return event.reply("Welcome to the bot menu. Select the desired function.").withComponents(menuService.mainMenuButtons());
	}
}
