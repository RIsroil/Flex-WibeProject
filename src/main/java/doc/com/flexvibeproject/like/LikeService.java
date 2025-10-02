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
    public boolean toggleLike(Long movieId,
                              Long episodeId,
                              Long commentId,
                              Principal principal) {

        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        // 1) Faqat bitta ID kelganini tekshiramiz
        long nonNullCount = Stream.of(movieId, episodeId, commentId)
                .filter(Objects::nonNull)
                .count();
        if (nonNullCount != 1) {
            throw new IllegalArgumentException("Like faqat Movie, Episode yoki Comment uchun bo'lishi kerak (faqat bittasi).");
        }

        if (movieId != null) {
            MovieEntity movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
            if(movie.getMovieRole() == MovieRole.SERIAL) {
                throw new IllegalArgumentException("You can not like SERIAL");
            }
            boolean isLiked = toggleMovieLike(user, movie);
            movieRepository.save(movie);
            return isLiked;
        }

        // 3) Episode like / unlike
        if (episodeId != null) {
            EpisodeEntity episode = episodeRepository.findById(episodeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
            boolean isLiked = toggleEpisodeLike(user, episode);
            episodeRepository.save(episode);
            return isLiked;
        }

        // 4) Comment like / unlike
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        boolean isLiked = toggleCommentLike(user, comment);
        commentRepository.save(comment);
        return isLiked;
    }

    private boolean toggleMovieLike(UserEntity user, MovieEntity movie) {
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndMovie(user, movie);
        if (existingLike.isPresent()) {
            movie.setLikeCount(movie.getLikeCount() - 1);
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            movie.setLikeCount(movie.getLikeCount() + 1);
            likeRepository.save(LikeEntity.builder()
                    .user(user).movie(movie).episode(null).comment(null).build());
            return true;
        }
    }

    private boolean toggleEpisodeLike(UserEntity user, EpisodeEntity episode) {
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndEpisode(user, episode);
        if (existingLike.isPresent()) {
            episode.setLikeCount(episode.getLikeCount() - 1);
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            episode.setLikeCount(episode.getLikeCount() + 1);
            likeRepository.save(LikeEntity.builder()
                    .user(user).movie(null).episode(episode).comment(null).build());
            return true;
        }
    }

    private boolean toggleCommentLike(UserEntity user, CommentEntity comment) {
        Optional<LikeEntity> existingLike = likeRepository.findByUserAndComment(user, comment);
        if (existingLike.isPresent()) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            likeRepository.delete(existingLike.get());
            return false;
        } else {
            comment.setLikeCount(comment.getLikeCount() + 1);
            likeRepository.save(LikeEntity.builder()
                    .user(user).movie(null).episode(null).comment(comment).build());
            return true;
        }
    }
}
