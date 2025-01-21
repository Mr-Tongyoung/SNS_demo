package com.example.junho.sns_demo.domain.elasticSearch;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
@Data
@Document(indexName = "item")
public class PostDocument {
  @Id
  private Long id;

  @Field(type = FieldType.Long)
  private Long userId;

  @Field(type = FieldType.Text, analyzer = "standard")
  private String content;

  @Field(type = FieldType.Keyword)
  private String imageUrl;

  @Field(type = FieldType.Integer)
  private int likeCount;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX||epoch_millis")
  private OffsetDateTime createdAt;

  @Field(type = FieldType.Date, format = DateFormat.date_time, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX||epoch_millis")
  private OffsetDateTime updatedAt;
}
