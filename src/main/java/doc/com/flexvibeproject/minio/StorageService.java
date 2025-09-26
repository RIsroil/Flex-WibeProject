package doc.com.flexvibeproject.minio;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final S3Presigner presigner;

    @Value("${minio.bucket-name}")
    private String bucket;

    /**
     * Generate a presigned GET URL for given object key.
     * Returns null if objectKey is null/blank.
     */
    public String generatePresignedUrl(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

            // Use shorter expiry for testing - 1 hour
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
            String url = presigned.url().toString();

            log.debug("Generated presigned URL for object '{}': {}", objectKey, url);
            return url;

        } catch (Exception e) {
            log.error("Error generating presigned URL for object '{}': {}", objectKey, e.getMessage(), e);
            // Fallback to direct URL if presigned URL fails
            return String.format("https://s3.us-east-1.wasabisys.com/%s/%s", bucket, objectKey);
        }
    }
}