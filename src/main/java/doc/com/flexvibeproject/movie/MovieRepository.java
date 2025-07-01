package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}
