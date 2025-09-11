package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.movie.dto.*;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;


    @PostMapping()
    public void addMovie(@RequestBody MovieRequest request) {
        movieService.createMovie(request);
    }

    @PatchMapping("/{id}")
    public void updateMovie(@PathVariable Long id, @RequestBody MovieUpdateRequest request) {
        movieService.updateMovie(id, request);
    }

    @GetMapping("/{id}")
    public MovieResponse getByMovieId(@PathVariable Long id) {
        return movieService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteMovieById(@PathVariable Long id) {
        movieService.deleteMovieById(id);
    }

    @GetMapping
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/page")
    public Page<MovieResponse> getAllMovies(
            Pageable pageable) {
        return movieService.getAllMoviesByPage(pageable);
    }

    @GetMapping("/by-role")
    public Page<MovieResponse> getMoviesByRole(@RequestParam MovieRole role, Pageable pageable) {
        return movieService.getMoviesByRole(role, pageable);
    }

    @GetMapping("/genres/available")
    public List<String> getAvailableGenres() {
        return movieService.getAvailableGenres();
    }

    @GetMapping("/by-genre")
    public Page<MovieResponse> getMoviesByGenre(@RequestParam MovieGenre genre, Pageable pageable) {
        return movieService.getMoviesByGenre(genre, pageable);
    }

    @GetMapping("/years/available")
    public List<Integer> getAvailableYears() {
        return movieService.getAllAvailableYears();
    }

    @GetMapping("/by-year")
    public Page<MovieResponse> getMoviesByYear(@RequestParam Integer year, Pageable pageable) {
        return movieService.getMoviesByYear(year, pageable);
    }

    @GetMapping("/languages/available")
    public List<String> getAvailableLanguages() {
        return movieService.getAvailableLanguages();
    }

    @GetMapping("/by-language")
    public Page<MovieResponse> getMoviesByLanguage(@RequestParam LanguageType language, Pageable pageable) {
        return movieService.getMoviesByLanguage(language, pageable);
    }

    @PutMapping("/{id}/views")
    public void incrementMovieViews(@PathVariable Long id) {
        movieService.incrementViews(id);
    }

    @GetMapping("/all-movie-roles")
    public ResponseEntity<MovieGenre[]> getAllCommentRoles() {
        return ResponseEntity.ok(MovieGenre.values());
    }

    @GetMapping("/search")
    public Page<MovieResponse> search(@RequestParam String query, Pageable pageable) {
        return movieService.smartSearch(query, pageable);
    }

    @GetMapping("/views")
    public ResponseEntity<Integer> getViewsCount(@RequestParam(name = "isLastMonth", defaultValue = "false") boolean isLastMonth) {
        int viewsCount = movieService.getViewsCount(isLastMonth);
        return ResponseEntity.ok(viewsCount);
    }

    @GetMapping("/counts/last-month")
    public ResponseEntity<ContentCountsResponse> getContentCountsLastMonth() {
        ContentCountsResponse response = movieService.getContentCountsLastMonth();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/counts/all")
    public ResponseEntity<TotalContentCountsResponse> getTotalContentCounts() {
        TotalContentCountsResponse response = movieService.getTotalContentCounts();
        return ResponseEntity.ok(response);
    }
}
