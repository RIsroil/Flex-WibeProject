package doc.com.flexvibeproject.movie.dto;

import doc.com.flexvibeproject.movie.role.CountryType;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
public class MovieResponse {
    private MovieRole movieRole;

    private Long id;
    private String title;
    private String description;
    private String filePath;
    private Set<MovieGenre> genres;
    private CountryType country;
    private LanguageType language;
    private String duration;
    private Integer releaseYear;
    private Integer ageLimit;
    private String imageUrl;
    private String trailerPath;
    private boolean premiere;
    private LocalDateTime releaseDateLocal;
    private int views;
    private int likes;

}
