package com.example.junho.sns_demo.domain.user.domain;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.dto.UserResponseDto;
import com.example.junho.sns_demo.global.util.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  private String password;

  private String email;

  private String role;

  @Builder.Default
  private int followerCount = 0;

  @Builder.Default
  private int followingCount = 0;

  private boolean isCeleb;

  @Builder.Default
  @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Follow> followings = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Follow> followers = new ArrayList<>();

  // 추가: User가 작성한 Post 리스트
  @Builder.Default
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts = new ArrayList<>();

  public void increaseFollowerCount() {
    this.followerCount++;
    updateCelebrityStatus();
  }

  public void decreaseFollowerCount() {
    this.followerCount--;
    updateCelebrityStatus();
  }

  public void increaseFollowingCount() {
    this.followingCount++;
    updateCelebrityStatus();
  }

  public void decreaseFollowingCount() {
    this.followingCount--;
    updateCelebrityStatus();
  }

  private void updateCelebrityStatus() {
    this.isCeleb = this.followerCount >= 100000;
  }


  public UserResponseDto toResponseDto() {
    return new UserResponseDto(
        this.id,
        this.username,
        this.email,
        this.role,
        this.followerCount,
        this.followingCount,
        this.isCeleb
    );
  }
}
