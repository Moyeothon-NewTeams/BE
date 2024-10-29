package com.example.moyeothon.Repository;

import com.example.moyeothon.Entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findAllBySenderIdOrReceiverId(Long senderId, Long receiverId);
    List<MessageEntity> findBySenderId(Long senderId);
    List<MessageEntity> findByReceiverId(Long receiverId);
}
