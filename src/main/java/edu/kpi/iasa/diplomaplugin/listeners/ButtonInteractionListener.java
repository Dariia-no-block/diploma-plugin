package edu.kpi.iasa.diplomaplugin.listeners;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import edu.kpi.iasa.diplomaplugin.entity.StudentTask;
import edu.kpi.iasa.diplomaplugin.entity.test.AssignedTest;
import edu.kpi.iasa.diplomaplugin.google.sheets.GoogleSheetsService;
import edu.kpi.iasa.diplomaplugin.repository.GroupRepository;
import edu.kpi.iasa.diplomaplugin.repository.StudentTaskRepository;
import edu.kpi.iasa.diplomaplugin.repository.UserRepository;
import edu.kpi.iasa.diplomaplugin.repository.test.AssignedTestRepository;
import edu.kpi.iasa.diplomaplugin.service.GroupService;
import edu.kpi.iasa.diplomaplugin.service.MenuService;
import edu.kpi.iasa.diplomaplugin.google.drive.GoogleDriveService;
import edu.kpi.iasa.diplomaplugin.service.AnswerService;
import edu.kpi.iasa.diplomaplugin.service.TestService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Component
public class ButtonInteractionListener {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final MenuService menuService;
    private final GoogleDriveService googleDriveService;

    private final GoogleSheetsService googleSheetsService;
    private final AnswerService answerService;
    private final TestService testService;
    private final AssignedTestRepository assignedTestRepository;
    private final GroupService groupService;
    private final StudentTaskRepository studentTaskRepository;


    public ButtonInteractionListener(UserRepository userRepository, GatewayDiscordClient client, GroupRepository groupRepository, MenuService menuService, GoogleDriveService googleDriveService, GoogleSheetsService googleSheetsService, AnswerService answerService, TestService testService, AssignedTestRepository assignedTestRepository, GroupService groupService, StudentTaskRepository studentTaskRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.menuService = menuService;
        this.googleDriveService = googleDriveService;
		this.googleSheetsService = googleSheetsService;
		this.answerService = answerService;
		this.testService = testService;
		this.assignedTestRepository = assignedTestRepository;
		this.groupService = groupService;
		this.studentTaskRepository = studentTaskRepository;
		client.on(ButtonInteractionEvent.class, this::handleButtonInteraction).subscribe();
    }

    public Mono<Void> handleSelectGroupButton(ButtonInteractionEvent event) {
        String discordId = event.getInteraction().getUser().getId().asString();
        String groupId = event.getCustomId().split("_")[1];

        return Mono.justOrEmpty(userRepository.findByDiscordId(discordId))
                .flatMap(user -> {
                    user.setGroup(groupId);
                    return Mono.fromCallable(() -> userRepository.save(user))
                            .flatMap(savedUser -> Mono.justOrEmpty(groupRepository.findById(groupId))
                                    .flatMap(group -> {
										try {
											String id = googleDriveService.crateFolderInFolderWithId(user.getName() + "_" + user.getLastname(),
                                                    group.getGoogleDriveGroupFolderId());
                                            googleSheetsService.addRow(savedUser.getName() + " " + savedUser.getLastname(), group.getGoogleSpreadsheetId());
                                            savedUser.setGoogleDriveFolderId(id);
                                            savedUser.setGoogleSpreadsheetRowNumber(googleSheetsService.getRowCount(group.getGoogleSpreadsheetId()));
                                            userRepository.save(savedUser);
										} catch (GeneralSecurityException e) {
											throw new RuntimeException(e);
										} catch (IOException e) {
											throw new RuntimeException(e);
										}
										return event.reply()
                                                        .withContent("Student: \"" + savedUser.getName() + " " + savedUser.getLastname() +
                                                                "\" successfully registered in the system.\nGroup: " + group.getName() +
                                                                ".\nUnique student ID: " + savedUser.getId())
                                                        .withComponents(menuService.mainMenuButtons());
                                            }
                                    )
                            );
                })
                .switchIfEmpty(event.reply("User not found!").then());
    }

    public Mono<Void> handleButtonInteraction(ButtonInteractionEvent event) {
        String customId = event.getCustomId();
        System.out.println(customId);

        if (customId.startsWith("my_")) {
            return menuService.handleMainMenuInteraction(event);
        } else if (customId.startsWith("select_test_")) {
            String testId = customId.split("_")[2];
            return handleTestSelection(event, testId);
        }
        else if (customId.startsWith("start_select_test_")) {
            String testId = customId.split("_")[3];
            return handleStartTestSelection(event, testId);
        }
        else if (customId.startsWith("select_group_")) {
            String groupId = customId.split("_")[2];
            return handleGroupSelection(event, groupId);
        }
        else if(customId.startsWith("group")) {
            return handleSelectGroupButton(event);
        } else if(customId.startsWith("admin")){
            return handleAddTaskToGroupButtonInteraction(event);
        } else if(customId.startsWith("answer_")) {
            return handleAnswerButton(event);
        } else if(customId.startsWith("take_")){
            return handleStartTest(event);
        } else if (customId.startsWith("view=")) {
            String filename = customId.split("=")[1];
            return handleViewTask(event, filename);
        } else if(customId.startsWith("submit_")){
            return handleSubmitTask(event);
        } else return event.reply("Idk");
    }

