package com.example.moyeothon.Service;

import com.example.moyeothon.DTO.MessageDTO;
import com.example.moyeothon.Entity.MessageEntity;
import com.example.moyeothon.Entity.UserEntity;
import com.example.moyeothon.Enum.MessageStatus;
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

    // 쪽지 전송
    public MessageDTO createMessage(String uid, MessageDTO messageDTO, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        UserEntity sender = userRepository.findByUid(uid);
        UserEntity receiver = userRepository.findById(messageDTO.getReceiverId()).orElseThrow();
        MessageEntity messageEntity = messageDTO.dtoToEntity(sender, receiver, MessageStatus.안읽음);
        messageEntity.setCreateTime(LocalDateTime.now());
        logger.info("쪽지 전송 성공!");
        logger.info("전송된 쪽지 내용 : " + messageDTO.getContent() + ", 송신인 ID : " + sender.getId() + ", 수신인 ID : " + receiver.getId());
        return MessageDTO.entityToDTO(messageRepository.save(messageEntity));
    }

    // 쪽지 답장
    public MessageDTO replyMessage(Long messageId, String uid, MessageDTO messageDTO, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        MessageEntity originalMessage = messageRepository.findById(messageId).orElseThrow();
        UserEntity sender = userRepository.findByUid(uid);
        UserEntity receiver = originalMessage.getSender();
        MessageEntity messageEntity = messageDTO.dtoToEntity(sender, receiver, MessageStatus.안읽음);
        messageEntity.setCreateTime(LocalDateTime.now());
        logger.info("쪽지 전송 성공!");
        logger.info("전송된 쪽지 내용 : " + messageDTO.getContent() + ", 송신인 ID : " + sender.getId() + ", 수신인 ID : " + receiver.getId());
        return MessageDTO.entityToDTO(messageRepository.save(messageEntity));
    }

    // 쪽지 읽음 상태로 변경
    public MessageDTO readMessage(Long messageId, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        MessageEntity messageEntity = messageRepository.findById(messageId).orElseThrow();
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
        messageEntity.setStatus(MessageStatus.삭제됨);
        messageRepository.save(messageEntity);
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
