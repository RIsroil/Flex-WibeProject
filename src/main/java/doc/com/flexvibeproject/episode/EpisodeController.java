package doc.com.flexvibeproject.episode;

import doc.com.flexvibeproject.episode.dto.EpisodeDto;
import doc.com.flexvibeproject.minio.MinioService;
import doc.com.flexvibeproject.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/episode")
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
        EpisodeDto episode = episodeService.getEpisodeById(id);
        try {
            if (episode.getFilePath() != null) {
                String fileName = minioService.extractFileName(episode.getFilePath());
                episode.setFilePath(minioService.getPresignedUrl(fileName));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage());
        }
        return episode;
    }

    @PatchMapping("/{id}")
    public void updateEpisode(@PathVariable Long id, @RequestBody EpisodeDto request) {
        episodeService.updateEpisode(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteEpisodeById(@PathVariable Long id) {
        episodeService.deleteEpisodeById(id);
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
