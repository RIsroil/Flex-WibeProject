package doc.com.flexvibeproject.comment;

import doc.com.flexvibeproject.comment.dto.CommentRequest;
import doc.com.flexvibeproject.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{id}")
    public void addComment(Principal principal, @PathVariable(required = false) Long id, @RequestBody CommentRequest request, @RequestParam CommentRole role) {
        commentService.postComment(principal, id, request, role);
    }

    @PutMapping("/{id}")
    public void updateComment(Principal principal, @PathVariable Long id, @RequestBody CommentRequest request) {
        commentService.updateComment(principal, id, request);
    }

    // New paginated endpoint for movie comments
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<Page<CommentResponse>> getMovieComments(
            @PathVariable Long movieId,
            Pageable pageable) {
        Page<CommentResponse> comments = commentService.getMovieCommentsPaginated(movieId, pageable);
        return ResponseEntity.ok(comments);
    }

    // New paginated endpoint for replies to a comment
    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<Page<CommentResponse>> getReplies(
            @PathVariable Long parentCommentId,
            Pageable pageable) {
        Page<CommentResponse> replies = commentService.getRepliesPaginated(parentCommentId, pageable);
        return ResponseEntity.ok(replies);
    }

    // Keep the old endpoint for backward compatibility
    @GetMapping("/{id}")
    public List<CommentResponse> getCommentsByMovieId(@PathVariable(required = false) Long id) {
        return commentService.getCommentByMovieIdOrWebsite(id);
    }

    @DeleteMapping("/{id}")
    public void deleteCommentById(Principal principal, @PathVariable Long id) {
        commentService.deleteCommentById(principal, id);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCommentCount(
            @RequestParam(name = "movieId", required = false) Long movieId,
            @RequestParam(name = "commentId", required = false) Long commentId) {
        if (movieId != null && commentId != null) {
            throw new IllegalArgumentException("Please provide either movieId or commentId, not both");
        }
        if (movieId == null && commentId == null) {
            throw new IllegalArgumentException("Please provide either movieId or commentId");
        }

        int count = commentService.getCommentCount(movieId, commentId);
        return ResponseEntity.ok(count);
    }
}