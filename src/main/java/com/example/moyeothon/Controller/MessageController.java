package com.example.moyeothon.Controller;

import com.example.moyeothon.DTO.MessageDTO;
import com.example.moyeothon.Service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 쪽지 전송
    @Operation(summary = "쪽지 전송")
    @PostMapping("/{uid}/{bucketListId}")
    public ResponseEntity<MessageDTO> createMessage(@PathVariable String uid, @PathVariable Long bucketListId, @RequestBody MessageDTO messageDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.createMessage(uid, bucketListId, messageDTO, userDetails));
    }

    // 쪽지 답장
    @Operation(summary = "쪽지 답장")
    @PostMapping("/reply/{uid}/{messageId}/{bucketListId}")
    public ResponseEntity<MessageDTO> replyMessage(@PathVariable Long messageId, @PathVariable String uid, @PathVariable Long bucketListId, @RequestBody MessageDTO messageDTO, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.replyMessage(messageId, uid, bucketListId, messageDTO, userDetails));
    }

    // 쪽지 읽음 처리
    @Operation(summary = "쪽지 읽음 처리")
    @PatchMapping("/read/{messageId}/{uid}")
    public ResponseEntity<MessageDTO> readMessage(@PathVariable Long messageId, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.readMessage(messageId, uid, userDetails));
    }

    // 쪽지 삭제
    @Operation(summary = "쪽지 삭제")
    @DeleteMapping("/{messageId}/{uid}")
    public ResponseEntity<MessageDTO> deleteMessage(@PathVariable Long messageId, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.deleteMessage(messageId, uid, userDetails));
    }

    // 해당 유저 송수신 쪽지 전체 조회
    @Operation(summary = "해당 유저 송수신 쪽지 전체 조회")
    @GetMapping("/user/{uid}")
    public ResponseEntity<List<MessageDTO>> getAllMessagesForUser(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getAllMessagesForUser(uid, userDetails));
    }

    // 해당 유저 송신 쪽지 전체 조회
    @Operation(summary = "해당 유저 송신 쪽지 전체 조회")
    @GetMapping("/user/sendmessage/{uid}")
    public ResponseEntity<List<MessageDTO>> getAllMessageForSender(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getAllMessageForSender(uid, userDetails));
    }

    // 해당 유저 수신 쪽지 전체 조회
    @Operation(summary = "해당 유저 수신 쪽지 전체 조회")
    @GetMapping("/user/receivemessage/{uid}")
    public ResponseEntity<List<MessageDTO>> getAllMessageForReceiver(@PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.getAllMessageForReceiver(uid, userDetails));
    }

    // 특정 키워드가 포함된 쪽지 검색 (카테고리 키워드 검색 구현 전 연습)
    @Operation(summary = "특정 키워드가 포함된 쪽지 검색 (카테고리 키워드 검색 구현 전 연습)")
    @GetMapping("/search/{uid}")
    public ResponseEntity<List<MessageDTO>> searchMessagesByContent(@RequestParam String keyword, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(messageService.searchMessagesByContent(keyword, uid, userDetails));
    }
}
