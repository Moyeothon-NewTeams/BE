package com.example.moyeothon.Entity;

import com.example.moyeothon.Enum.MessageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "messages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime createTime;

    @ManyToOne
    @JoinColumn(name="sender_id")
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name="receiver_id")
    private UserEntity receiver;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;
}
