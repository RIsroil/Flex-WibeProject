package doc.com.flexvibeproject.save;

import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.user.UserEntity;
import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveMovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="movie_id", referencedColumnName = "id", nullable = false)
    private MovieEntity movie;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    @Column(name = "saved_at")
    private java.time.LocalDateTime savedAt;

    @PrePersist
    public void prePersist() {
        this.savedAt = java.time.LocalDateTime.now();
    }
}
