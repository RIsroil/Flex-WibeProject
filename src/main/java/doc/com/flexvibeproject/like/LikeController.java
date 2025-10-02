package doc.com.flexvibeproject.like;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PutMapping()
    public boolean like(@RequestParam(required = false) Long movieId,
                     @RequestParam(required = false) Long episodeId,
                     @RequestParam(required = false) Long commentId,
                     Principal principal) {
        return likeService.toggleLike(movieId,episodeId, commentId,  principal);
    }
}
