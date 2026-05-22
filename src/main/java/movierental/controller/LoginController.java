package movierental.controller;

import movierental.model.User;
import movierental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String login(@RequestParam String username,
                        @RequestParam String password) {

        User user = userService.authenticate(username, password);

        if (user == null) {
            return "Invalid login";
        }

        if (user.getRole().equals("ADMIN")) {
            return "Admin logged in";
        } else {
            return "User logged in";
        }
    }
}