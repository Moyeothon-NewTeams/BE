package com.example.moyeothon.Entity;


import com.example.moyeothon.DTO.BucketDto.RequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BucketlistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bucket_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private String title;

    private String content;

    private boolean isPublic;


    public BucketlistEntity(RequestDto requestDto, UserEntity user){
        this.user = user;
        this.content = requestDto.getContent();
        this.title = requestDto.getTitle();
        this.isPublic = requestDto.isPublic();
    }

    public void update(RequestDto requestDto){
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.isPublic = requestDto.isPublic();
    }

}
