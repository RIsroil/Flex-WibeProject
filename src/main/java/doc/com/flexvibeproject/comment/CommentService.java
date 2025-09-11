package doc.com.flexvibeproject.comment;

import doc.com.flexvibeproject.comment.dto.CommentRequest;
import doc.com.flexvibeproject.comment.dto.CommentResponse;
import doc.com.flexvibeproject.exception.InvalidInputException;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.like.LikeRepository;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.MovieRepository;
import doc.com.flexvibeproject.user.Role;
import doc.com.flexvibeproject.user.UserEntity;
import doc.com.flexvibeproject.user.auth.AuthHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        CommentEntity parentComment = null;

        if (commentRole == CommentRole.MOVIE) {
            if (id == null) {
                throw new InvalidInputException("Movie id is required");
            }
            movie = movieRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        } else if (commentRole == CommentRole.REPLY) {
            if (id == null) {
                throw new InvalidInputException("Movie id is required");
            }
            parentComment = commentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
            movie = parentComment.getMovieEntity(); // Inherit movie from parent comment
            if (movie == null || parentComment.getCommentRole() != CommentRole.MOVIE) {
                throw new IllegalArgumentException("Replies must be associated with a movie comment");
            }
        } else if (commentRole == CommentRole.WEBSITE) {
            if (id != null) {
                throw new InvalidInputException("Website comments should not have an id");
            }
        }

        CommentEntity newComment = CommentEntity.builder()
                .comment(request.getComment())
                .user(user)
                .movieEntity(movie)
                .parentComment(parentComment)
                .commentRole(commentRole)
                .commentDate(LocalDateTime.now())
                .likeCount(0)
                .build();
        commentRepository.save(newComment);
    }

    public void updateComment(Principal principal, Long id, CommentRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("You can only update your own comments");
        }
        comment.setComment(request.getComment());
        commentRepository.save(comment);
    }

    public Page<CommentResponse> getCommentByMovieIdOrWebsite(Long id, Pageable pageable) {
        if (id == null) {
            Page<CommentEntity> commentPage = commentRepository.findAllByWebsiteComments(pageable);
            List<CommentResponse> commentResponses = commentPage.getContent().stream()
                    .map(this::mapToResponse)
                    .toList();
            return new PageImpl<>(commentResponses, pageable, commentPage.getTotalElements());
        } else {
            MovieEntity movie = movieRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            Page<CommentEntity> commentPage = commentRepository.findAllByMovieComments(movie.getId(), pageable);
            List<CommentResponse> commentResponses = commentPage.getContent().stream()
                    .map(this::mapToResponse)
                    .toList();
            return new PageImpl<>(commentResponses, pageable, commentPage.getTotalElements());
        }
    }

    @Transactional
    public void deleteCommentById(Principal principal, Long id) {
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
            return commentRepository.countAllByMovieEntity(movie);
        } else {
            CommentEntity comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
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
                .like(commentEntity.getLikeCount())
                .build();
    }
}