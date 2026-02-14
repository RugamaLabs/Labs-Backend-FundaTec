package teccr.justdoitcloud.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class Task {
    private final String id;
    @Size(min = 3, message = "Descripcion debe tener al menos 3 caracteres")
    private final String description;
    private final LocalDateTime created;
    private final LocalDate deadline;
    @NotNull
    private final Status status;

    public Task(String description, LocalDateTime created, LocalDate deadline, Status status) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.created = created;
        this.deadline = deadline;
        this.status = status;
    }

    public enum Status {
        PENDING,
        INPROGRESS,
        DONE
    }
}
