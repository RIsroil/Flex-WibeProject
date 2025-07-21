package doc.com.flexvibeproject.episode;

import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.role.MovieRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EpisodeRepository extends JpaRepository<EpisodeEntity, Long> {
    EpisodeEntity findByTitle(String title);
    EpisodeEntity findBySeason(int season);
    EpisodeEntity findByEpisodeNumber(int episode);
    void deleteAllByMovieEntity(MovieEntity movie);
    List<EpisodeEntity> findAllByMovieEntity(MovieEntity movie);

    int countByMovieEntityAndCreatedAtAfter(@Param("movie") MovieEntity movie, @Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(COUNT(e), 0) FROM EpisodeEntity e WHERE e.movieEntity.movieRole = :role")
    int countByMovieEntityMovieRole(@Param("role") MovieRole role);
}
