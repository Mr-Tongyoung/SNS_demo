//package com.example.junho.sns_demo.global.util.aws.sqs;
//
//import io.awspring.cloud.sqs.operations.SendResult;
//import io.awspring.cloud.sqs.operations.SqsTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.sqs.SqsAsyncClient;
//
//@Service
//public class SqsMessageSender {
//
//  private final SqsTemplate queueMessagingTemplate;
//
//  @Value("${cloud.aws.sqs.queue-name}")
//  private String QUEUE_NAME;
//
//  public SqsMessageSender(SqsAsyncClient sqsAsyncClient) {
//    this.queueMessagingTemplate = SqsTemplate.newTemplate(sqsAsyncClient);
//  }
//
//  public SendResult<String> sendMessage(String message) {
//    System.out.println("Sender: " + message);
//
//    return queueMessagingTemplate.send(to -> to
//        .queue(QUEUE_NAME)
//        .payload(message));
//  }
//}
//
