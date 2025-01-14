package com.example.junho.sns_demo.global.util.aws;
import lombok.Getter;
import lombok.Setter;

public record PresignedUrlRequest (
    String fileName,
    String contentType
){
}
