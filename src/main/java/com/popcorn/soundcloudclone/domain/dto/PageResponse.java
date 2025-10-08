package com.popcorn.soundcloudclone.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PageResponse<T> {
    boolean last, first;
    int totalPages, size, numberOfElements;
    long totalElements;
    List<T> items;
}
