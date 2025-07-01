package doc.com.flexvibeproject.like;

import doc.com.flexvibeproject.comment.CommentEntity;
import doc.com.flexvibeproject.episode.EpisodeEntity;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;

    @ManyToOne
    @JoinColumn(name = "episode_id")
    private EpisodeEntity episode;
}
