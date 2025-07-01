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
    public void like(@RequestParam(required = false) Long movieId,
                     @RequestParam(required = false) Long commentId,
                     Principal principal) {
        likeService.toggleLike(movieId, commentId, principal);
    }
}
