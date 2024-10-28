package com.example.moyeothon.Repository;

import com.example.moyeothon.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUid(String uid);
    boolean existsByUid(String uid);
    boolean existsByNickname(String nickname);
}
