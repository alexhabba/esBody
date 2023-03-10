package market.habba.controller;

import lombok.RequiredArgsConstructor;
import market.habba.entity.User;
import market.habba.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

//    @PostMapping("register")
//    public String registrationUser(@RequestBody User userDto) {
//        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
////        userService.addNewUser(userDto);
//        return "home";
//    }

//    @GetMapping("/api/admin/get")
//    public String getAut() {
//        return "dfjngjdfsngjndfjhngjidfihidfsjhjsfdih";
//    }
//
//    @GetMapping("/api/get")
//    public String getAutt() {
//        return "dfjngjdfsngjndfjhngjidfihidfsjhjsfdih";
//    }
//
//
//    @GetMapping("/")
//    public String getAuttt() {
//        return "dfjngjdfsngjndfjhngjidfihidfsjhjsfdih";
//    }
}
