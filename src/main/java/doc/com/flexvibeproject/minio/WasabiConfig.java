package doc.com.flexvibeproject.minio;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;


import java.net.URI;


@Configuration
public class WasabiConfig {


    @Value("${minio.access-key}")
    private String accessKey;


    @Value("${minio.secret-key}")
    private String secretKey;


    @Value("${minio.endpoint}")
    private String endpoint; // https://s3.wasabisys.com or your region endpoint


    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);


        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
// Wasabi doesn't require a real AWS region; use any valid Region
                .region(Region.US_EAST_1)
                .build();
    }
}