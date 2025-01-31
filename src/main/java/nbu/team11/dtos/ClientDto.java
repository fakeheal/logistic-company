package nbu.team11.dtos;
import java.time.Instant;
import lombok.Data;

@Data
public class ClientDto {
    private Integer id;
    private Integer userId;
    private String phoneNumber;
    private Instant createdOn;
    private Instant updatedOn;
}
