package doc.com.flexvibeproject.comment;

import doc.com.flexvibeproject.movie.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("SELECT c FROM CommentEntity c WHERE c.commentRole = doc.com.flexvibeproject.comment.CommentRole.MOVIE AND c.movieEntity.id = :id")
    List<CommentEntity> findAllByMovieComments(@Param("id") Long id);

    @Query("SELECT c FROM CommentEntity c WHERE c.commentRole = doc.com.flexvibeproject.comment.CommentRole.WEBSITE")
    List<CommentEntity> findAllByWebsiteComments();

    int countAllByMovieEntity(MovieEntity movie);

    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.parentComment = :comment")
    int countAllByParentComment(@Param("comment") CommentEntity comment);
}
