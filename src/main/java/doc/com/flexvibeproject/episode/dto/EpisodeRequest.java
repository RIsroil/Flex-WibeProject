package doc.com.flexvibeproject.episode.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EpisodeRequest {
    private String title;
    private String filePath;
    private String duration;
    private Integer season;
    private Integer episodeNumber;

}
