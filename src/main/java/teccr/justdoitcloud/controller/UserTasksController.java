package teccr.justdoitcloud.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import teccr.justdoitcloud.data.Task;
import teccr.justdoitcloud.data.User;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/user/tasks")
@SessionAttributes("user")
public class UserTasksController {

    @ModelAttribute(name = "user")
    public User user() {
        User usr = new User("jrugama96", "Jonathan Rugama", "jrugama96@outlook.com", User.Type.REGULAR);
        // Add a few sample tasks
        Task task = new Task("Comprar Leche", LocalDateTime.now(), null, Task.Status.DONE);
        usr.addTask(task);
        task = new Task("Reparacion de sistema de frenos del carro", LocalDateTime.now(),
                LocalDateTime.now().plusDays(3).toLocalDate(), Task.Status.INPROGRESS);
        usr.addTask(task);
        return usr;
    }

    @GetMapping
    public String showUserTasks(Model model) {
        model.addAttribute("newTask", new Task("", LocalDateTime.now(), null, Task.Status.INPROGRESS));
        return "usertasks";
    }

    @PostMapping
    public String addTask(@Valid @ModelAttribute(name = "newTask") Task newTask,
            Errors errors,
            @ModelAttribute("user") User user) {
        log.info("Adding task: " + newTask);
        if (errors.hasErrors()) {
            return "usertasks";
        }

        user.addTask(newTask);
        return "redirect:/user/tasks";
    }
}
