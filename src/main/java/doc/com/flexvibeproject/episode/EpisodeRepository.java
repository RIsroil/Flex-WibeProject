package doc.com.flexvibeproject.episode;

import doc.com.flexvibeproject.movie.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpisodeRepository extends JpaRepository<EpisodeEntity, Long> {
    EpisodeEntity findByTitle(String title);
    EpisodeEntity findBySeason(int season);
    EpisodeEntity findByEpisodeNumber(int episode);
    void deleteAllByMovieEntity(MovieEntity movie);
    List<EpisodeEntity> findAllByMovieEntity(MovieEntity movie);
}