    public Mono<Void> handleViewTask(ButtonInteractionEvent event, String fileId) {
        String fileUrl = "https://drive.google.com/file/d/" + fileId + "/view?usp=drive_link";
        return event.reply()
                .withEphemeral(true)
                .withContent("You can view the task [here](" + fileUrl + ").");
    }

    public Mono<Void> handleSubmitTask(ButtonInteractionEvent event) {
        return event.reply()
                .withEphemeral(true)
                .withContent("Please use the /send_file command to submit your task.");
    }

    public Mono<Void> handleAddTaskToGroupButtonInteraction(ButtonInteractionEvent event) {
        String[] customIdParts = event.getCustomId().split("_");
        String groupId = customIdParts[1];
        String fileName = customIdParts[2];

        System.out.println(groupId);
        System.out.println(fileName);
        return Mono.justOrEmpty(groupRepository.findById(groupId))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(group -> {
                    File file = new File("temp/" + fileName);
                    String googleDriveId = googleDriveService.uploadFileToFolderWithId(group.getGoogleDriveGroupFolderId(), file);
                    StudentTask studentTask = new StudentTask();
                    studentTask.setName(fileName);
                    studentTask.setGoogleDrivePath(googleDriveId);
                    studentTaskRepository.save(studentTask);
										return event.reply()
                            .withContent("The task has been successfully added to the group: " + group.getName() + ".");
                })
                .switchIfEmpty(event.reply("Group not found!").then());
    }

    private Mono<Void> handleAnswerButton(ButtonInteractionEvent event) {
        String customId = event.getCustomId();
        String[] parts = customId.split("_");
        String testId = parts[1];
        int questionIndex = Integer.parseInt(parts[2]);
        int selectedOptionIndex = Integer.parseInt(parts[3]);

        String userId = event.getInteraction().getUser().getId().asString();
        answerService.saveUserAnswer(userId, testId, questionIndex, selectedOptionIndex);

        return menuService.sendQuestion(event, testId, questionIndex + 1);
    }

    private Mono<Void> handleStartTest(ButtonInteractionEvent event) {
        String discordId = event.getInteraction().getUser().getId().asString();
        return Mono.justOrEmpty(userRepository.findByDiscordId(discordId))
                .flatMap(user -> {
                    String groupId = user.getGroup();
                    List<AssignedTest> assignedTests = assignedTestRepository.findByGroupId(groupId);
                    AssignedTest activeTest = assignedTests.stream()
                            .filter(test -> test.getStartTime() <= System.currentTimeMillis() && test.getEndTime() >= System.currentTimeMillis())
                            .findFirst()
                            .orElse(null);

                    if (activeTest != null && activeTest.getStudentsPassedTestList().stream().noneMatch(s -> s.equals(user.getId()))) {
                        return menuService.sendQuestion(event, activeTest.getTestId(), 0);
                    } else {
                        return event.reply("There are no active tests.");
                    }
                });
    }

    private Mono<Void> handleTestSelection(ButtonInteractionEvent event, String testId) {
        return Mono.fromCallable(() -> {
            testService.setSelectedTestId(event.getInteraction().getUser().getId().asString(), testId);
            return null;
        }).then(event.reply("You have selected test with ID: " + testId + ". Now, please enter the question details.")
                .withEphemeral(true));
    }

    private Mono<Void> handleGroupSelection(ButtonInteractionEvent event, String groupId) {
        return Mono.fromCallable(() -> {
            testService.setSelectedGroupId(event.getInteraction().getUser().getId().asString(), groupId);
            return null;
        }).then(event.reply("You have selected group with ID: " + groupId + ". Now, please enter the duration in minutes using the /set_duration command.")
                .withEphemeral(true));
    }

    private Mono<Void> handleStartTestSelection(ButtonInteractionEvent event, String testId) {
        return Mono.fromCallable(() -> {
            testService.setSelectedTestId(event.getInteraction().getUser().getId().asString(), testId);
            return groupService.findAllGroups();
        }).flatMap(groups -> menuService.showGroupSelectionMenu(event, groups));
    }
}