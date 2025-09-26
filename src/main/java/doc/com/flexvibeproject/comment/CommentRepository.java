package doc.com.flexvibeproject.comment;

import doc.com.flexvibeproject.movie.MovieEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("SELECT c FROM CommentEntity c WHERE (c.commentRole = doc.com.flexvibeproject.comment.CommentRole.MOVIE OR c.commentRole = doc.com.flexvibeproject.comment.CommentRole.REPLY) AND c.movieEntity.id = :id")
    List<CommentEntity> findAllByMovieComments(@Param("id") Long id);

    @Query("SELECT c FROM CommentEntity c WHERE c.commentRole = doc.com.flexvibeproject.comment.CommentRole.WEBSITE")
    List<CommentEntity> findAllByWebsiteComments();

    // Paginated queries for movie comments (only top-level comments)
    @Query("SELECT c FROM CommentEntity c WHERE c.commentRole = doc.com.flexvibeproject.comment.CommentRole.MOVIE AND c.movieEntity.id = :movieId AND c.parentComment IS NULL")
    Page<CommentEntity> findTopLevelCommentsByMovieId(@Param("movieId") Long movieId, Pageable pageable);

    // Find replies to a specific comment
    @Query("SELECT c FROM CommentEntity c WHERE c.parentComment.id = :parentCommentId")
    Page<CommentEntity> findRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId, Pageable pageable);

    int countAllByMovieEntity(MovieEntity movie);

    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.parentComment = :comment")
    int countAllByParentComment(@Param("comment") CommentEntity comment);

    // Count replies by parent comment ID
    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.parentComment.id = :parentCommentId")
    int countRepliesByParentCommentId(@Param("parentCommentId") Long parentCommentId);
}
