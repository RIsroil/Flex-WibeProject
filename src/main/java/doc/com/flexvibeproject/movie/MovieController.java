package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.comment.CommentRole;
import doc.com.flexvibeproject.minio.MinioService;
import doc.com.flexvibeproject.movie.dto.MovieRequest;
import doc.com.flexvibeproject.movie.dto.MovieResponse;
import doc.com.flexvibeproject.movie.dto.MovieUpdateRequest;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/movie")
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
        MovieResponse movie = movieService.getById(id);
        try {
            if (movie.getFilePath() != null) {
                String fileName = minioService.extractFileName(movie.getFilePath());
                movie.setFilePath(minioService.getPresignedUrl(fileName));
            }
            if (movie.getTrailerPath() != null) {
                String fileName = minioService.extractFileName(movie.getTrailerPath());
                movie.setTrailerPath(minioService.getPresignedUrl(fileName));
            }
            if (movie.getImageUrl() != null) {
                String fileName = minioService.extractFileName(movie.getImageUrl());
                movie.setImageUrl(minioService.getPresignedUrl(fileName));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URLs: " + e.getMessage());
        }
        return movie;
    }

//    @DeleteMapping("/{id}")
//    public void deleteMovieById(@PathVariable Long id) {
//        movieService.deleteMovieById(id);
//    }

    @GetMapping
    public List<MovieResponse> getAllMovies() {
        return movieService.getAllMovies();
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
}
