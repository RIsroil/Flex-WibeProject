package doc.com.flexvibeproject.save;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/save-movie")
@RequiredArgsConstructor
public class SaveMovieController {

    private final SaveMovieService saveMovieService;

    @PostMapping("/{movieId}")
    public void toggleSaveMovie(
            Principal principal,
            @PathVariable("movieId") Long movieId) {

        saveMovieService.toggleSaveMovie(principal, movieId);
    }

    @GetMapping
    public Page<SaveMovieDTO> getSavedMovies(Principal principal, Pageable pageable) {
        return saveMovieService.getSavedMovies(principal, pageable);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getSavedMoviesCount(Principal principal) {
        long count = saveMovieService.getSavedMoviesCount(principal);
        return ResponseEntity.ok(count);
    }
}