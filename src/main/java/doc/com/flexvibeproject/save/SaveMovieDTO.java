package doc.com.flexvibeproject.save;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SaveMovieDTO {
    private Long id;
    private Long movieId;
    private LocalDateTime savedAt;
    private Long userId;
}