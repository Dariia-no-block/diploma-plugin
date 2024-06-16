package edu.kpi.iasa.diplomaplugin.service;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import edu.kpi.iasa.diplomaplugin.entity.Group;
import edu.kpi.iasa.diplomaplugin.entity.Role;
import edu.kpi.iasa.diplomaplugin.entity.User;
import edu.kpi.iasa.diplomaplugin.entity.test.AssignedTest;
import edu.kpi.iasa.diplomaplugin.entity.test.Question;
import edu.kpi.iasa.diplomaplugin.entity.test.Test;
import edu.kpi.iasa.diplomaplugin.google.drive.GoogleDriveService;
import edu.kpi.iasa.diplomaplugin.google.sheets.GoogleSheetsService;
import edu.kpi.iasa.diplomaplugin.repository.GroupRepository;
import edu.kpi.iasa.diplomaplugin.repository.UserRepository;
import edu.kpi.iasa.diplomaplugin.repository.test.AssignedTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final AssignedTestRepository assignedTestRepository;
    private final TestService testService;
    private final UserRepository userRepository;
    private final AnswerService answerService;
    private final GoogleDriveService googleDriveService;
    private final GoogleSheetsService googleSheetsService;
    private final GroupRepository groupRepository;
    private final GatewayDiscordClient client;


    public ActionRow mainMenuButtons() {
        return ActionRow.of(
                Button.primary("my_tasks", "My tasks"),
                Button.primary("my_performance", "My success rate"),
                Button.primary("take_test", "Pass a test")
        );
    }


    public Mono<Void> handleMainMenuInteraction(ButtonInteractionEvent event) {
        String customId = event.getCustomId();
        switch (customId) {
            case "my_tasks":
                return handleMyTasksButton(event, event.getInteraction().getUser().getId().asString());
            case "my_performance":
                return handleMyPerformanceButton(event, event.getInteraction().getUser().getId().asString());
            case "take_test":
                return startTest(event);
            default:
                return event.reply("Unknown option!");
        }
    }

    public Mono<Void> sendQuestion(ButtonInteractionEvent event, String testId, int questionIndex) {
        List<Question> questions = testService.getQuestionsByTestId(testId);
        if (questionIndex >= questions.size()) {
            User user = userRepository.findByDiscordId(event.getInteraction().getUser().getId().asString()).get();
            List<AssignedTest> assignedTests = assignedTestRepository.findByGroupId(user.getGroup());
            AssignedTest activeTest = assignedTests.stream()
                    .filter(test -> test.getStartTime() <= System.currentTimeMillis() && test.getEndTime() >= System.currentTimeMillis())
                    .findFirst()
                    .orElse(null);
            activeTest.getStudentsPassedTestList().add(user.getId());
            assignedTestRepository.save(activeTest);
            int result = answerService.calculateResult(event.getInteraction().getUser().getId().asString(), testId);
            sendResult(user.getName() + " " + user.getLastname() + ", (Discord id: " + user.getDiscordId() + "). \nResult: " + result);
            return event.reply("The test is complete! Your result:" + result);
        }
        Question question = questions.get(questionIndex);
        List<Button> answerButtons = question.getOptions().stream()
                .map(option -> Button.primary("answer_" + testId + "_" + questionIndex + "_" + question.getOptions().indexOf(option), option))
                .collect(Collectors.toList());

        return event.reply(question.getText())
                .withComponents(ActionRow.of(answerButtons));
    }

    public void sendResult(String message) {
        List<User> users = userRepository.findAll();
        String teacherId = "";
        for(var v : users){
            if(v.getRole().equals(Role.TEACHER))
                teacherId = v.getDiscordId();
        }

        client.getUserById(Snowflake.of(teacherId))
                .flatMap(discord4j.core.object.entity.User::getPrivateChannel)
                .flatMap(channel -> channel.createMessage(message))
                .subscribe();
    }
    private Mono<Void> startTest(ButtonInteractionEvent event) {
        String discordId = event.getInteraction().getUser().getId().asString();
        System.out.println("MAMUMAV");
        return Mono.justOrEmpty(userRepository.findByDiscordId(discordId))
                .flatMap(user -> {
                    String groupId = user.getGroup();
                    List<AssignedTest> assignedTests = assignedTestRepository.findByGroupId(groupId);
                    AssignedTest activeTest = assignedTests.stream()
                            .filter(test -> test.getStartTime() <= System.currentTimeMillis() && test.getEndTime() >= System.currentTimeMillis())
                            .findFirst()
                            .orElse(null);

                    if (activeTest != null && activeTest.getStudentsPassedTestList().stream().noneMatch(s -> s.equals(user.getId()))) {
                        return sendQuestion(event, activeTest.getTestId(), 0);
                    } else {
                        return event.reply("There are no active tests.");
                    }
                });
    }

    public Mono<Void> showTestSelectionMenu(ChatInputInteractionEvent event, List<Test> tests) {
        InteractionApplicationCommandCallbackReplyMono message = event.reply().withContent("Please select a test:");
        ActionRow actionRow = ActionRow.of(
                tests.stream()
                        .map(this::createButton)
                        .toArray(Button[]::new)
        );
        return message.withComponents(actionRow).then();
    }

    private Button createButton(Test test) {
        return Button.primary("select_test_" + test.getId(), test.getName());
    }

    public Mono<Void> showGroupSelectionMenu(ButtonInteractionEvent event, List<Group> groups) {
        InteractionApplicationCommandCallbackReplyMono message = event.reply().withContent("Please select a group:");
        ActionRow actionRow = ActionRow.of(
                groups.stream()
                        .map(this::createGroupButton)
                        .toArray(Button[]::new)
        );
        return message.withComponents(actionRow).then();
    }

    public Mono<Void> showStartTestSelectionMenu(ChatInputInteractionEvent event, List<Test> tests) {
        InteractionApplicationCommandCallbackReplyMono message = event.reply().withContent("Please select a test:");
        ActionRow actionRow = ActionRow.of(
                tests.stream()
                        .map(this::createTestButton)
                        .toArray(Button[]::new)
        );
        return message.withComponents(actionRow).then();
    }

    private Button createTestButton(Test test) {
        return Button.primary("start_select_test_" + test.getId(), test.getName());
    }

    private Button createGroupButton(Group group) {
        return Button.primary("select_group_" + group.getId(), group.getName());
    }

    public Mono<Void> handleMyPerformanceButton(ButtonInteractionEvent event, String userId){
        Optional<User> userOptional = userRepository.findByDiscordId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Group> groupOptional = groupRepository.findById(user.getGroup());
            if (groupOptional.isPresent()) {
                Group group = groupOptional.get();
                int sum;
				try {
					sum = googleSheetsService.calculateSumOfGrades(group.getGoogleSpreadsheetId(), user.getGoogleSpreadsheetRowNumber());
                    return event.reply()
                            .withEphemeral(true)
                            .withContent("You current rate: " + sum);
				} catch (GeneralSecurityException | IOException e) {
					throw new RuntimeException(e);
				}
			}
        }
        return event.reply()
                .withEphemeral(true)
                .withContent("ERROR.");
    }

    public Mono<Void> handleMyTasksButton(ButtonInteractionEvent event, String userId) {
        Optional<User> userOptional = userRepository.findByDiscordId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Group> groupOptional = groupRepository.findById(user.getGroup());
            if (groupOptional.isPresent()) {
                Group group = groupOptional.get();
                try {
                    List<com.google.api.services.drive.model.File> files = googleDriveService.getFilesInFolder(group.getGoogleDriveGroupFolderId());
                    List<Button> buttons = new ArrayList<>();
                    for (var file : files) {
                        buttons.add(Button.primary("view=" + file.getId(), file.getName()));
                        buttons.add(Button.danger("submit_" + file.getName(), "Submit " + file.getName()));
                    }
                    return event.reply()
                            .withEphemeral(true)
                            .withComponents(ActionRow.of(buttons));
                } catch (IOException | GeneralSecurityException e) {
                    return event.reply()
                            .withEphemeral(true)
                            .withContent("Failed to fetch tasks from Google Drive: " + e.getMessage());
                }
            }
        }
        return event.reply()
                .withEphemeral(true)
                .withContent("Group or user not found.");
    }
}