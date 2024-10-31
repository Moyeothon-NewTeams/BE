package com.example.moyeothon.Controller;

import com.example.moyeothon.DTO.BucketDto.RequestDto;
import com.example.moyeothon.DTO.BucketDto.ResponseDto;
import com.example.moyeothon.Service.BucketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name= "bucketList Api")
@RequestMapping("/api")
public class BucketController {
    private final BucketService bucketService;

    // 버킷리스트 추가
    @Operation(summary = "bucketList 추가")
    @PostMapping("/bucket/create/{uid}")
    public ResponseEntity<ResponseDto> addBucket(@Validated @RequestBody RequestDto requestDto, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(bucketService.addBucket(requestDto, uid, userDetails));
    }

    // id로 버킷리스트 조회
    @Operation(summary = "bucketList 상세보기")
    @GetMapping("/bucket/{uid}/{bucketId}")
    public ResponseEntity<ResponseDto> getBucket(@Validated  @PathVariable Long bucketId, @PathVariable(required = false) String uid, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(bucketService.getBucket(bucketId, uid, userDetails));
    }

    // 버킷리스트 삭제
    @Operation(summary = "bucketList 삭제하기")
    @DeleteMapping("/bucket/{uid}/{bucketId}")
    public ResponseEntity<ResponseDto> deleteBucket(@PathVariable Long bucketId, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bucketService.deleteBucket(bucketId, uid, userDetails));
    }

    // 버킷리스트 수정
    @Operation(summary = "bucketList 수정하기")
    @PutMapping("/bucket/{uid}/{bucketId}")
    public ResponseEntity<ResponseDto> updateBucket(@Validated @PathVariable Long bucketId, @PathVariable String uid, @RequestBody RequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(bucketService.updateBucket(bucketId, uid, requestDto, userDetails));
    }

    // 해당 유저 버킷리스트 전체 조회
    @Operation(summary = "유저의 bucketList 전체 조회")
    @GetMapping("/user/bucket/{uid}")
    public ResponseEntity<List<ResponseDto>> getAllUserBucket(@Validated @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(bucketService.getUserAllBucket(uid, userDetails));
    }

    // 버킷리스트 전체 조회
    @Operation(summary = "모든 bucketList 보기")
    @GetMapping("/bucket/all/{uid}")
    public ResponseEntity<List<ResponseDto>> getAllBucket(@Validated @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(bucketService.getAllBucket(uid, userDetails));
    }

    // 제목, 내용 키워드별로 버킷리스트 검색하기
    @Operation(summary = "제목, 내용 키워드별로 버킷리스트 검색하기")
    @GetMapping("/bucket/search/{uid}")
    public ResponseEntity<List<ResponseDto>> searchTitleAndContent(@RequestParam String keyword, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bucketService.searchTitleAndContent(keyword, uid, userDetails));
    }
}
