package doc.com.flexvibeproject.minio;


import doc.com.flexvibeproject.exception.ResourceNotFoundException;
import doc.com.flexvibeproject.movie.MovieEntity;
import doc.com.flexvibeproject.movie.MovieRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/minio")
@RequiredArgsConstructor
public class MinioController {

    private final MovieRepository movieRepository;
    private final MinioService minioService;
    private final MinioAdapter minioAdapter;
    private final MinioConfig minioConfig;
    private final MinioClient minioClient;

    @GetMapping("/movie/{id}")
    public ResponseEntity<MinioFilePathsResponse> getMoviePaths(@PathVariable Long id) {
        MovieEntity movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + id));

        MinioFilePathsResponse response = new MinioFilePathsResponse(
                movie.getFilePath(),
                movie.getTrailerPath(),
                movie.getImageUrl()
        );

        return ResponseEntity.ok(response);
    }

//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            return minioService.uploadFile(file);
//        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
//            throw new RuntimeException("File upload failed: " + e.getMessage());
//        }
//    }

    @GetMapping(value = "/proxy/{objectName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> proxyFile(@PathVariable String objectName) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "video/mp4");
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + objectName + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            throw new RuntimeException("Failed to proxy file: " + e.getMessage());
        }
    }

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(minioService.uploadVideo(file));
    }

    @GetMapping(value = "/stream/{objectName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> streamVideo(
            @PathVariable String objectName,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        try {
            // Get object metadata to determine total size
            var stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
            long fileSize = stat.size();

            // Default range: entire file
            long rangeStart = 0;
            long rangeEnd = fileSize - 1;
            boolean isPartial = false;

            // Parse Range header (e.g., "bytes=500-999")
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                isPartial = true;
                String[] ranges = rangeHeader.replace("bytes=", "").split("-");
                rangeStart = Long.parseLong(ranges[0]);
                rangeEnd = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileSize - 1;

                if (rangeStart >= fileSize || rangeEnd >= fileSize) {
                    return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                            .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                            .build();
                }
            }

            // Calculate content length for the response
            long contentLength = rangeEnd - rangeStart + 1;

            // Build GetObjectArgs with range if partial
            GetObjectArgs.Builder getObjectArgsBuilder = GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName);

            if (isPartial) {
                getObjectArgsBuilder.offset(rangeStart).length(contentLength);
            }

            // Fetch the object (or range) from MinIO
            InputStream inputStream = minioClient.getObject(getObjectArgsBuilder.build());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
            headers.set(HttpHeaders.CONTENT_TYPE, "video/mp4"); // Adjust based on video format
            headers.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));

            if (isPartial) {
                headers.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", rangeStart, rangeEnd, fileSize));
            }

            // Return response with appropriate status
            return new ResponseEntity<>(
                    new InputStreamResource(inputStream),
                    headers,
                    isPartial ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK
            );

        } catch (MinioException e) {
            throw new RuntimeException("Failed to stream video: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
}
