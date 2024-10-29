package com.example.moyeothon.DTO.BucketDto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    @JsonProperty("isPublic")
    private boolean isPublic;
}
