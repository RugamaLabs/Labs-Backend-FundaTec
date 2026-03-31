package teccr.justdoitcloud.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import teccr.justdoitcloud.data.User;
import teccr.justdoitcloud.service.UserService;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminHome(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "admin";
    }

    @GetMapping("/admin/users")
    public String showCreateUserForm(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("newUser", new User());
        return "admin-users";
    }

    @PostMapping("/admin/users")
    public String createUser(@ModelAttribute("newUser") User newUser) {
        userService.createUser(newUser);
        return "redirect:/admin/users?success";
    }
}
