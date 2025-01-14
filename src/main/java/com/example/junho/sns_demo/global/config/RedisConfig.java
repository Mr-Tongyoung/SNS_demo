package com.example.junho.sns_demo.global.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory("localhost", 6379);
  }

  @Bean
  public RedisCacheConfiguration redisCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10)) // 캐시 TTL 설정
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .disableCachingNullValues();
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory());

    // Key Serializer
    template.setKeySerializer(new StringRedisSerializer());

    // Value Serializer
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
    template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
    return template;
  }

  /**
   * 리스트에 접근하여 다양한 연산을 수행합니다.
   *
   * @return ListOperations<String, Object>
   */
  public ListOperations<String, Object> getListOperations() {
    return this.redisTemplate().opsForList();
  }

  /**
   * 단일 데이터에 접근하여 다양한 연산을 수행합니다.
   *
   * @return ValueOperations<String, Object>
   */
  public ValueOperations<String, Object> getValueOperations() {
    return this.redisTemplate().opsForValue();
  }

  /**
   * Redis 작업중 등록, 수정, 삭제에 대해서 처리 및 예외처리를 수행합니다.
   *
   * @param operation
   * @return
   */
  public int executeOperation(Runnable operation) {
    try {
      operation.run();
      return 1;
    } catch (Exception e) {
      System.out.println("Redis 작업 오류 발생 :: " + e.getMessage());
      return 0;
    }
  }
}

