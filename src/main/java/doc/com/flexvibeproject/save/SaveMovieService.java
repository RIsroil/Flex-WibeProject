package doc.com.flexvibeproject.save;

import doc.com.flexvibeproject.episode.EpisodeEntity;
import doc.com.flexvibeproject.episode.dto.EpisodeResponse;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.MovieRepository;
import doc.com.flexvibeproject.user.UserEntity;
import doc.com.flexvibeproject.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaveMovieService {

    private final SaveMovieRepository saveMovieRepository;
    private final AuthHelperService authHelperService;
    private final MovieRepository movieRepository;

    @Transactional
    public void toggleSaveMovie(Principal principal, Long movieId) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + movieId));

        if (saveMovieRepository.existsByUserIdAndMovieId(user.getId(), movieId)) {
            saveMovieRepository.deleteByUserIdAndMovieId(user.getId(), movieId);

        } else {
            SaveMovieEntity savedMovie = SaveMovieEntity.builder()
                    .movie(movie)
                    .user(user)
                    .savedAt(LocalDateTime.now())
                    .build();

            saveMovieRepository.save(savedMovie);

        }
    }

    public List<SaveMovieDTO> getSavedMovies(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        return saveMovieRepository.findByUserIdOrderBySavedAtDesc(user.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SaveMovieDTO mapToResponse(SaveMovieEntity e) {
        return SaveMovieDTO.builder()
                .id(e.getId())
                .movieId(e.getMovie().getId())
                .savedAt(e.getSavedAt())
                .userId(e.getUser().getId())
                .build();
    }

    public long getSavedMoviesCount(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        return saveMovieRepository.countSavedMoviesByUserId(user.getId());
    }
}