package doc.com.flexvibeproject.minio;


import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MinioAdapter {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    String defaultBucketName;

    @Autowired
    public MinioAdapter(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public InputStream getFileAsInputStream(GetObjectArgs objectName) {
        try {
            return minioClient.getObject(objectName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public int getFileLength(GetObjectArgs objectName) {
        try {
            InputStream file = minioClient.getObject(objectName);
            return file.readAllBytes().length;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}