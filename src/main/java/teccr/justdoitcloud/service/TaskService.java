package teccr.justdoitcloud.service;

import org.springframework.stereotype.Service;
import teccr.justdoitcloud.data.Task;
import teccr.justdoitcloud.data.User;
import teccr.justdoitcloud.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasksForUser(User user) {
        return taskRepository.findByUserId(user.getId());
    }

    public void addTaskToUser(User user, Task task) {
        task.setUserId(user.getId());
        taskRepository.save(task);
    }

    public void advanceTaskStatus(Long taskId, User user) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task != null && task.getUserId().equals(user.getId())) {
            if (task.getStatus() == Task.Status.PENDING) {
                task.setStatus(Task.Status.INPROGRESS);
                taskRepository.save(task);
            } else if (task.getStatus() == Task.Status.INPROGRESS) {
                task.setStatus(Task.Status.DONE);
                taskRepository.save(task);
            }
        }
    }
}
