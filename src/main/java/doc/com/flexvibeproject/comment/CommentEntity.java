package doc.com.flexvibeproject.comment;

import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comments")
public class CommentEntity {

    private static final String GENERATOR_NAME = "comments_gen";
    private static final String SEQUENCE_NAME = "comments_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    private String comment;
    private CommentRole commentRole;
    private LocalDateTime commentDate;
    private int likeCount = 0;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private MovieEntity movieEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private CommentEntity parentComment;
}
