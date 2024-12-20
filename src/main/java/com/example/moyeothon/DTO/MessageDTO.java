package com.example.moyeothon.DTO;

import com.example.moyeothon.DTO.BucketDto.RequestDto;
import com.example.moyeothon.DTO.BucketDto.ResponseDto;
import com.example.moyeothon.Entity.BucketlistEntity;
import com.example.moyeothon.Entity.MessageEntity;
import com.example.moyeothon.Entity.UserEntity;
import com.example.moyeothon.Enum.MessageStatus;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MessageDTO {
    private Long id;
    private String content;
    private LocalDateTime createTime;
    private Long senderId;
    private Long receiverId;
    private UserDTO sender;
    private UserDTO receiver;
    private MessageStatus status;
    private Long bucketListId;
    private ResponseDto bucketList;

    public static MessageDTO entityToDTO(MessageEntity messageEntity) {
        return new MessageDTO(
                messageEntity.getId(),
                messageEntity.getContent(),
                messageEntity.getCreateTime(),
                messageEntity.getSender().getId(),
                messageEntity.getReceiver().getId(),
                UserDTO.entityToDto(messageEntity.getSender()),
                UserDTO.entityToDto(messageEntity.getReceiver()),
                messageEntity.getStatus(),
                messageEntity.getBucketList().getId(),
                ResponseDto.entityToDto(messageEntity.getBucketList())
        );
    }

    public MessageEntity dtoToEntity(UserEntity sender, UserEntity receiver, BucketlistEntity bucketList) {
        return new MessageEntity(id, content, createTime, sender, receiver, bucketList, status);
    }
}
