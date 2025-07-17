package doc.com.flexvibeproject.save;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SaveMovieRepository extends JpaRepository<SaveMovieEntity,Integer> {
    // Find all saved movies for a user
    List<SaveMovieEntity> findByUserIdOrderBySavedAtDesc(Long userId);

    // Check if movie is already saved by user
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SaveMovieEntity s WHERE s.user.id = :userId AND s.movie.id = :movieId")
    boolean existsByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    // Find specific saved movie by user and movie
    @Query("SELECT s FROM SaveMovieEntity s WHERE s.user.id = :userId AND s.movie.id = :movieId")
    Optional<SaveMovieEntity> findByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    // Delete saved movie by user and movie
    @Modifying
    @Query("DELETE FROM SaveMovieEntity s WHERE s.user.id = :userId AND s.movie.id = :movieId")
    void deleteByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    // Get count of saved movies for user
    @Query("SELECT COUNT(s) FROM SaveMovieEntity s WHERE s.user.id = :userId")
    long countSavedMoviesByUserId(@Param("userId") Long userId);
}
