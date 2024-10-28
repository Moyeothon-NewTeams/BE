package com.example.moyeothon.Controller;

import com.example.moyeothon.DTO.MessageDTO;
import com.example.moyeothon.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 쪽지 전송
    @PostMapping
    public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.createMessage(messageDTO, userDetails));
    }

    // 쪽지 답장
    @PostMapping("/reply/{originalMessageId}")
    public ResponseEntity<MessageDTO> replyMessage(@PathVariable Long originalMessageId, @RequestBody MessageDTO messageDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.replyMessage(originalMessageId, messageDTO, userDetails));
    }

    // 쪽지 읽음 처리
    @PatchMapping("/read/{messageId}")
    public ResponseEntity<MessageDTO> readMessage(@PathVariable Long messageId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.readMessage(messageId, userDetails));
    }

    // 쪽지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<MessageDTO> deleteMessage(@PathVariable Long messageId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.deleteMessage(messageId, userDetails));
    }

    // 해당 유저 쪽지 전체 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getAllMessagesForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(messageService.getAllMessagesForUser(userId));
    }
}
