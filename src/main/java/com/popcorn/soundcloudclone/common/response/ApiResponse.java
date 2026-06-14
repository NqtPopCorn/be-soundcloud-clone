package com.popcorn.soundcloudclone.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    public int statusCode = 200;
    @Builder.Default
    public String message = "Success";
    public T result;
}


