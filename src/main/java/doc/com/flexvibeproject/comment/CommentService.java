package doc.com.flexvibeproject.comment;

import doc.com.flexvibeproject.comment.dto.CommentRequest;
import doc.com.flexvibeproject.comment.dto.CommentResponse;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.like.LikeRepository;
import doc.com.flexvibeproject.like.LikeService;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.MovieRepository;
import doc.com.flexvibeproject.user.Role;
import doc.com.flexvibeproject.user.UserEntity;
import doc.com.flexvibeproject.user.auth.AuthHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final MovieRepository movieRepository;
    private final AuthHelperService authHelperService;
    private final LikeRepository likeRepository;

    public void postComment(Principal principal, Long id, CommentRequest request, CommentRole commentRole) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        MovieEntity movie = null;

        if (commentRole == CommentRole.MOVIE) {
            movie = movieRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        }

        CommentEntity newComment = CommentEntity.builder()
                .comment(request.getComment())
                .user(user)
                .commentRole(commentRole)
                .commentDate(LocalDateTime.now())
                .movieEntity(movie)
                .user(user)
                .build();
        commentRepository.save(newComment);
    }

    public void updateComment(Principal principal, Long id, CommentRequest request){
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("You can only update your own comments");
        }
        comment.setComment(request.getComment());
        commentRepository.save(comment);
    }

    public List<CommentResponse> getCommentByMovieIdOrWebsite(Long id){
        if(id == null) {
            return commentRepository.findAllByWebsiteComments().stream()
                    .map(this::mapToResponse)
                    .toList();
        }else {
            MovieEntity movie = movieRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            return commentRepository.findAllByMovieComments(movie.getId()).stream()
                    .map(this::mapToResponse)
                    .toList();
        }
    }

    @Transactional
    public void deleteCommentById(Principal principal, Long id){
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }
        likeRepository.deleteAllByComment(comment);
        commentRepository.delete(comment);
    }

    public int getCommentCount(Long movieId, Long commentId) {
        if (movieId != null) {
            MovieEntity movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            // Count all comments and their replies for the movie
            return commentRepository.countAllByMovieEntity(movie);
        } else {
            CommentEntity comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
            // Count all replies for the specific comment
            return commentRepository.countAllByParentComment(comment);
        }
    }

    private CommentResponse mapToResponse(CommentEntity commentEntity) {
        return CommentResponse.builder()
                .id(commentEntity.getId())
                .movieId(commentEntity.getMovieEntity() != null ? commentEntity.getMovieEntity().getId() : null)
                .comment(commentEntity.getComment())
                .username(commentEntity.getUser().getUsername())
                .commentRole(commentEntity.getCommentRole())
                .commentDate(commentEntity.getCommentDate())
                .build();
    }

}
