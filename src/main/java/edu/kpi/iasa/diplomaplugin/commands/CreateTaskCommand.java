package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import edu.kpi.iasa.diplomaplugin.dto.UserDto;
import edu.kpi.iasa.diplomaplugin.entity.Role;
import edu.kpi.iasa.diplomaplugin.google.drive.GoogleDriveService;
import edu.kpi.iasa.diplomaplugin.repository.GroupRepository;
import edu.kpi.iasa.diplomaplugin.repository.UserRepository;
import edu.kpi.iasa.diplomaplugin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CreateTaskCommand implements SlashCommand {

	private final UserService userService;
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;

	@Override
	public String getName() {
		return "create_task";
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

		return event.deferReply().then(
				Mono.fromCallable(() -> {

					String nameFromOptions = event.getOption("filename")
							.flatMap(option -> option.getValue().map(ApplicationCommandInteractionOptionValue::asString))
							.orElseThrow(() -> new IllegalArgumentException("File name not found."));

					String data = event.getInteraction().getData().toString();
					System.out.println(data);

					String url = extractValue(data, "url");
					String nameForExtractType = extractValue(data, "filename");

					if (url.isEmpty() || nameForExtractType.isEmpty()) {
						throw new IllegalArgumentException("URL or file name not found.");
					}

					String[] arr = nameForExtractType.split("\\.");
					String type = arr[arr.length - 1];

					String filename = "temp/" + userRepository.findByDiscordId(event.getInteraction().getUser().getId().asString())
							.map(user -> nameFromOptions + "." + type)
							.orElseThrow();

					saveUrl(filename, url);

					return filename;
				}).flatMap(filename -> {
					File file = new File(filename);
					List<Button> buttons = groupRepository.findAll().stream()
							.map(group -> Button.primary("admin_" + group.getId() + "_" + file.getName(), group.getName()))
							.collect(Collectors.toList());

					return event.createFollowup("Select the group to which you want to assign the task:")
							.withComponents(ActionRow.of(buttons));
				}).then()
		);
	}

	private String extractValue(String data, String key) {
		Pattern pattern = Pattern.compile("(?<=" + key + "=)([^,\\s]+)");
		Matcher matcher = pattern.matcher(data);
		return matcher.find() ? matcher.group(1) : "";
	}

	private void saveUrl(String filename, String urlString) throws IOException {
		try (BufferedInputStream in = new BufferedInputStream(new URL(urlString).openStream());
			 FileOutputStream fout = new FileOutputStream(filename)) {

			byte[] data = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				fout.write(data, 0, count);
			}
		}
	}
}
