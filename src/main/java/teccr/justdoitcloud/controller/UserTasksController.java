package teccr.justdoitcloud.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import teccr.justdoitcloud.data.Task;
import teccr.justdoitcloud.data.User;
import teccr.justdoitcloud.service.TaskService;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/user/tasks")
@SessionAttributes("user")
public class UserTasksController {

    private final TaskService taskService;

    public UserTasksController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String showUserTasks(Model model) {
        Task newTask = new Task(null, "", LocalDateTime.now(), null);
        newTask.setStatus(Task.Status.PENDING);
        model.addAttribute("newTask", newTask);
        // Retrieve user tasks and add to user object in session
        User user = (User) model.getAttribute("user");
        if (user != null) {
            user.setTasks(taskService.getTasksForUser(user));
        }

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

        taskService.addTaskToUser(user, newTask);
        return "redirect:/user/tasks";
    }

    @PostMapping("/{taskId}/advance")
    public String advanceTask(@PathVariable Long taskId, @ModelAttribute("user") User user) {
        if (user != null) {
            taskService.advanceTaskStatus(taskId, user);
        }
        return "redirect:/user/tasks";
    }
}
