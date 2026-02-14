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

    @PostMapping("/advance")
    public String advanceTask(@RequestParam("taskId") String taskId,
            @ModelAttribute("user") User user) {
        log.info("Advancing task with ID: " + taskId);

        // Find the task by ID
        Task taskToAdvance = user.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);

        if (taskToAdvance != null) {
            // Remove the old task
            user.getTasks().remove(taskToAdvance);

            // Create a new task with advanced status
            Task.Status newStatus = advanceStatus(taskToAdvance.getStatus());
            Task updatedTask = new Task(
                    taskToAdvance.getDescription(),
                    taskToAdvance.getCreated(),
                    taskToAdvance.getDeadline(),
                    newStatus);

            // Add the updated task
            user.addTask(updatedTask);
            log.info("Task advanced from {} to {}", taskToAdvance.getStatus(), newStatus);
        }

        return "redirect:/user/tasks";
    }

    private Task.Status advanceStatus(Task.Status currentStatus) {
        return switch (currentStatus) {
            case PENDING -> Task.Status.INPROGRESS;
            case INPROGRESS -> Task.Status.DONE;
            case DONE -> Task.Status.DONE; // Already done, no change
        };
    }
}
