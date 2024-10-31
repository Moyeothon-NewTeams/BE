package com.example.moyeothon.Service;


import com.example.moyeothon.DTO.BucketDto.RequestDto;
import com.example.moyeothon.DTO.BucketDto.ResponseDto;
import com.example.moyeothon.Entity.BucketlistEntity;
import com.example.moyeothon.Entity.UserEntity;
import com.example.moyeothon.Repository.BucketRepository;
import com.example.moyeothon.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BucketService {

    private static final Logger logger = LoggerFactory.getLogger(BucketService.class);
    private final UserRepository userRepository;
    private final BucketRepository bucketRepository;

    // 버킷리스트 추가
    public ResponseDto addBucket(RequestDto requestDto, String uid, UserDetails userDetails){
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        UserEntity user = userRepository.findByUid(uid);
        BucketlistEntity bucketList = bucketRepository.save(new BucketlistEntity(requestDto, user));
        return ResponseDto.entityToDto(bucketList);
    }

    // id로 버킷리스트 조회
    public ResponseDto getBucket(Long id, String uid, UserDetails userDetails){
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        BucketlistEntity bucketList = bucketRepository.findById(id).orElseThrow();
        if (!bucketList.isPublic() && !bucketList.getUser().getUid().equals(uid)) {
            throw new RuntimeException("해당 버킷리스트에 접근 권한이 없습니다.");
        }
        return ResponseDto.entityToDto(bucketList);
    }

    // 버킷리스트 삭제
    public ResponseDto deleteBucket(Long id, String uid, UserDetails userDetails){
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        BucketlistEntity bucketList = bucketRepository.findById(id).orElseThrow();
        if (!bucketList.getUser().getUid().equals(uid)) {
            throw new AccessDeniedException("권한이 없는 유저입니다.");
        }
        bucketRepository.deleteById(id);
        return ResponseDto.entityToDto(bucketList);
    }

    // 버킷리스트 수정
    public ResponseDto updateBucket(Long id, String uid, RequestDto requestDto, UserDetails userDetails){
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        BucketlistEntity bucketList = bucketRepository.findById(id).orElseThrow();
        if(!bucketList.getUser().getUid().equals(uid)){
            throw new AccessDeniedException("권환이 없는 유저입니다.");
        }
        bucketList.update(requestDto);
        return ResponseDto.entityToDto(bucketList);
    }

    // 해당 유저 버킷리스트 전체 조회
    public List<ResponseDto> getUserAllBucket(String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        return bucketRepository.findByUser_Uid(uid).stream()
                .map(ResponseDto::entityToDto)
                .collect(Collectors.toList());
    }

    // 버킷리스트 전체 조회
    public List<ResponseDto> getAllBucket(String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        return bucketRepository.findAll()
                    .stream()
                    .filter(bucket -> bucket.isPublic())
                    .map(ResponseDto::entityToDto)
                    .collect(Collectors.toList());
    }

    // 제목, 내용 키워드별로 버킷리스트 검색하기
    public List<ResponseDto> searchTitleAndContent(String keyword, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        return bucketRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(ResponseDto::entityToDto)
                .collect(Collectors.toList());
    }

    // 특정 버킷리스트의 공개 여부 변경
    public ResponseDto updateBucketVisibility(Long bucketId, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        BucketlistEntity bucketlist = bucketRepository.findById(bucketId)
                .orElseThrow(() -> new RuntimeException("버킷리스트를 찾을 수 없습니다."));
        if (!bucketlist.getUser().getUid().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        boolean newIsPublic = !bucketlist.isPublic();
        bucketlist.setPublic(newIsPublic);
        bucketRepository.save(bucketlist);
        logger.info("버킷리스트 ID {}의 공개 여부가 {}로 변경되었습니다.", bucketId, newIsPublic);
        return ResponseDto.entityToDto(bucketlist);
    }
}
