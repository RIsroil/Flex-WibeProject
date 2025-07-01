package doc.com.flexvibeproject.minio;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient; // For internal operations
    private final MinioClient publicMinioClient; // For presigned URLs
    private final MinioConfig minioConfig;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String getPermanentUrl(String fileName) {
        String endpoint = minioConfig.getPublicEndpoint();
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + bucketName + "/" + fileName;
    }

//    public String getPresignedUrl(String fileName) throws Exception {
//        try {
//            minioClient.statObject(
//                    StatObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object(fileName)
//                            .build()
//            );
//        } catch (MinioException e) {
//            log.error("Object not found: {}", fileName, e);
//            throw new IllegalArgumentException("Object not found: " + fileName);
//        }
//
//        String publicUrl = publicMinioClient.getPresignedObjectUrl(
//                GetPresignedObjectUrlArgs.builder()
//                        .method(Method.GET)
//                        .bucket(bucketName)
//                        .object(fileName)
//                        .expiry(60 * 60) // 1 hour
//                        .build()
//        );
//
//        log.info("Generated presigned URL: {}", publicUrl);
//        return publicUrl;
//    }
//
//    public String extractFileName(String fullUrl) {
//        if (fullUrl == null || fullUrl.isBlank()) {
//            throw new IllegalArgumentException("File URL cannot be null or empty");
//        }
//
//        String baseUrl = getPermanentUrl("");
//        if (!fullUrl.startsWith(baseUrl)) {
//            throw new IllegalArgumentException("Invalid file URL: " + fullUrl);
//        }
//
//        return fullUrl.substring(baseUrl.length());
//    }

    public ResponseEntity<?> uploadVideo(MultipartFile file) {
        try {
            String fileUrl = uploadFile(file);
            return ResponseEntity.ok().body(fileUrl);
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during file upload: " + e.getMessage());
        }
    }

    public String uploadFile(MultipartFile file) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID().toString() + extension;

        try (var inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
        return getPermanentUrl(fileName);
    }
}