package doc.com.flexvibeproject.movie.dto;


import doc.com.flexvibeproject.movie.role.CountryType;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import jdk.jfr.ContentType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class MovieUpdateRequest {

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
}
