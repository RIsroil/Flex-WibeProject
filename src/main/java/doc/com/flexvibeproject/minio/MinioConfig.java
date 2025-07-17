package doc.com.flexvibeproject.minio;

import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    private String endpoint;
    private String publicEndpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;

    @PostConstruct
    public void init() {
        log.info("MinIO Config - Endpoint: {}, Public Endpoint: {}", endpoint, publicEndpoint);
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint) // Use internal endpoint (http://minio-flex:9000)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String getPublicEndpoint() {
        return publicEndpoint; // Returns https://be-dev.uz
    }
}