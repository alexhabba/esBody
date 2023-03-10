package market.habba.controller;

import lombok.RequiredArgsConstructor;
import market.habba.api.UserAuthApi;
import market.habba.entity.User;
import market.habba.model.UserRequestDto;
import market.habba.model.UserTokenDto;
import market.habba.security.JwtTokenProvider;
import market.habba.service.MailSenderServiceImpl;
import market.habba.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class UserAuthApiController implements UserAuthApi {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    private final MailSenderServiceImpl mailSenderService;

    @Override
    public ResponseEntity<UserTokenDto> authorizationUser(UserRequestDto userRequestDto) {
        mailSenderService.sendEmail("jovew95155@jobsfeel.com", "Вам необходимо зарегаться", "this is body");

        System.out.println("send email");
        System.out.println("*******************************");
        try {
            String email = userRequestDto.getEmail();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, userRequestDto.getPassword()));
            User user = userService.getUserByEmail(email);

            if (user == null) {
                throw new UsernameNotFoundException("User with email: " + email + " not found");
            }

//            String token = jwtTokenProvider.createToken(email, user.getRoles());

            return ResponseEntity.ok(new UserTokenDto().token(null));
//            return ResponseEntity.ok(new UserTokenDto().token(token));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public ResponseEntity<UserTokenDto> registrationUser(UserRequestDto userRequestDto) {
        userRequestDto.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        userService.addNewUser(userRequestDto);
        return ResponseEntity.ok(new UserTokenDto().token("success"));
    }
}
