package doc.com.flexvibeproject.episode;

import doc.com.flexvibeproject.episode.dto.EpisodeDto;
import doc.com.flexvibeproject.minio.MinioService;
import doc.com.flexvibeproject.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/episode")
@RequiredArgsConstructor
public class EpisodeController {
    private final EpisodeService episodeService;
    private final MinioService minioService;

    @PostMapping
    public void addEpisode(Long id, @RequestBody EpisodeDto request) {
        episodeService.createEpisode(id, request);
    }

    @GetMapping("/{id}")
    public EpisodeDto getEpisodeById(@PathVariable Long id) {
        //        try {
//            if (episode.getFilePath() != null) {
//                String fileName = minioService.extractFileName(episode.getFilePath());
//                episode.setFilePath(minioService.getPresignedUrl(fileName));
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage());
//        }
        return episodeService.getEpisodeById(id);
    }

    @PatchMapping("/{id}")
    public void updateEpisode(@PathVariable Long id, @RequestBody EpisodeDto request) {
        episodeService.updateEpisode(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteEpisodeById(@PathVariable Long id) {
        episodeService.deleteEpisodeById(id);
    }

}
