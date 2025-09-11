package doc.com.flexvibeproject.episode;

import doc.com.flexvibeproject.episode.dto.EpisodeRequest;
import doc.com.flexvibeproject.episode.dto.EpisodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/episode")
@RequiredArgsConstructor
public class EpisodeController {
    private final EpisodeService episodeService;

    @PostMapping
    public void addEpisode(Long id, @RequestBody EpisodeRequest request) {
        episodeService.createEpisode(id, request);
    }

    @GetMapping("/by-serial/{serialId}")
    public Page<EpisodeResponse> getEpisodesBySerial(@PathVariable Long serialId, Pageable pageable) {
        return episodeService.getEpisodesBySerialId(serialId, pageable);
    }

    @GetMapping("/{id}")
    public EpisodeResponse getEpisodeById(@PathVariable Long id) {
        return episodeService.getEpisodeById(id);
    }

    @PatchMapping("/{id}")
    public void updateEpisode(@PathVariable Long id, @RequestBody EpisodeRequest request) {
        episodeService.updateEpisode(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteEpisodeById(@PathVariable Long id) {
        episodeService.deleteEpisodeById(id);
    }

    @PutMapping("/view/{id}")
    public void viewEpisode(@PathVariable Long id) {
        episodeService.incrementViews(id);
    }
}
