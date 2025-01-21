package com.example.junho.sns_demo.global.util.aws.s3;

public record PresignedUrlRequest (
    String fileName,
    String contentType
){
}
