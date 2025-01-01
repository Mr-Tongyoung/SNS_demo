package com.example.junho.sns_demo.domain.user.domain;

import com.example.junho.sns_demo.domain.post.domain.Post;
import com.example.junho.sns_demo.domain.user.dto.UserResponseDto;
import com.example.junho.sns_demo.global.util.BaseTimeEntity;
import com.example.junho.sns_demo.global.util.Gender;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

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

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String loginId;

  @Column(nullable = false)
  String password;

  @Column(nullable = false)
  LocalDate birthday;

  @Column(nullable = false)
  String nickname;

  @Column(nullable = false)
  Gender gender;

  @Column(nullable = false)
  String phone;

  @Column(nullable = false)
  String email;

  @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Follow> following = new ArrayList<>();

  @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Follow> followers = new ArrayList<>();


  public UserResponseDto toResponseDto() {
    return new UserResponseDto(
        this.id,
        this.name,
        this.loginId,
        this.birthday,
        this.nickname,
        this.gender,
        this.phone,
        this.email
    );
  }
}
