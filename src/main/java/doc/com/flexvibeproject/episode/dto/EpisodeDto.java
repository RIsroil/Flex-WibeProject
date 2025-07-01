package doc.com.flexvibeproject.episode.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EpisodeDto {
    private String title;
    private String filePath;
    private String duration;
    private Integer season;
    private Integer episodeNumber;
    private int viewCount;
    private int likeCount;
}
