//package doc.com.flexvibeproject.minio;
//
//import io.minio.GetObjectArgs;
//import io.minio.MinioClient;
//import io.minio.StatObjectArgs;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.InputStream;
//
//@Service
//public class MinioAdapter {
//
//    private final MinioClient minioClient;
//
//    @Value("${minio.bucket.name}")
//    String defaultBucketName;
//
//    @Autowired
//    public MinioAdapter(MinioClient minioClient) {
//        this.minioClient = minioClient;
//    }
//
//    public InputStream getFileAsInputStream(GetObjectArgs objectArgs) {
//        try {
//            return minioClient.getObject(objectArgs);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to get file: " + e.getMessage());
//        }
//    }
//
//    public long getFileLength(GetObjectArgs objectArgs) {
//        try {
//            return minioClient.statObject(
//                    StatObjectArgs.builder()
//                            .bucket(objectArgs.bucket())
//                            .object(objectArgs.object())
//                            .build()
//            ).size();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to get file length: " + e.getMessage());
//        }
//    }
//}
