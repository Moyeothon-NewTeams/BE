package com.example.moyeothon.Repository;


import com.example.moyeothon.Entity.BucketlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BucketRepository extends JpaRepository <BucketlistEntity, Long> {
    List<BucketlistEntity> findByUser_Uid(String uid);
    List<BucketlistEntity> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
    void deleteByUserUid(String uid);
}
