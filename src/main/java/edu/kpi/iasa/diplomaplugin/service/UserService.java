package edu.kpi.iasa.diplomaplugin.service;

import edu.kpi.iasa.diplomaplugin.dto.UserDto;
import edu.kpi.iasa.diplomaplugin.entity.Role;
import edu.kpi.iasa.diplomaplugin.entity.User;
import edu.kpi.iasa.diplomaplugin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    public UserDto registerNewUser(UserDto userDto){
        return userRepository.findByDiscordId(userDto.getDiscordId())
                .map(existingUser -> modelMapper.map(existingUser, UserDto.class))
                .orElseGet(() -> {
                    User newUser = userRepository.save(modelMapper.map(userDto, User.class));
                    return modelMapper.map(newUser, UserDto.class);
                });
    }
    public List<UserDto> getAllUsers(){
        return userRepository
                .findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    public UserDto findByDiscordId(String discordId) throws IllegalArgumentException{
        User user = userRepository.findByDiscordId(discordId).orElseThrow(() -> new IllegalArgumentException(String.format("User with id %s not found", discordId)));
        return modelMapper.map(user, UserDto.class);
    }

    public void deleteAllUsers(){
        userRepository.deleteAll();
        List<User> users = userRepository.findAll();

        for(var u : users){
            if(!u.getRole().equals(Role.TEACHER)){
                userRepository.delete(u);
            }
        }
    }

    public void deleteUserById(String id){
        userRepository.deleteById(id);
    }

    public boolean isUserWithDiscordIdExist(String discordId){
        return userRepository.findByDiscordId(discordId).isPresent();
    }
}
