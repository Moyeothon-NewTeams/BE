package com.example.moyeothon.DTO.BucketDto;

import com.example.moyeothon.DTO.UserDTO;
import com.example.moyeothon.Entity.BucketlistEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {
    private Long id;
    private String title;
    private String content;
    private boolean isPublic;
    private Long userId;
    private UserDTO user;

    public static ResponseDto entityToDto(BucketlistEntity bucketlistEntity){
        return new ResponseDto(
                bucketlistEntity.getId(),
                bucketlistEntity.getTitle(),
                bucketlistEntity.getContent(),
                bucketlistEntity.isPublic(),
                bucketlistEntity.getUser().getId(),
                UserDTO.entityToDto(bucketlistEntity.getUser())
        );
    }

    public ResponseDto(BucketlistEntity bucket) {
        this.id = bucket.getId();
        this.title = bucket.getTitle();
        this.content = bucket.getContent();
        this.isPublic = bucket.isPublic();
        this.userId = bucket.getUser().getId();
        this.user = new UserDTO();
    }
}
