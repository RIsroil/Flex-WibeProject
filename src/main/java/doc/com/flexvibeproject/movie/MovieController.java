package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.comment.CommentRole;
import doc.com.flexvibeproject.minio.MinioService;
import doc.com.flexvibeproject.movie.dto.*;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("api/movie")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final MinioService minioService;


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
            @PageableDefault(page = 0, size = 20, sort = {"release_date_local"}) Pageable pageable) {
        return movieService.getAllMoviesByPage(pageable);
    }

    @GetMapping("/by-role")
    public List<MovieResponse> getMoviesByRole(@RequestParam MovieRole role) {
        return movieService.getMoviesByRole(role);
    }

    @GetMapping("/genres/available")
    public List<String> getAvailableGenres() {
        return movieService.getAvailableGenres();
    }

    @GetMapping("/by-genre")
    public List<MovieResponse> getMoviesByGenre(@RequestParam MovieGenre genre) {
        return movieService.getMoviesByGenre(genre);
    }

    @GetMapping("/years/available")
    public List<Integer> getAvailableYears() {
        return movieService.getAllAvailableYears();
    }

    @GetMapping("/by-year")
    public List<MovieResponse> getMoviesByYear(@RequestParam Integer year) {
        return movieService.getMoviesByYear(year);
    }

    @GetMapping("/languages/available")
    public List<String> getAvailableLanguages() {
        return movieService.getAvailableLanguages();
    }

    @GetMapping("/by-language")
    public List<MovieResponse> getMoviesByLanguage(@RequestParam LanguageType language) {
        return movieService.getMoviesByLanguage(language);
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
    public List<MovieResponse> search(@RequestParam String query) {
        return movieService.smartSearch(query);
    }

    private String extractFileName(String fullUrl) {
        if (fullUrl == null || fullUrl.isBlank()) {
            throw new IllegalArgumentException("File URL cannot be null or empty");
        }

        String baseUrl = minioService.getPermanentUrl("");
        if (!fullUrl.startsWith(baseUrl)) {
            throw new IllegalArgumentException("Invalid file URL: " + fullUrl);
        }

        return fullUrl.substring(baseUrl.length());
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
