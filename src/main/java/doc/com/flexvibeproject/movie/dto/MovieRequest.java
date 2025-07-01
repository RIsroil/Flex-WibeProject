package doc.com.flexvibeproject.movie.dto;

import doc.com.flexvibeproject.movie.role.CountryType;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {

    private MovieRole movieRole;
    private Set<MovieGenre> genres;
    private CountryType country;
    private LanguageType language;

    private String filePath;
    private String imageUrl;
    private String trailerPath;
    private String title;
    private String description;
    private String duration;
    private Integer releaseYear;
    private Integer ageLimit;
    private boolean premiere;
}
