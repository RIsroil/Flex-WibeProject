package doc.com.flexvibeproject.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SerialCountResponse {
    private Long serialId;
    private String title;
    private int episodeCount;
}