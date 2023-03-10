package market.habba.service;

import lombok.RequiredArgsConstructor;
import market.habba.entity.Role;
import market.habba.entity.User;
import market.habba.model.UserRequestDto;
import market.habba.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static market.habba.entity.enums.RoleEnum.USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User with email: " + email + " not found"));
    }

    @Override
    public void addNewUser(UserRequestDto userRequestDto) {
        boolean empty = StringUtils.isEmpty("");
        Role role = Role.builder().name(USER).build();
//        User user = User.builder()
//                .password(userRequestDto.getPassword())
//                .email(userRequestDto.getEmail())
//                .roles(List.of(role))
//                .build();
//        role.setUser(user);
//        userRepository.save(user);
    }
}
