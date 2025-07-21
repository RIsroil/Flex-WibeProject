package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    MovieEntity findByTitle(String title);

    @Query("SELECT DISTINCT g FROM MovieEntity m JOIN m.genres g")
    List<MovieGenre> findAllUsedGenres();

    @Query("SELECT DISTINCT g FROM MovieEntity m JOIN m.language g ")
    List<MovieGenre> findAllUsedLanguages();

    List<MovieEntity> findAllByMovieRole(MovieRole movieRole);

    List<MovieEntity> findByLanguage(LanguageType genre);


    List<MovieEntity> findByGenresContaining(MovieGenre genre);

    @Query("SELECT DISTINCT m.releaseYear FROM MovieEntity m ORDER BY m.releaseYear DESC")
    List<Integer> findAllDistinctYears();

    List<MovieEntity> findAllByReleaseYear(Integer releaseYear);

    @Query("SELECT e.movieEntity.id, SUM(e.viewCount) " +
            "FROM EpisodeEntity e " +
            "WHERE e.movieEntity.movieRole = doc.com.flexvibeproject.movie.role.MovieRole.SERIAL " +
            "GROUP BY e.movieEntity.id")
    List<Object[]> getTotalViewsPerSerial();

    @Query("SELECT COALESCE(SUM(m.viewCount), 0) FROM MovieEntity m WHERE m.movieRole = :role")
    int sumViewCountByMovieRole(@Param("role") MovieRole role);

    @Query("SELECT COALESCE(SUM(m.viewCount), 0) FROM MovieEntity m WHERE m.movieRole = :role AND m.releaseDateLocal >= :since")
    int sumViewCountByMovieRoleAndLastMonth(@Param("role") MovieRole role, @Param("since") LocalDateTime since);

    int countByMovieRoleAndCreatedAtAfter(@Param("role") MovieRole role, @Param("since") LocalDateTime since);

    List<MovieEntity> findByMovieRoleAndCreatedAtAfter(@Param("role") MovieRole role, @Param("since") LocalDateTime since);

    int countByMovieRole(@Param("role") MovieRole role);

}
