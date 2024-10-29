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
        UserEntity receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("수신인이 존재하지 않습니다."));

        MessageEntity messageEntity = messageDTO.dtoToEntity(sender, receiver, MessageStatus.않읽음);
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

        MessageEntity originalMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메세지를 찾을 수 없습니다."));

        UserEntity sender = userRepository.findByUid(uid);
        UserEntity receiver = originalMessage.getSender();

        MessageEntity replyMessage = messageDTO.dtoToEntity(sender, receiver, MessageStatus.않읽음);
        replyMessage.setCreateTime(LocalDateTime.now());
        logger.info("쪽지 전송 성공!");
        logger.info("전송된 쪽지 내용 : " + messageDTO.getContent() + ", 송신인 ID : " + sender.getId() + ", 수신인 ID : " + receiver.getId());
        return MessageDTO.entityToDTO(messageRepository.save(replyMessage));
    }

    // 쪽지 읽음 상태로 변경
    public MessageDTO readMessage(Long messageId, String uid, UserDetails userDetails) {
        MessageEntity messageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메세지를 찾을 수 없습니다."));

        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        messageEntity.setStatus(MessageStatus.읽음);
        messageRepository.save(messageEntity);
        logger.info("쪽지 상태 변경 성공!");
        return MessageDTO.entityToDTO(messageEntity);
    }

    // 쪽지 삭제
    public MessageDTO deleteMessage(Long messageId, String uid, UserDetails userDetails) {
        MessageEntity messageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메세지를 찾을 수 없습니다."));

        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        messageEntity.setStatus(MessageStatus.삭제됨);
        messageRepository.save(messageEntity);
        logger.info("쪽지 삭제 성공!");
        return MessageDTO.entityToDTO(messageEntity);
    }

    // 해당 유저 쪽지 전체 조회
    public List<MessageDTO> getAllMessagesForUser(Long userId, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        return messageRepository.findAllBySenderIdOrReceiverId(userId, userId)
                .stream()
                .map(MessageDTO::entityToDTO)
                .collect(Collectors.toList());
    }

    // 해당 유저 송신 쪽지 전체 조회
    public List<MessageDTO> getAllMessageForSender(Long userId, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        return messageRepository.findBySenderId(userId)
                .stream()
                .map(MessageDTO::entityToDTO)
                .collect(Collectors.toList());
    }

    // 해당 유저 수신 쪽지 전체 조회
    public List<MessageDTO> getAllMessageForReceiver(Long userId, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        return messageRepository.findByReceiverId(userId)
                .stream()
                .map(MessageDTO::entityToDTO)
                .collect(Collectors.toList());
    }
}
