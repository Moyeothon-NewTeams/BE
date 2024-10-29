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

    @Operation(summary = "bucketList 추가")
    @PostMapping("/bucket/create/{userId}")
    public ResponseEntity<ResponseDto> addBucket(@Validated @PathVariable Long userId ,
                                                 @RequestBody RequestDto requestDto){

        ResponseDto responseDto = bucketService.addBucket(requestDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "bucketList 상세보기")
    @GetMapping("/bucket/{bucketId}/{userId}")
    public ResponseEntity<ResponseDto> getBucket(@Validated  @PathVariable Long bucketId, @PathVariable(required = false) Long userId){
        return ResponseEntity.ok(bucketService.getBucket(bucketId,userId));
    }

    @Operation(summary = "bucketList 삭제하기")
    @DeleteMapping("/bucket/{userId}/{bucketId}")
    public ResponseEntity<String> deleteBucket(@PathVariable Long userId, @PathVariable Long bucketId) {
        bucketService.deleteBucket(bucketId, userId);
        return ResponseEntity.ok("삭제가 성공했습니다.");
    }


    @Operation(summary = "bucketList 수정하기")
    @PutMapping("/bucket/{userId}/{bucketId}")
    public ResponseEntity<ResponseDto> updateBucket(@Validated @PathVariable Long userId,
                                                    @PathVariable Long bucketId,
                                                    @RequestBody RequestDto requestDto){
        return ResponseEntity.ok(bucketService.updateBucket(bucketId, userId, requestDto));
    }

    @Operation(summary = "유저의 bucketList 전체 조회")
    @GetMapping("/user/bucket/{userId}")
    public ResponseEntity<List<ResponseDto>> getAllUserBucket(@Validated @PathVariable Long userId){
        return ResponseEntity.ok(bucketService.getUserAllBucket(userId));

    }

    @Operation(summary = "모든 bucketList 보기")
    @GetMapping("/bucket/all/{userId}")
    public ResponseEntity<List<ResponseDto>> getAllBucket(@Validated @PathVariable Long userId){
        return ResponseEntity.ok(bucketService.getAllBucket(userId));
    }

    // 제목, 내용 키워드별로 버킷리스트 검색하기
    @Operation(summary = "제목, 내용 키워드별로 버킷리스트 검색하기")
    @GetMapping("/bucket/search/{uid}")
    public ResponseEntity<List<ResponseDto>> searchTitleAndContent(@RequestParam String keyword, @PathVariable String uid, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bucketService.searchTitleAndContent(keyword, uid, userDetails));
    }
}
