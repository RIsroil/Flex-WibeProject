package doc.com.flexvibeproject.movie;

import doc.com.flexvibeproject.movie.role.CountryType;
import doc.com.flexvibeproject.movie.role.LanguageType;
import doc.com.flexvibeproject.movie.role.MovieGenre;
import doc.com.flexvibeproject.movie.role.MovieRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "movies")
public class MovieEntity {
    private static final String GENERATOR_NAME = "movies_gen";
    private static final String SEQUENCE_NAME = "movies_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;
    private String description;
    private String filePath;
    private String duration;
    private Integer releaseYear;
    private Integer ageLimit;
    private String imageUrl;
    private String trailerPath;
    private boolean premiere = false;
    private LocalDateTime releaseDateLocal;

    @Enumerated(EnumType.STRING)
    private MovieRole movieRole;

    @Enumerated(EnumType.STRING)
    private CountryType country;

    @Enumerated(EnumType.STRING)
    private LanguageType language;

    @ElementCollection(targetClass = MovieGenre.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    private Set<MovieGenre> genres = new HashSet<>();

    private int viewCount = 0;
    private int likeCount = 0;

    private int episodeCount= 0;

    public void incrementViewCount(){
        this.viewCount++;
    }
    public void incrementLikeCount(){
        this.likeCount++;
    }
    public void decrementLikeCount(){
        this.likeCount--;
    }

}
