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
    public ResponseEntity<String> addComment(
            Principal principal,
            @PathVariable(required = false) Long id,
            @RequestBody CommentRequest request,
            @RequestParam CommentRole role) {
        commentService.postComment(principal, id, request, role);
        return ResponseEntity.ok("Comment added successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(
            Principal principal,
            @PathVariable Long id,
            @RequestBody CommentRequest request) {
        commentService.updateComment(principal, id, request);
        return ResponseEntity.ok("Comment updated successfully");
    }

    /**
     * Get paginated top-level comments for a movie
     * Returns only direct comments on movie (not replies)
     * Each comment includes replyCount field
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<Page<CommentResponse>> getMovieComments(
            @PathVariable Long movieId,
            Pageable pageable) {
        Page<CommentResponse> comments = commentService.getMovieCommentsPaginated(movieId, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Get paginated replies for a specific comment
     * Can be called recursively for nested replies
     * Each reply also includes replyCount for its own sub-replies
     */
    @GetMapping("/replies/{parentCommentId}")
    public ResponseEntity<Page<CommentResponse>> getReplies(
            @PathVariable Long parentCommentId,
            Pageable pageable) {
        Page<CommentResponse> replies = commentService.getRepliesPaginated(parentCommentId, pageable);
        return ResponseEntity.ok(replies);
    }

    /**
     * Post a reply to an existing comment
     */
    @PostMapping("/reply/{parentCommentId}")
    public ResponseEntity<String> addReply(
            Principal principal,
            @PathVariable Long parentCommentId,
            @RequestBody CommentRequest request) {
        commentService.postComment(principal, parentCommentId, request, CommentRole.REPLY);
        return ResponseEntity.ok("Reply added successfully");
    }

    // Keep the old endpoint for backward compatibility
    @GetMapping("/{id}")
    public ResponseEntity<List<CommentResponse>> getCommentsByMovieId(
            @PathVariable(required = false) Long id) {
        List<CommentResponse> comments = commentService.getCommentByMovieIdOrWebsite(id);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommentById(
            Principal principal,
            @PathVariable Long id) {
        commentService.deleteCommentById(principal, id);
        return ResponseEntity.ok("Comment deleted successfully");
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