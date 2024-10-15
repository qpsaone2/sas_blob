// 패키지 선언
package com.ollacare.sas_api;

// Spring Boot 관련 클래스 임포트
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Azure Blob Storage 관련 클래스 임포트
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;

import java.time.OffsetDateTime;

// Spring Boot 애플리케이션 설정 및 REST 컨트롤러 선언
@SpringBootApplication
@RestController
public class SasApiApplication {

    // // Azure Blob Storage 계정 정보
    private static final String ACCOUNT_NAME = "YOUR_STORAGE_ACCOUNT_NAME";
    private static final String ACCOUNT_KEY = "YOUR_STORAGE_ACCOUNT_ACCESS_KEY";
    private static final String CONTAINER_NAME = "YOUT_BLOB_CONTAINER_NAME";


    // 애플리케이션 메인 메서드
    public static void main(String[] args) {
        SpringApplication.run(SasApiApplication.class, args);
    }

    // SAS 토큰 생성 엔드포인트
    @GetMapping("/generateSasToken")
    public String generateSasToken() {
        // Azure Blob Storage 연결 문자열 생성
        String connectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net", ACCOUNT_NAME, ACCOUNT_KEY);
        
        // BlobServiceClient 생성
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();

        // SAS 토큰 만료 시간 설정
        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);

        // SAS 토큰 생성에 필요한 권한 설정
        BlobSasPermission permission = new BlobSasPermission().setCreatePermission(true).setWritePermission(true).setReadPermission(true);

        // SAS 토큰 생성에 필요한 값 설정
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(expiryTime, permission)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        // BlobContainerClient 생성 및 SAS 토큰 생성
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        String sasToken = containerClient.generateSas(values);

        // SAS 토큰이 포함된 URL 생성
        String blobEndpoint = String.format("https://%s.blob.core.windows.net", ACCOUNT_NAME);
        String sasUrl = String.format("%s/%s?%s", blobEndpoint, CONTAINER_NAME, sasToken);

        return sasUrl;
    }
}