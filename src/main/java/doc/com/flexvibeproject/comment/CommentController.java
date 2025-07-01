package doc.com.flexvibeproject.comment;

import doc.com.flexvibeproject.comment.dto.CommentRequest;
import doc.com.flexvibeproject.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public void addComment(Principal principal, Long movieId, @RequestBody CommentRequest request, CommentRole role) {
        commentService.postComment(principal, movieId, request, role);
    }

    @PutMapping("/{id}")
    public void updateComment(Principal principal, @PathVariable Long id, @RequestBody CommentRequest request) {
        commentService.updateComment(principal, id, request);
    }

    @GetMapping("/{id}")
    public List<CommentResponse> getCommentsByMovieId(@PathVariable Long id) {
        return commentService.getCommentByMovieIdOrWebsite(id);
    }

    @GetMapping
    public List<CommentResponse> getWebsiteComments() {
        return commentService.getCommentByMovieIdOrWebsite(null);
    }

    @DeleteMapping("/{id}")
    public void deleteCommentById(Principal principal, @PathVariable Long id) {
        commentService.deleteCommentById(principal, id);
    }
}
