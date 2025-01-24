package com.example.junho.sns_demo.domain.post.domain;

import com.example.junho.sns_demo.domain.user.domain.User;
import com.example.junho.sns_demo.domain.post.dto.PostResponseDto;
import com.example.junho.sns_demo.global.util.BaseTimeEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class Post extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String content;

  private int likes = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ElementCollection
  private Set<Long> likedUsers = new HashSet<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MediaFile> mediaFiles = new ArrayList<>();

  public PostResponseDto toResponseDto() {
    List<String> mediaUrls = this.mediaFiles.stream()
        .map(MediaFile::getUrl)
        .toList();

    return new PostResponseDto(
        this.title,
        this.content,
        this.likes,
        this.user.getUsername(),
        this.user.getId(),
        mediaUrls,
        this.getCreatedAt()
    );
  }

  public void upLike() {
    this.likes++;
  }

  public void downLike() {
    this.likes--;
  }

  public void addMediaFile(MediaFile mediaFile) {
    if (!this.mediaFiles.contains(mediaFile)) {
      this.mediaFiles.add(mediaFile);
      mediaFile.setPost(this); // 양방향 연관 설정
    }
  }

}
