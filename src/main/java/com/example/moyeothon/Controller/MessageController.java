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
    @PostMapping("/{uid}")
    public ResponseEntity<MessageDTO> createMessage(@PathVariable String uid, @RequestBody MessageDTO messageDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.createMessage(uid, messageDTO, userDetails));
    }

    // 쪽지 답장
    @PostMapping("/reply/{messageId}/{uid}")
    public ResponseEntity<MessageDTO> replyMessage(@PathVariable Long messageId, @PathVariable String uid, @RequestBody MessageDTO messageDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.replyMessage(messageId, uid, messageDTO, userDetails));
    }

    // 쪽지 읽음 처리
    @PatchMapping("/read/{messageId}/{uid}")
    public ResponseEntity<MessageDTO> readMessage(@PathVariable Long messageId, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.readMessage(messageId, uid, userDetails));
    }

    // 쪽지 삭제
    @DeleteMapping("/{messageId}/{uid}")
    public ResponseEntity<MessageDTO> deleteMessage(@PathVariable Long messageId, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.deleteMessage(messageId, uid, userDetails));
    }

    // 해당 유저 쪽지 전체 조회
    @GetMapping("/user/{uid}")
    public ResponseEntity<List<MessageDTO>> getAllMessagesForUser(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getAllMessagesForUser(uid, userDetails));
    }

    // 해당 유저 송신 쪽지 전체 조회
    @GetMapping("/user/sendmessage/{uid}")
    public ResponseEntity<List<MessageDTO>> getAllMessageForSender(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getAllMessageForSender(uid, userDetails));
    }

    // 해당 유저 수신 쪽지 전체 조회
    @GetMapping("/user/receivemessage/{uid}")
    public ResponseEntity<List<MessageDTO>> getAllMessageForReceiver(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getAllMessageForReceiver(uid, userDetails));
    }
}
