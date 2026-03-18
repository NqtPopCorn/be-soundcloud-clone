package com.popcorn.soundcloudclone.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    public int statusCode;
    public String message;
    public T result;
}
