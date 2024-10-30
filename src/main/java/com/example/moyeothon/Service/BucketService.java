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
    private final UserRepository userRepository;
    private final BucketRepository bucketRepository;

    public ResponseDto addBucket(RequestDto requestDto, Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 ID가 맞지 않습니다."));

        BucketlistEntity bucket = bucketRepository.save(new BucketlistEntity(requestDto, user));
        return new ResponseDto(bucket);
    }

    public ResponseDto getBucket(Long id, Long userId){
        BucketlistEntity bucketList = bucketRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("bucketId에 맞는 버킷리스트를 찾을 수 없습니다."));

        if (!bucketList.isPublic() && !bucketList.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 버킷리스트에 접근 권한이 없습니다.");
        }
        return new ResponseDto(bucketList);
    }

    public void  deleteBucket(Long id, Long userId){
        // 버킷리스트 조회
        BucketlistEntity bucketList = bucketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("bucket Id에 맞는 버킷리스트를 찾을 수 없습니다."));

        // 사용자 권한 확인 후 삭제 또는 예외 발생
        if (bucketList.getUser().getId().equals(userId)) {
            bucketRepository.deleteById(id);
        } else {
            throw new AccessDeniedException("권한이 없는 유저입니다.");
        }
    }

    public ResponseDto updateBucket(Long id, Long userId, RequestDto requestDto){
        BucketlistEntity bucketList = bucketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("bucket Id에 맞는 버킷리스트를 찾을 수 없습니다."));

        if(bucketList.getUser().getId().equals(userId)){
            bucketList.update(requestDto);
        }else{
            throw new AccessDeniedException("권환이 없는 유저입니다.");

        }
        return new ResponseDto(bucketList);
    }

    public List<ResponseDto> getUserAllBucket(Long userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("userId가 맞지 않습니다."));

        List<BucketlistEntity> bucketlistEntityList = bucketRepository.findByUserId(userId);
        List<ResponseDto> responseDtoList = new ArrayList<>();
        for(BucketlistEntity bucket : bucketlistEntityList){

            responseDtoList.add(new ResponseDto(bucket));

        }
        return responseDtoList;

    }

    public List<ResponseDto> getAllBucket(Long userId) {
        List<BucketlistEntity> bucketlistEntityList = bucketRepository.findAll();
        List<ResponseDto> responseDtoList = new ArrayList<>();

        for (BucketlistEntity bucket : bucketlistEntityList) {
            // 버킷리스트가 공개 상태이거나, 비공개 상태일 때 userId와 작성자 ID가 같을 때만 추가
            if (bucket.isPublic() || (userId != null && bucket.getUser().getId().equals(userId))) {
                responseDtoList.add(new ResponseDto(bucket));
            }
        }

        return responseDtoList;
    }

    // 제목, 내용 키워드별로 버킷리스트 검색하기
    public List<ResponseDto> searchTitleAndContent(String keyword, String uid, UserDetails userDetails) {
        if (!userDetails.getUsername().equals(uid)) {
            throw new RuntimeException("인증되지 않은 유저입니다.");
        }
        return bucketRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(ResponseDto::entityToDTO)
                .collect(Collectors.toList());
    }
}
