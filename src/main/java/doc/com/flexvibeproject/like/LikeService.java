package doc.com.flexvibeproject.like;

import doc.com.flexvibeproject.comment.CommentEntity;
import doc.com.flexvibeproject.comment.CommentRepository;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.MovieRepository;
import doc.com.flexvibeproject.user.UserEntity;
import doc.com.flexvibeproject.user.auth.AuthHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MovieRepository movieRepository;
    private final CommentRepository commentRepository;
    private final AuthHelperService authHelperService;

    @Transactional
    public void toggleLike(Long movieId, Long commentId, Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        if (movieId != null && commentId != null) {
            throw new IllegalArgumentException("Like faqat Movie yoki Comment uchun bo'lishi kerak. Ikkalasiga emas.");
        }

        if (movieId != null) {
            MovieEntity movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            Optional<LikeEntity> existing = likeRepository.findByUserAndMovie(user, movie);

            if (existing.isPresent()) {
                movie.setLikeCount(movie.getLikeCount() - 1);
                likeRepository.delete(existing.get());
            } else {
                movie.setLikeCount(movie.getLikeCount() + 1);
                LikeEntity like = LikeEntity.builder()
                        .user(user)
                        .movie(movie)
                        .comment(null)
                        .build();
                likeRepository.save(like);
            }
            movieRepository.save(movie);
        }

        if (commentId != null) {
            CommentEntity comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

            Optional<LikeEntity> existing = likeRepository.findByUserAndComment(user, comment);

            if (existing.isPresent()) {
                comment.setLikeCount(comment.getLikeCount() - 1);
                likeRepository.delete(existing.get());
            } else {
                comment.setLikeCount(comment.getLikeCount() + 1);
                LikeEntity like = LikeEntity.builder()
                        .user(user)
                        .movie(null)
                        .comment(comment)
                        .build();
                likeRepository.save(like);
            }

            commentRepository.save(comment);
        }
    }
}
