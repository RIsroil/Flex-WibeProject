package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.comment.CommentEntity;
import doc.com.flexvibeproject.comment.CommentRepository;
import doc.com.flexvibeproject.episode.EpisodeEntity;
import doc.com.flexvibeproject.episode.EpisodeRepository;
import doc.com.flexvibeproject.exception.DuplicateResourceException;
import doc.com.flexvibeproject.exception.InvalidInputException;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.like.LikeRepository;
import doc.com.flexvibeproject.minio.MinioConfig;
import doc.com.flexvibeproject.minio.MinioService;
import doc.com.flexvibeproject.movie.dto.*;
import doc.com.flexvibeproject.movie.role.CountryType;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final EpisodeRepository episodeRepository;


    public void createMovie(MovieRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new InvalidInputException("Title is required");
        }

        if (movieRepository.findByTitle(request.getTitle()) != null) {
            throw new DuplicateResourceException("This title is already taken");
        }

        if (request.getMovieRole() == null) {
            throw new InvalidInputException("Movie role is required");
        }

        if (request.getMovieRole() == MovieRole.SERIAL) {
            if (request.getFilePath() != null && !request.getFilePath().isBlank()) {
                throw new InvalidInputException("File path (SERIAL) is required for FilePath");
            }
        }

        MovieEntity movie = MovieEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .filePath(request.getFilePath())        // 🎥 Asosiy video
                .trailerPath(request.getTrailerPath())  // 🎞️  Trailer
                .imageUrl(request.getImageUrl())       // 🖼️  Rasm
                .genres(request.getGenres())
                .country(request.getCountry())
                .releaseYear(request.getReleaseYear())
                .duration(request.getDuration())
                .language(request.getLanguage())
                .ageLimit(request.getAgeLimit())
                .premiere(request.isPremiere())
                .releaseDateLocal(LocalDateTime.now())
                .movieRole(request.getMovieRole())
                .build();
        movieRepository.save(movie);
    }

    public void updateMovie(Long id, MovieUpdateRequest request) {
        MovieEntity existing = movieRepository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Movie not found with id: " + id));
        
        if (request.getDescription() != null) {existing.setDescription(request.getDescription());}
        if (request.getFilePath() != null) {existing.setFilePath(request.getFilePath());}
        if (request.getGenres() != null) {existing.setGenres(request.getGenres());}
        if (request.getCountry() != null) {existing.setCountry(request.getCountry());}
        if (request.getReleaseYear() != null) {existing.setReleaseYear(request.getReleaseYear());}
        if (request.getDuration() != null) {existing.setDuration(request.getDuration());}
        if (request.getLanguage() != null) {existing.setLanguage(request.getLanguage());}
        if (request.getAgeLimit() != null) {existing.setAgeLimit(request.getAgeLimit());}
        if (request.getImageUrl() != null) {existing.setImageUrl(request.getImageUrl());}
        if (request.getTrailerPath() != null) {existing.setTrailerPath(request.getTrailerPath());}
        movieRepository.save(existing);
    }

    public MovieResponse getById(Long id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        return mapToResponse(movie);
    }

    @Transactional
    public void deleteMovieById(Long movieId) {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        movieRepository.delete(movie);
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Page<MovieResponse> getAllMoviesByPage(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public List<MovieResponse> getMoviesByRole(MovieRole role){
        return movieRepository.findAllByMovieRole(role)
                .stream().map(this::mapToResponse).toList();
    }

    public List<String> getAvailableGenres() {
        return movieRepository.findAllUsedGenres()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public List<MovieResponse> getMoviesByGenre(MovieGenre genre) {
        return movieRepository.findByGenresContaining(genre).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<Integer> getAllAvailableYears() {
        return movieRepository.findAllDistinctYears();
    }

    public List<MovieResponse> getMoviesByYear(Integer year) {
        return movieRepository.findAllByReleaseYear(year).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<String> getAvailableLanguages() {
        return movieRepository.findAllUsedLanguages()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public List<MovieResponse> getMoviesByLanguage(LanguageType language) {
        return movieRepository.findByLanguage(language).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<MovieResponse> smartSearch(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return List.of();
        }

        String text = rawText.toLowerCase();
        String[] tokens = text.split("\\s+");

        Set<MovieGenre> foundGenres = new HashSet<>();
        Set<LanguageType> foundLanguages = new HashSet<>();
        Set<CountryType> foundCountries = new HashSet<>();
        Set<MovieRole> foundTypes = new HashSet<>();
        Integer foundYear = null;
        Boolean isPremiere = null;
        StringBuilder titleBuilder = new StringBuilder();

        for (String word : tokens) {

            fuzzyMatchEnum(MovieGenre.class, word).ifPresent(foundGenres::add);

            fuzzyMatchEnum(LanguageType.class, word).ifPresent(foundLanguages::add);

            fuzzyMatchEnum(CountryType.class, word).ifPresent(foundCountries::add);

            fuzzyMatchEnum(MovieRole.class, word).ifPresent(foundTypes::add);

            if (word.contains("premyera") || word.contains("premera") || word.contains("primyera")) {
                isPremiere = true;
            }

            if (word.matches("\\d{4}")) {
                foundYear = Integer.parseInt(word);
            } else if (word.matches("\\d{4}-?yil")) {
                foundYear = Integer.parseInt(word.replace("-yil", "").replace("yil", ""));
            }

            if (
                    !fuzzyMatchEnum(MovieGenre.class, word).isPresent() &&
                            !fuzzyMatchEnum(LanguageType.class, word).isPresent() &&
                            !fuzzyMatchEnum(CountryType.class, word).isPresent() &&
                            !fuzzyMatchEnum(MovieRole.class, word).isPresent() &&
                            !word.matches("\\d{4}(-?yil)?") &&
                            !word.contains("premyera")
            ) {
                titleBuilder.append(word).append(" ");
            }
        }

        String titleLike = titleBuilder.toString().trim();

        List<MovieEntity> allMovies = movieRepository.findAll();

        Integer finalFoundYear1 = foundYear;
        Boolean finalIsPremiere1 = isPremiere;
        return allMovies.stream()
                .filter(m -> titleLike.isEmpty() || isSimilar(m.getTitle(), titleLike))
                .filter(m -> foundGenres.isEmpty() || !Collections.disjoint(m.getGenres(), foundGenres))
                .filter(m -> foundLanguages.isEmpty() || foundLanguages.contains(m.getLanguage()))
                .filter(m -> foundCountries.isEmpty() || foundCountries.contains(m.getCountry()))
                .filter(m -> foundTypes.isEmpty() || foundTypes.contains(m.getMovieRole()))
                .filter(m -> finalFoundYear1 == null || m.getReleaseYear() == finalFoundYear1)
                .filter(m -> finalIsPremiere1 == null || m.isPremiere() == finalIsPremiere1)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private boolean isSimilar(String a, String b) {
        LevenshteinDistance distance = new LevenshteinDistance();
        int maxAllowed = 2;

        int result = distance.apply(a.toLowerCase(), b.toLowerCase());
        return result <= maxAllowed;
    }

    public <E extends Enum<E>> Optional<E> fuzzyMatchEnum(Class<E> enumClass, String input) {
        LevenshteinDistance distance = new LevenshteinDistance();
        int maxDistance = 2;

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> distance.apply(e.name().toLowerCase(), input.toLowerCase()) <= maxDistance)
                .findFirst();
    }

    @Transactional
    public void incrementViews(Long movieId) {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        if (movie.getMovieRole() == MovieRole.SERIAL) {
            throw new ResourceNotFoundException("You can not post view for Serial: " + movieId);
        }else{
            movie.setViewCount(movie.getViewCount() + 1);
        }
        movieRepository.save(movie);
    }

    private MovieResponse mapToResponse(MovieEntity movie) {
        int episodesCount = 0;
        episodesCount = movie.getMovieRole() == MovieRole.SERIAL
                ? episodeRepository.findAllByMovieEntity(movie)
                .stream()
                .mapToInt(EpisodeEntity::getCountItself)
                .sum()
                : movie.getEpisodeCount();

        int views =  movie.getMovieRole() == MovieRole.SERIAL
                ? episodeRepository.findAllByMovieEntity(movie)
                .stream()
                .mapToInt(EpisodeEntity::getViewCount)
                .sum()
                : movie.getViewCount();

        int likes =  movie.getMovieRole() == MovieRole.SERIAL
                ? episodeRepository.findAllByMovieEntity(movie)
                .stream()
                .mapToInt(EpisodeEntity::getLikeCount)
                .sum()
                : movie.getLikeCount();

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .filePath(movie.getFilePath())
                .trailerPath(movie.getTrailerPath())
                .imageUrl(movie.getImageUrl())
                .genres(movie.getGenres())
                .country(movie.getCountry())
                .releaseYear(movie.getReleaseYear())
                .duration(movie.getDuration())
                .language(movie.getLanguage())
                .ageLimit(movie.getAgeLimit())
                .premiere(movie.isPremiere())
                .releaseDateLocal(movie.getReleaseDateLocal())
                .movieRole(movie.getMovieRole())
                .views(views)
                .likes(likes)
                .episodesCount(episodesCount)
                .build();
    }

    public int getViewsCount(boolean isLastMonth) {
        if (isLastMonth) {
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
            return movieRepository.sumViewCountByMovieRoleAndLastMonth(MovieRole.FILM, oneMonthAgo);
        } else {
            return movieRepository.sumViewCountByMovieRole(MovieRole.FILM);
        }
    }
    public ContentCountsResponse getContentCountsLastMonth() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        // Count films and concerts
        int filmCount = movieRepository.countByMovieRoleAndReleaseDateLocalAfter(MovieRole.FILM, oneMonthAgo);
        int concertCount = movieRepository.countByMovieRoleAndReleaseDateLocalAfter(MovieRole.CONCERT, oneMonthAgo);

        // Count serials and their episodes
        List<MovieEntity> serials = movieRepository.findByMovieRoleAndReleaseDateLocalAfter(MovieRole.SERIAL, oneMonthAgo);
        List<SerialCountResponse> serialCounts = serials.stream().map(serial -> {
            int episodeCount = episodeRepository.countByMovieEntityAndCreatedDateAfter(serial, oneMonthAgo);
            return new SerialCountResponse(serial.getId(), serial.getTitle(), episodeCount);
        }).collect(Collectors.toList());

        return new ContentCountsResponse(filmCount, serialCounts, concertCount);
    }

    public TotalContentCountsResponse getTotalContentCounts() {
        int filmCount = movieRepository.countByMovieRole(MovieRole.FILM);
        int concertCount = movieRepository.countByMovieRole(MovieRole.CONCERT);
        int serialCount = movieRepository.countByMovieRole(MovieRole.SERIAL);
        int episodeCount = episodeRepository.countByMovieEntityMovieRole(MovieRole.SERIAL);

        List<SerialSummaryResponse> serialSummary = Collections.singletonList(
                new SerialSummaryResponse(serialCount, episodeCount)
        );

        return new TotalContentCountsResponse(filmCount, serialSummary, concertCount);
    }
}
