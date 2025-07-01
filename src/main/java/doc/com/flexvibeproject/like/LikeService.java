package doc.com.flexvibeproject.like;

import doc.com.flexvibeproject.comment.CommentEntity;
import doc.com.flexvibeproject.comment.CommentRepository;
import doc.com.flexvibeproject.episode.EpisodeEntity;
import doc.com.flexvibeproject.episode.EpisodeRepository;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.MovieRepository;
import doc.com.flexvibeproject.movie.role.MovieRole;
import doc.com.flexvibeproject.user.UserEntity;
import doc.com.flexvibeproject.user.auth.AuthHelperService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;   // ✅ yangi
    private final CommentRepository commentRepository;
    private final AuthHelperService authHelperService;

    @Transactional
    public void toggleLike(Long movieId,
                           Long episodeId,
                           Long commentId,
                           Principal principal) {

        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        // 1) Faqat bitta ID kelganini tekshiramiz
        long nonNullCount = Stream.of(movieId, episodeId, commentId)
                .filter(Objects::nonNull)
                .count();
        if (nonNullCount != 1) {
            throw new IllegalArgumentException("Like faqat Movie, Episode yoki Comment uchun bo‘lishi kerak (faqat bittasi).");
        }

        if (movieId != null) {
            MovieEntity movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            if(movie.getMovieRole() == MovieRole.SERIAL) {
                throw new IllegalArgumentException("You can not like SERIAL");
            }
            toggleMovieLike(user, movie);
            movieRepository.save(movie);
            return;
        }

        // 3) Episode like / unlike
        if (episodeId != null) {
            EpisodeEntity episode = episodeRepository.findById(episodeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
            toggleEpisodeLike(user, episode);
            episodeRepository.save(episode);
            return;
        }

        // 4) Comment like / unlike
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        toggleCommentLike(user, comment);
        commentRepository.save(comment);
    }

    /* ---------- Private helpers ---------- */

    private void toggleMovieLike(UserEntity user, MovieEntity movie) {
        likeRepository.findByUserAndMovie(user, movie).ifPresentOrElse(existing -> {
            movie.setLikeCount(movie.getLikeCount() - 1);
            likeRepository.delete(existing);
        }, () -> {
            movie.setLikeCount(movie.getLikeCount() + 1);
            likeRepository.save(LikeEntity.builder()
                    .user(user).movie(movie).episode(null).comment(null).build());
        });
    }

    private void toggleEpisodeLike(UserEntity user, EpisodeEntity episode) {
        likeRepository.findByUserAndEpisode(user, episode).ifPresentOrElse(existing -> {
            episode.setLikeCount(episode.getLikeCount() - 1);
            likeRepository.delete(existing);
        }, () -> {
            episode.setLikeCount(episode.getLikeCount() + 1);
            likeRepository.save(LikeEntity.builder()
                    .user(user).movie(null).episode(episode).comment(null).build());
        });
    }

    private void toggleCommentLike(UserEntity user, CommentEntity comment) {
        likeRepository.findByUserAndComment(user, comment).ifPresentOrElse(existing -> {
            comment.setLikeCount(comment.getLikeCount() - 1);
            likeRepository.delete(existing);
        }, () -> {
            comment.setLikeCount(comment.getLikeCount() + 1);
            likeRepository.save(LikeEntity.builder()
                    .user(user).movie(null).episode(null).comment(comment).build());
        });
    }
}
