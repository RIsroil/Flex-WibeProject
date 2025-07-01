package doc.com.flexvibeproject.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentRequest {
    private String comment;
}
