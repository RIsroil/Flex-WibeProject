package doc.com.flexvibeproject.episode;

import doc.com.flexvibeproject.episode.dto.EpisodeDto;
import doc.com.flexvibeproject.exception.DuplicateResourceException;
import doc.com.flexvibeproject.exception.InvalidInputException;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.MovieRepository;
import doc.com.flexvibeproject.movie.role.MovieRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final MovieRepository movieRepository;

    public void createEpisode(Long id, EpisodeDto request){
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        EpisodeEntity existingByEpisodeNumber = episodeRepository.findByEpisodeNumber(request.getEpisodeNumber());

        if (movie.getMovieRole() != MovieRole.SERIAL) {throw new InvalidInputException("This Movie is not a Serial");}
        if (episodeRepository.findByTitle(request.getTitle()) != null) {throw new DuplicateResourceException("This title is already taken");}
        if (request.getSeason() == null || request.getEpisodeNumber() == null) {throw new InvalidInputException("Season and Episode number are required");}
        if (request.getDuration() == null) {throw new InvalidInputException("Duration is required");}
        if (request.getFilePath() == null) {throw new InvalidInputException("File path is required");}
        if (request.getTitle() == null) {throw new InvalidInputException("Title is required");}
        if (request.getSeason() < 0 || request.getEpisodeNumber() < 0) {
            throw new InvalidInputException("Season and Episode number must be positive");
        }
        if (existingByEpisodeNumber != null &&
                (request.getSeason().equals(existingByEpisodeNumber.getSeason()) ||
                        request.getEpisodeNumber().equals(existingByEpisodeNumber.getEpisodeNumber()))) {
            throw new DuplicateResourceException("This episode already exists");
        }


        EpisodeEntity newEpisode = EpisodeEntity.builder()
                .title(request.getTitle())
                .filePath(request.getFilePath())
                .season(request.getSeason())
                .episodeNumber(request.getEpisodeNumber())
                .duration(request.getDuration())
                .movieEntity(movie)
                .build();
        episodeRepository.save(newEpisode);
    }

    public EpisodeDto getEpisodeById(Long id){
        EpisodeEntity episode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found") );

        return EpisodeDto.builder()
                .title(episode.getTitle())
                .filePath(episode.getFilePath())
                .duration(episode.getDuration())
                .season(episode.getSeason())
                .episodeNumber(episode.getEpisodeNumber())
                .build();
    }

    public void updateEpisode(Long id, EpisodeDto request){
        EpisodeEntity episode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        EpisodeEntity existing = episodeRepository.findByTitle(request.getTitle());
        if (existing != null && !existing.getId().equals(episode.getId())) {
            throw new DuplicateResourceException("Title already exists");
        }
        if (request.getFilePath() != null) {episode.setFilePath(request.getFilePath());}
        if (request.getSeason() != null) {episode.setSeason(request.getSeason());}
        if (request.getEpisodeNumber() != null) {episode.setEpisodeNumber(request.getEpisodeNumber());}
        if (request.getDuration() != null) {episode.setDuration(request.getDuration());}

        episodeRepository.save(episode);
    }

    public void deleteEpisodeById(Long id){
        EpisodeEntity episode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        episodeRepository.delete(episode);
    }
}
