package com.example.junho.sns_demo.domain.elasticSearch;

import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {

  @Value("${spring.data.elasticsearch.rest.username}")
  private String username;

  @Value("${spring.data.elasticsearch.rest.password}")
  private String password;

  @Value("${spring.data.elasticsearch.rest.ssl.trust-store}")
  private String trustStorePath;

  @Value("${spring.data.elasticsearch.rest.ssl.trust-store-password}")
  private String trustStorePassword;

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo("localhost:9200")
        .usingSsl(createSSLContext())  // HTTPS 사용 설정
        .withBasicAuth(username, password)  // 인증 정보 추가
        .build();
  }

  private SSLContext createSSLContext() {
    try {
      String trustStorePath = this.trustStorePath; // truststore 경로
      String trustStorePassword = this.trustStorePassword; // truststore 비밀번호

      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      try (FileInputStream fis = new FileInputStream(trustStorePath)) {
        trustStore.load(fis, trustStorePassword.toCharArray());
      }

      return SSLContextBuilder.create()
          .loadTrustMaterial(trustStore, null)
          .build();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create SSLContext", e);
    }
  }
}
