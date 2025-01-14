package com.example.junho.sns_demo.domain.post.repository;

import com.example.junho.sns_demo.domain.post.domain.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

}
