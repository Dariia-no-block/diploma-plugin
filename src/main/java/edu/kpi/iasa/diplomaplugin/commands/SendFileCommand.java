package edu.kpi.iasa.diplomaplugin.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import edu.kpi.iasa.diplomaplugin.entity.User;
import edu.kpi.iasa.diplomaplugin.google.drive.GoogleDriveService;
import edu.kpi.iasa.diplomaplugin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class SendFileCommand implements SlashCommand {

    private final GoogleDriveService googleDriveService;
    private final UserRepository userRepository;

    @Override
    public String getName() {
        return "send";
    }

    @Override
    public Mono<Void> execute(ChatInputInteractionEvent event) {
        return event.deferReply()
                .then(Mono.fromCallable(() -> {
                    String data = event.getInteraction().getData().data().toString();
                    System.out.println(data);

                    String url = extractValue(data, "url");
                    String name = extractValue(data, "filename");
                    String fileNameFromUser = event.getOption("filename").orElseThrow().getValue().orElseThrow().asString();

                    if (url.isEmpty() || name.isEmpty()) {
                        throw new IllegalArgumentException("URL or file name not found.");
                    }

                    String[] arr = name.split("\\.");
                    String type = arr[arr.length - 1];

                    User user = userRepository.findByDiscordId(event.getInteraction().getUser().getId().asString()).orElseThrow();
                    String filename = fileNameFromUser + "." + type;

                    saveUrl(filename, url);
                    googleDriveService.uploadFileToFolderWithId(user.getGoogleDriveFolderId(), new File(filename));

                    return filename;
                }))
                .flatMap(filename -> event.createFollowup("Attachment successfully uploaded to Google Drive: " + filename))
                .then();
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