package doc.com.flexvibeproject.minio;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;


import java.time.Duration;


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
        if (objectKey == null || objectKey.isBlank()) return null;


        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();


// expiry: 1 hour. Adjust if needed (Wasabi supports up to 7 days in many setups).
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(24))
                .getObjectRequest(getObjectRequest)
                .build();


        PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
        return presigned.url().toString();
    }
}