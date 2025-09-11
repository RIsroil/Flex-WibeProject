package doc.com.flexvibeproject.save;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SaveMovieRepository extends JpaRepository<SaveMovieEntity,Integer> {
    Page<SaveMovieEntity> findByUserIdOrderBySavedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SaveMovieEntity s WHERE s.user.id = :userId AND s.movie.id = :movieId")
    boolean existsByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    @Modifying
    @Query("DELETE FROM SaveMovieEntity s WHERE s.user.id = :userId AND s.movie.id = :movieId")
    void deleteByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    @Query("SELECT COUNT(s) FROM SaveMovieEntity s WHERE s.user.id = :userId")
    long countSavedMoviesByUserId(@Param("userId") Long userId);
}
