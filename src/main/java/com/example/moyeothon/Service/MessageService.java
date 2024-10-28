package com.example.moyeothon.Service;

import com.example.moyeothon.DTO.MessageDTO;
import com.example.moyeothon.Entity.MessageEntity;
import com.example.moyeothon.Entity.UserEntity;
import com.example.moyeothon.Enum.MessageStatus;
import com.example.moyeothon.Repository.MessageRepository;
import com.example.moyeothon.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // 쪽지 전송
    public MessageDTO createMessage(MessageDTO messageDTO, UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        UserEntity sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("송신인이 존재하지 않습니다."));
        UserEntity receiver = userRepository.findById(messageDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("수신인이 존재하지 않습니다."));

        MessageEntity messageEntity = messageDTO.dtoToEntity(sender, receiver, MessageStatus.않읽음);
        messageEntity.setCreateTime(LocalDateTime.now());
        return MessageDTO.entityToDTO(messageRepository.save(messageEntity));
    }

    // 쪽지 답장
    public MessageDTO replyMessage(Long originalMessageId, MessageDTO replyDTO, UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        MessageEntity originalMessage = messageRepository.findById(originalMessageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메세지를 찾을 수 없습니다."));

        UserEntity sender = userRepository.findById(replyDTO.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("송신인이 존재하지 않습니다."));
        UserEntity receiver = originalMessage.getSender();

        MessageEntity replyMessage = replyDTO.dtoToEntity(sender, receiver, MessageStatus.않읽음);
        replyMessage.setCreateTime(LocalDateTime.now());
        return MessageDTO.entityToDTO(messageRepository.save(replyMessage));
    }

    // 쪽지 읽음 상태로 변경
    public MessageDTO readMessage(Long messageId, UserDetails userDetails) {
        MessageEntity messageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메세지를 찾을 수 없습니다."));

        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        messageEntity.setStatus(MessageStatus.읽음);
        messageRepository.save(messageEntity);
        return MessageDTO.entityToDTO(messageEntity);
    }

    // 쪽지 삭제
    public MessageDTO deleteMessage(Long messageId, UserDetails userDetails) {
        MessageEntity messageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메세지를 찾을 수 없습니다."));

        if (userDetails == null) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }

        messageEntity.setStatus(MessageStatus.삭제됨);
        messageRepository.save(messageEntity);
        return MessageDTO.entityToDTO(messageEntity);
    }

    // 해당 유저 쪽지 전체 조회
    public List<MessageDTO> getAllMessagesForUser(Long userId) {
        return messageRepository.findAllBySenderIdOrReceiverId(userId, userId)
                .stream()
                .map(MessageDTO::entityToDTO)
                .collect(Collectors.toList());
    }
}
