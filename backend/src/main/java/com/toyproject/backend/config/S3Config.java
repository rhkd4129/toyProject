package com.toyproject.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Profile({"prod", "local-s3"})
//@Profile("prod")
public class S3Config {

    // EC2 IAM Role(AmazonS3FullAccess) 연결 후 액세스 키 불필요 → 아래 주석 코드는 IAM Role 적용 전 방식
    // IAM Role 이전: 환경변수(CLOUD_AWS_CREDENTIALS_ACCESS_KEY/SECRET_KEY)에서 키를 직접 주입해
    //               StaticCredentialsProvider로 명시적 인증했으나, SDK 내부 백그라운드 스레드가
    //               DefaultCredentialsProvider(기본 체인)도 별도로 시도하면서 IMDS 실패 에러가 발생했음
    // @Value("${cloud.aws.credentials.access-key}") private String accessKey;
    // @Value("${cloud.aws.credentials.secret-key}") private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public S3Presigner s3Presigner() {
        // credentialsProvider 미지정 → SDK가 EC2 IAM Role을 자동으로 사용 (InstanceProfileCredentialsProvider)
        return S3Presigner.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public S3Client s3Client() {
        // credentialsProvider 미지정 → SDK가 EC2 IAM Role을 자동으로 사용 (InstanceProfileCredentialsProvider)
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}
