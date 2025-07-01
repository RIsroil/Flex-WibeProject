package doc.com.flexvibeproject.comment.dto;

import doc.com.flexvibeproject.comment.CommentRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentResponse {
    private String comment;
    private String username;
    private CommentRole commentRole;
    private LocalDateTime commentDate;

    private Long movieId;
}
