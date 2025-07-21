package doc.com.flexvibeproject.comment;

import doc.com.flexvibeproject.comment.dto.CommentRequest;
import doc.com.flexvibeproject.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{movieId}")
    public void addComment(Principal principal,@PathVariable(required = false) Long movieId, @RequestBody CommentRequest request, CommentRole role) {
        commentService.postComment(principal, movieId, request, role);
    }

    @PutMapping("/{id}")
    public void updateComment(Principal principal, @PathVariable Long id, @RequestBody CommentRequest request) {
        commentService.updateComment(principal, id, request);
    }

    @GetMapping("/{id}")
    public List<CommentResponse> getCommentsByMovieId(@PathVariable(required = false) Long id) {
        return commentService.getCommentByMovieIdOrWebsite(id);
    }

//    @GetMapping
//    public List<CommentResponse> getWebsiteComments() {
//        return commentService.getCommentByMovieIdOrWebsite(null);
//    }

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
