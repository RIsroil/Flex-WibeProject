package doc.com.flexvibeproject.episode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import doc.com.flexvibeproject.movie.MovieEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "episodes")
public class EpisodeEntity {
    private static final String GENERATOR_NAME = "episodes_gen";
    private static final String SEQUENCE_NAME = "episodes_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    private String title;
    private String filePath;
    private String duration;
    private Integer season;
    private Integer episodeNumber;
    private int viewCount = 0;
    private int likeCount = 0;
    private LocalDateTime createdDate;
    private Integer countItself;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    @JsonBackReference
    private MovieEntity movieEntity;
}
