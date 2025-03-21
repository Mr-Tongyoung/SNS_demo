package com.example.junho.sns_demo.domain.post.domain;

import com.example.junho.sns_demo.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "media_file")
public class MediaFile extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @Lob // URL이 길어질 수 있으므로 LOB 타입 지정 (TEXT와 유사)
  @Column(columnDefinition = "TEXT", nullable = false)
  private String url;
}
