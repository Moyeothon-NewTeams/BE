package com.example.moyeothon.DTO.BucketDto;


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

    public static ResponseDto entityToDTO(BucketlistEntity bucketlistEntity){
        return new ResponseDto(
                bucketlistEntity.getId(),
                bucketlistEntity.getTitle(),
                bucketlistEntity.getContent(),
                bucketlistEntity.isPublic()
        );
    }

    public ResponseDto(BucketlistEntity bucket) {
        this.id = bucket.getId();
        this.title = bucket.getTitle();
        this.content = bucket.getContent();
        this.isPublic = bucket.isPublic();


    }

}
