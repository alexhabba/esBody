package market.habba.controller;

import lombok.RequiredArgsConstructor;
import market.habba.entity.User;
import market.habba.security.JwtTokenProvider;
import market.habba.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


//    @PostMapping("login")
//    public ResponseEntity<UserDto> login(@RequestBody UserDto requestDto) {
//        try {
//            String email = requestDto.getEmail();
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, requestDto.getPassword()));
//            User user = userService.getUserByEmail(email);
//
//            if (user == null) {
//                throw new UsernameNotFoundException("User with email: " + email + " not found");
//            }
//
//            String token = jwtTokenProvider.createToken(email, user.getRoles());
//
//            return ResponseEntity.ok(UserDto.builder().token(token).build());
//        } catch (AuthenticationException e) {
//            throw new BadCredentialsException("Invalid username or password");
//        }
//    }
}
