package doc.com.flexvibeproject.minio;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MinioFilePathsResponse {
    private String videoPath;
    private String trailerPath;
    private String imageUrl;
}
