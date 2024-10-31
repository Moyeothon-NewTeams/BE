package com.example.moyeothon.Repository;

import com.example.moyeothon.Entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findAllBySenderUidOrReceiverUid(String senderId, String receiverId);
    List<MessageEntity> findBySenderUid(String senderId);
    List<MessageEntity> findByReceiverUid(String receiverId);
    List<MessageEntity> findByContentContainingIgnoreCase(String keyword);
}
