package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieRepository extends JpaRepository<MovieEntity, Long>, JpaSpecificationExecutor<MovieEntity> {
    MovieEntity findByTitle(String title);

    @Query("SELECT DISTINCT g FROM MovieEntity m JOIN m.genres g")
    List<MovieGenre> findAllUsedGenres();

    @Query("SELECT DISTINCT g FROM MovieEntity m JOIN m.language g ")
    List<MovieGenre> findAllUsedLanguages();

    Page<MovieEntity> findAllByMovieRole(MovieRole movieRole, Pageable pageable);

    Page<MovieEntity> findByLanguage(LanguageType genre, Pageable pageable);

    Page<MovieEntity> findByGenresContaining(MovieGenre genre, Pageable pageable);

    @Query("SELECT DISTINCT m.releaseYear FROM MovieEntity m ORDER BY m.releaseYear DESC")
    List<Integer> findAllDistinctYears();

    Page<MovieEntity> findAllByReleaseYear(Integer releaseYear, Pageable pageable);

    @Query("SELECT COALESCE(SUM(m.viewCount), 0) FROM MovieEntity m WHERE m.movieRole = :role")
    int sumViewCountByMovieRole(@Param("role") MovieRole role);

    @Query("SELECT COALESCE(SUM(m.viewCount), 0) FROM MovieEntity m WHERE m.movieRole = :role AND m.releaseDateLocal >= :since")
    int sumViewCountByMovieRoleAndLastMonth(@Param("role") MovieRole role, @Param("since") LocalDateTime since);

    int countByMovieRoleAndReleaseDateLocalAfter(@Param("role") MovieRole role, @Param("since") LocalDateTime since);

    List<MovieEntity> findByMovieRoleAndReleaseDateLocalAfter(@Param("role") MovieRole role, @Param("since") LocalDateTime since);

    int countByMovieRole(@Param("role") MovieRole role);

    Page<MovieEntity> findByPremiere(boolean premiere, Pageable pageable);
}
