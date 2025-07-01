package doc.com.flexvibeproject.like;

import doc.com.flexvibeproject.comment.CommentEntity;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByUserAndMovie(UserEntity user, MovieEntity movie);
    Optional<LikeEntity> findByUserAndComment(UserEntity user, CommentEntity comment);

    void deleteAllByMovie(MovieEntity movie);
    void deleteAllByComment(CommentEntity comment);

}
