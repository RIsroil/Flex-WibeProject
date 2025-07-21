package doc.com.flexvibeproject.episode;

import doc.com.flexvibeproject.episode.dto.EpisodeRequest;
import doc.com.flexvibeproject.episode.dto.EpisodeResponse;
import doc.com.flexvibeproject.exception.DuplicateResourceException;
import doc.com.flexvibeproject.exception.InvalidInputException;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.MovieRepository;
import doc.com.flexvibeproject.movie.role.MovieRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EpisodeService {
    private final EpisodeRepository episodeRepository;
    private final MovieRepository movieRepository;

    public void createEpisode(Long id, EpisodeRequest request){
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        EpisodeEntity existingByEpisodeNumber = episodeRepository.findByEpisodeNumber(request.getEpisodeNumber());

        if (movie.getMovieRole() != MovieRole.SERIAL) {throw new InvalidInputException("This Movie is not a Serial");}
        if (episodeRepository.findByTitle(request.getTitle()) != null) {throw new DuplicateResourceException("This title is already taken");}
        if (request.getSeason() == null || request.getEpisodeNumber() == null) {throw new InvalidInputException("Season and Episode number are required");}
        if (request.getDuration() == null) {throw new InvalidInputException("Duration is required");}
        if (request.getFilePath() == null) {throw new InvalidInputException("File path is required");}
        if (request.getTitle() == null) {throw new InvalidInputException("Title is required");}
        if (request.getSeason() <= 0 || request.getEpisodeNumber() <= 0) {
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
                .createdDate(LocalDateTime.now())
                .build();
        episodeRepository.save(newEpisode);
    }

    public List<EpisodeResponse> getEpisodesBySerialId(Long serialId) {
        MovieEntity serial = movieRepository.findById(serialId)
                .orElseThrow(() -> new ResourceNotFoundException("Serial (Movie) not found with id: " + serialId));

        if (serial.getMovieRole() != MovieRole.SERIAL) {
            throw new InvalidInputException("Movie with id " + serialId + " is not of role SERIAL");
        }

        return episodeRepository.findAllByMovieEntity(serial)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EpisodeResponse getEpisodeById(Long id){
        EpisodeEntity episode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found") );

        return mapToResponse(episode);
    }

    public void updateEpisode(Long id, EpisodeRequest request){
        EpisodeEntity episode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        EpisodeEntity existing = episodeRepository.findByTitle(request.getTitle());
        if (existing != null && !existing.getId().equals(episode.getId())) {
            throw new DuplicateResourceException("Title already exists");
        }
        if (request.getTitle() != null) {episode.setTitle(request.getTitle());}
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

    @Transactional
    public void incrementViews(Long id) {
        EpisodeEntity episode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found" + id));
        episode.setViewCount(episode.getViewCount() + 1);
        episodeRepository.save(episode);
    }

    private EpisodeResponse mapToResponse(EpisodeEntity e) {
        return EpisodeResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .filePath(e.getFilePath())
                .duration(e.getDuration())
                .season(e.getSeason())
                .episodeNumber(e.getEpisodeNumber())
                .viewCount(e.getViewCount())
                .likeCount(e.getLikeCount())
                .build();
    }
}
