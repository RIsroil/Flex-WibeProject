package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.episode.EpisodeEntity;
import doc.com.flexvibeproject.episode.EpisodeRepository;
import doc.com.flexvibeproject.exception.DuplicateResourceException;
import doc.com.flexvibeproject.exception.InvalidInputException;
import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.movie.dto.*;
import doc.com.flexvibeproject.movie.role.CountryType;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
                .filePath(request.getFilePath())
                .trailerPath(request.getTrailerPath())
                .imageUrl(request.getImageUrl())
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

    @Transactional
    public void updateMovie(Long id, MovieUpdateRequest request) {
        MovieEntity existing = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

        if (request.getTitle() != null && !Objects.equals(existing.getTitle(), request.getTitle())) {
            existing.setTitle(request.getTitle());
        }

        if (request.getDescription() != null && !Objects.equals(existing.getDescription(), request.getDescription())) {
            existing.setDescription(request.getDescription());
        }
        if (request.getFilePath() != null && !Objects.equals(existing.getFilePath(), request.getFilePath())) {
            existing.setFilePath(request.getFilePath());
        }
        if (request.getGenres() != null && !Objects.equals(existing.getGenres(), request.getGenres())) {
            existing.setGenres(request.getGenres());
        }
        if (request.getCountry() != null && !Objects.equals(existing.getCountry(), request.getCountry())) {
            existing.setCountry(request.getCountry());
        }
        if (request.getReleaseYear() != null && !Objects.equals(existing.getReleaseYear(), request.getReleaseYear())) {
            existing.setReleaseYear(request.getReleaseYear());
        }
        if (request.getDuration() != null && !Objects.equals(existing.getDuration(), request.getDuration())) {
            existing.setDuration(request.getDuration());
        }
        if (request.getLanguage() != null && !Objects.equals(existing.getLanguage(), request.getLanguage())) {
            existing.setLanguage(request.getLanguage());
        }
        if (request.getAgeLimit() != null && !Objects.equals(existing.getAgeLimit(), request.getAgeLimit())) {
            existing.setAgeLimit(request.getAgeLimit());
        }
        if (request.getImageUrl() != null && !Objects.equals(existing.getImageUrl(), request.getImageUrl())) {
            existing.setImageUrl(request.getImageUrl());
        }
        if (request.getTrailerPath() != null && !Objects.equals(existing.getTrailerPath(), request.getTrailerPath())) {
            existing.setTrailerPath(request.getTrailerPath());
        }

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
        return getMovieResponses(pageable);
    }


    public Page<MovieResponse> getMoviesByRole(MovieRole role, Pageable pageable) {
        Page<MovieEntity> page = movieRepository.findAllByMovieRole(role, pageable);

        List<MovieResponse> movies = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PageImpl<>(
                movies, pageable, page.getTotalElements()
        );
    }

    public List<String> getAvailableGenres() {
        return movieRepository.findAllUsedGenres()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public Page<MovieResponse> getMoviesByGenre(MovieGenre genre, Pageable pageable) {

        Page<MovieEntity> page = movieRepository.findByGenresContaining(genre, pageable);

        List<MovieResponse> moviesByGenre = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PageImpl<>(
                moviesByGenre,
                pageable,
                page.getTotalElements()
        );
    }

    public List<Integer> getAllAvailableYears() {
        return movieRepository.findAllDistinctYears();
    }

    public Page<MovieResponse> getMoviesByYear(Integer year, Pageable pageable) {
        Page<MovieEntity> page = movieRepository.findAllByReleaseYear(year, pageable);

        List<MovieResponse> moviesByYear = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PageImpl<>(
                moviesByYear,
                pageable,
                page.getTotalElements()
        );
    }

    public List<String> getAvailableLanguages() {
        return movieRepository.findAllUsedLanguages()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public Page<MovieResponse> getMoviesByLanguage(LanguageType language, Pageable pageable) {
        Page<MovieEntity> page = movieRepository.findByLanguage(language, pageable);

        List<MovieResponse> moviesByLanguage = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return new PageImpl<>(
                moviesByLanguage,
                pageable,
                page.getTotalElements()
        );
    }

    public Page<MovieResponse> smartSearch(String rawText, Pageable pageable) {
        if (rawText == null || rawText.isBlank()) {
            return getMovieResponses(pageable);
        }

        String text = rawText.toLowerCase().trim();
        String[] tokens = text.split("\\s+");

        Set<MovieGenre> foundGenres = new HashSet<>();
        Set<LanguageType> foundLanguages = new HashSet<>();
        Set<CountryType> foundCountries = new HashSet<>();
        Set<MovieRole> foundTypes = new HashSet<>();
        Integer foundYear = null;
        Boolean isPremiere = null;
        List<String> titleWords = new ArrayList<>();

        for (String word : tokens) {
            boolean isSpecialField = false;

            if (fuzzyMatchEnum(MovieGenre.class, word).isPresent()) {
                foundGenres.add(fuzzyMatchEnum(MovieGenre.class, word).get());
                isSpecialField = true;
            }

            if (fuzzyMatchEnum(LanguageType.class, word).isPresent()) {
                foundLanguages.add(fuzzyMatchEnum(LanguageType.class, word).get());
                isSpecialField = true;
            }

            if (fuzzyMatchEnum(CountryType.class, word).isPresent()) {
                foundCountries.add(fuzzyMatchEnum(CountryType.class, word).get());
                isSpecialField = true;
            }

            if (fuzzyMatchEnum(MovieRole.class, word).isPresent()) {
                foundTypes.add(fuzzyMatchEnum(MovieRole.class, word).get());
                isSpecialField = true;
            }

            if (word.contains("premyera") || word.contains("premera") || word.contains("primyera") ||
                    word.contains("premiere") || word.contains("yangi")) {
                isPremiere = true;
                isSpecialField = true;
            }

            if (word.matches("\\d{4}")) {
                int year = Integer.parseInt(word);
                if (year >= 1900 && year <= 2030) {
                    foundYear = year;
                    isSpecialField = true;
                }
            } else if (word.matches("\\d{4}-?yil")) {
                foundYear = Integer.parseInt(word.replace("-yil", "").replace("yil", ""));
                isSpecialField = true;
            }

            if (!isSpecialField) {
                titleWords.add(word);
            }
        }

        Specification<MovieEntity> spec = createUniversalSearchSpec(
                titleWords, foundGenres, foundLanguages, foundCountries,
                foundTypes, foundYear, isPremiere
        );

        Page<MovieEntity> resultsPage = movieRepository.findAll(spec, pageable);

        if (resultsPage.getContent().isEmpty() && !titleWords.isEmpty()) {
            return performFuzzyTitleSearch(String.join(" ", titleWords), pageable); // Page qaytaradi
        }

        List<MovieEntity> unsortedContent = resultsPage.getContent();
        List<MovieEntity> sortedContent = unsortedContent.stream()
                .sorted((a, b) -> calculateRelevanceScore(mapToResponse(b), rawText) - calculateRelevanceScore(mapToResponse(a), rawText))
                .toList();

        List<MovieResponse> sortedResponses = sortedContent.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(sortedResponses, pageable, resultsPage.getTotalElements());
    }

    @NotNull
    private Page<MovieResponse> getMovieResponses(Pageable pageable) {
        Page<MovieEntity> page = movieRepository.findAll(pageable);
        List<MovieResponse> sortedList = page.getContent().stream()
                .map(this::mapToResponse)
                .sorted(Comparator.comparing(MovieResponse::getReleaseDateLocal).reversed())
                .toList();
        return new PageImpl<>(sortedList, pageable, page.getTotalElements());
    }

    private Specification<MovieEntity> createUniversalSearchSpec(
            List<String> titleWords,
            Set<MovieGenre> foundGenres,
            Set<LanguageType> foundLanguages,
            Set<CountryType> foundCountries,
            Set<MovieRole> foundTypes,
            Integer foundYear,
            Boolean isPremiere) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!titleWords.isEmpty()) {
                List<Predicate> titlePredicates = new ArrayList<>();

                for (String word : titleWords) {
                    if (word.length() >= 2) {
                        titlePredicates.add(
                                cb.like(cb.lower(root.get("title")), "%" + word + "%")
                        );
                    }
                }

                if (!titlePredicates.isEmpty()) {
                    predicates.add(cb.or(titlePredicates.toArray(new Predicate[0])));
                }
            }

            if (!foundGenres.isEmpty()) {
                List<Predicate> genrePredicates = new ArrayList<>();
                for (MovieGenre genre : foundGenres) {
                    genrePredicates.add(cb.isMember(genre, root.get("genres")));
                }
                predicates.add(cb.or(genrePredicates.toArray(new Predicate[0])));
            }

            if (!foundLanguages.isEmpty()) {
                predicates.add(root.get("language").in(foundLanguages));
            }

            if (!foundCountries.isEmpty()) {
                predicates.add(root.get("country").in(foundCountries));
            }

            if (!foundTypes.isEmpty()) {
                predicates.add(root.get("movieRole").in(foundTypes));
            }

            if (foundYear != null) {
                predicates.add(cb.equal(root.get("releaseYear"), foundYear));
            }

            if (isPremiere != null) {
                predicates.add(cb.equal(root.get("premiere"), isPremiere));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    private Page<MovieResponse> performFuzzyTitleSearch(String searchTitle, Pageable pageable) {
        List<MovieEntity> allMovies = movieRepository.findAll();

        List<MovieEntity> fuzzyResults = allMovies.stream()
                .filter(movie -> isSimilar(movie.getTitle(), searchTitle))
                .sorted((a, b) -> calculateTitleSimilarity(b.getTitle(), searchTitle) -
                        calculateTitleSimilarity(a.getTitle(), searchTitle))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), fuzzyResults.size());

        List<MovieEntity> pagedFuzzy;
        if (start >= fuzzyResults.size()) {
            pagedFuzzy = Collections.emptyList();
        } else {
            pagedFuzzy = fuzzyResults.subList(start, end);
        }

        List<MovieResponse> pagedResponses = pagedFuzzy.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        long totalFuzzy = fuzzyResults.size();
        return new PageImpl<>(pagedResponses, pageable, totalFuzzy);
    }

    private int calculateRelevanceScore(MovieResponse movie, String searchText) {
        int score = 0;
        String searchLower = searchText.toLowerCase();
        String titleLower = movie.getTitle() != null ? movie.getTitle().toLowerCase() : "";

        if (titleLower.contains(searchLower)) {
            score += 100;
        }

        String[] searchWords = searchLower.split("\\s+");
        for (String word : searchWords) {
            if (word.length() >= 2 && titleLower.contains(word)) {
                score += 10;
            }
        }

        if (movie.getReleaseYear() != null && movie.getReleaseYear() >= 2020) {
            score += 5;
        }

        return score;
    }

    private int calculateTitleSimilarity(String title, String search) {
        if (title == null || search == null) return 0;

        LevenshteinDistance distance = new LevenshteinDistance();
        int maxLength = Math.max(title.length(), search.length());
        int dist = distance.apply(title.toLowerCase(), search.toLowerCase());

        return maxLength - dist;
    }

    public <E extends Enum<E>> Optional<E> fuzzyMatchEnum(Class<E> enumClass, String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }

        LevenshteinDistance distance = new LevenshteinDistance();
        double maxDistance = 0.9;
        String inputLower = input.toLowerCase();

        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().toLowerCase().equals(inputLower)) {
                return Optional.of(enumValue);
            }
        }

        for (E enumValue : enumClass.getEnumConstants()) {
            String enumName = enumValue.name().toLowerCase();
            if (enumName.contains(inputLower) || inputLower.contains(enumName)) {
                return Optional.of(enumValue);
            }
        }

        for (E enumValue : enumClass.getEnumConstants()) {
            String enumString = enumValue.toString().toLowerCase();
            if (enumString.equals(inputLower) ||
                    enumString.contains(inputLower) ||
                    inputLower.contains(enumString)) {
                return Optional.of(enumValue);
            }
        }

        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> distance.apply(e.name().toLowerCase(), inputLower) <= maxDistance)
                .min(Comparator.comparingInt(e ->
                        distance.apply(e.name().toLowerCase(), inputLower)));
    }

    private boolean isSimilar(String a, String b) {
        if (a == null || b == null) {
            return false;
        }

        String aLower = a.toLowerCase();
        String bLower = b.toLowerCase();

        if (aLower.equals(bLower)) {
            return true;
        }

        if (aLower.contains(bLower) || bLower.contains(aLower)) {
            return true;
        }

        String[] wordsA = aLower.split("\\s+");
        String[] wordsB = bLower.split("\\s+");

        int matchingWords = 0;
        for (String wordA : wordsA) {
            for (String wordB : wordsB) {
                if (wordA.equals(wordB) ||
                        (wordA.length() > 3 && wordB.length() > 3 &&
                                (wordA.contains(wordB) || wordB.contains(wordA)))) {
                    matchingWords++;
                    break;
                }
            }
        }

        int totalWords = Math.min(wordsA.length, wordsB.length);
        if (totalWords > 0 && (double) matchingWords / totalWords >= 0.5) {
            return true;
        }

        LevenshteinDistance distance = new LevenshteinDistance();
        int maxAllowed = Math.max(2, Math.min(aLower.length(), bLower.length()) / 3);

        return distance.apply(aLower, bLower) <= maxAllowed;
    }

    @Transactional
    public void incrementViews(Long movieId) {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        if (movie.getMovieRole() == MovieRole.SERIAL) {
            throw new ResourceNotFoundException("You can not post view for Serial: " + movieId);
        } else {
            movie.setViewCount(movie.getViewCount() + 1);
        }
        movieRepository.save(movie);
    }

    private MovieResponse mapToResponse(MovieEntity movie) {
        Integer episodesCount = 0;
        int views = 0;
        int likes = 0;

        if (movie.getMovieRole() == MovieRole.SERIAL) {
            List<EpisodeEntity> episodes = episodeRepository.findAllByMovieEntity(movie, Pageable.unpaged()).getContent();

            episodesCount = episodes.size(); // Count episodes directly
            views = episodes.stream().mapToInt(EpisodeEntity::getViewCount).sum();
            likes = episodes.stream().mapToInt(EpisodeEntity::getLikeCount).sum();
        } else {
            episodesCount = movie.getEpisodeCount();
            views = movie.getViewCount();
            likes = movie.getLikeCount();
        }

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

        int filmCount = movieRepository.countByMovieRoleAndReleaseDateLocalAfter(MovieRole.FILM, oneMonthAgo);
        int concertCount = movieRepository.countByMovieRoleAndReleaseDateLocalAfter(MovieRole.CONCERT, oneMonthAgo);

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
