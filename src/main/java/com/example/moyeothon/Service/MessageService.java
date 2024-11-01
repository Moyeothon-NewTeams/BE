package com.example.moyeothon.Service;

import com.example.moyeothon.DTO.MessageDTO;
import com.example.moyeothon.Entity.BucketlistEntity;
import com.example.moyeothon.Entity.MessageEntity;
import com.example.moyeothon.Entity.UserEntity;
import com.example.moyeothon.Enum.MessageStatus;
import com.example.moyeothon.Repository.BucketRepository;
import com.example.moyeothon.Repository.MessageRepository;
import com.example.moyeothon.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final BucketRepository bucketRepository;

    // 쪽지 전송
    public MessageDTO createMessage(String uid, Long bucketListId, MessageDTO messageDTO, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        UserEntity sender = userRepository.findByUid(uid);
        UserEntity receiver = userRepository.findById(messageDTO.getReceiverId()).orElseThrow();
        BucketlistEntity bucketList = bucketRepository.findById(bucketListId).orElseThrow();
        if (!bucketList.getUser().getUid().equals(receiver.getUid())) {
            throw new RuntimeException("해당 버킷리스트의 생성자에게만 쪽지를 보낼 수 있습니다.");
        }
        MessageEntity messageEntity = messageDTO.dtoToEntity(sender, receiver, bucketList);
        messageEntity.setCreateTime(LocalDateTime.now());
        messageEntity.setStatus(MessageStatus.안읽음);
        logger.info("쪽지 전송 성공!");
        return MessageDTO.entityToDTO(messageRepository.save(messageEntity));
    }

    // 쪽지 답장
    public MessageDTO replyMessage(Long messageId, String uid, Long bucketListId, MessageDTO messageDTO, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        MessageEntity originalMessage = messageRepository.findById(messageId).orElseThrow();
        UserEntity sender = userRepository.findByUid(uid);
        UserEntity receiver = originalMessage.getSender();
        BucketlistEntity bucketList = bucketRepository.findById(bucketListId).orElseThrow();
        // 원본 쪽지가 해당 버킷리스트에서 생성된 쪽지인지 확인
        if (!originalMessage.getBucketList().getId().equals(bucketListId)) {
            throw new RuntimeException("해당 버킷리스트에서 생성된 쪽지에만 답장할 수 있습니다.");
        }
        // 원본 쪽지의 발신자에게만 답장을 허용
        if (!originalMessage.getReceiver().getUid().equals(uid)) {
            throw new RuntimeException("원본 쪽지의 발신자에게만 답장을 보낼 수 있습니다.");
        }
        MessageEntity messageEntity = messageDTO.dtoToEntity(sender, receiver, bucketList);
        messageEntity.setCreateTime(LocalDateTime.now());
        messageEntity.setStatus(MessageStatus.안읽음);
        logger.info("쪽지 답장 성공!");
        return MessageDTO.entityToDTO(messageRepository.save(messageEntity));
    }


    // 쪽지 읽음 상태로 변경
    public MessageDTO readMessage(Long messageId, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        MessageEntity messageEntity = messageRepository.findById(messageId).orElseThrow();
        if (!messageEntity.getSender().getUid().equals(uid)) {
            throw new RuntimeException("해당 유저의 쪽지가 아닙니다.");
        }
        messageEntity.setStatus(MessageStatus.읽음);
        messageRepository.save(messageEntity);
        logger.info("쪽지 상태 변경 성공!");
        return MessageDTO.entityToDTO(messageEntity);
    }

    // 쪽지 삭제
    public MessageDTO deleteMessage(Long messageId, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        MessageEntity messageEntity = messageRepository.findById(messageId).orElseThrow();
        if (!messageEntity.getSender().getUid().equals(uid)) {
            throw new RuntimeException("해당 유저의 쪽지가 아닙니다.");
        }
        messageRepository.delete(messageEntity);
        logger.info("쪽지 삭제 성공!");
        return MessageDTO.entityToDTO(messageEntity);
    }

    // 해당 유저 송수신 쪽지 전체 조회
    public List<MessageDTO> getAllMessagesForUser(String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        return messageRepository.findAllBySenderUidOrReceiverUid(uid, uid)
                .stream()
                .map(MessageDTO::entityToDTO)
                .collect(Collectors.toList());
    }

    // 해당 유저 송신 쪽지 전체 조회
    public List<MessageDTO> getAllMessageForSender(String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        return messageRepository.findBySenderUid(uid)
                .stream()
                .map(MessageDTO::entityToDTO)
                .collect(Collectors.toList());
    }

    // 해당 유저 수신 쪽지 전체 조회
    public List<MessageDTO> getAllMessageForReceiver(String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        return messageRepository.findByReceiverUid(uid)
                .stream()
                .map(MessageDTO::entityToDTO)
                .collect(Collectors.toList());
    }

    // 특정 키워드가 포함된 쪽지 검색 (카테고리 키워드 검색 구현 전 연습)
    public List<MessageDTO> searchMessagesByContent(String keyword, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        return messageRepository.findByContentContainingIgnoreCase(keyword)
                .stream()
                .filter(message -> message.getSender().getUid().equals(uid) || message.getReceiver().getUid().equals(uid))
                .map(MessageDTO::entityToDTO)
                .collect(Collectors.toList());
    }
}
