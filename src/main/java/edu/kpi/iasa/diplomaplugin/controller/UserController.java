package edu.kpi.iasa.diplomaplugin.controller;

import edu.kpi.iasa.diplomaplugin.dto.UserDto;
import edu.kpi.iasa.diplomaplugin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/get-all")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/get-by-discord-id/{discordId}")
    public ResponseEntity<UserDto> findUserByDiscordId(@PathVariable String discordId){
        return new ResponseEntity<>(userService.findByDiscordId(discordId), HttpStatus.OK);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAllUsers(){
        userService.deleteAllUsers();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}