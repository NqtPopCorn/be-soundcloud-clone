package com.popcorn.soundcloudclone.domain.mapper;

import com.popcorn.soundcloudclone.domain.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageResponseBuilder<T> {

    public PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .first(page.isFirst())
                .last(page.isLast())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .numberOfElements(page.getNumberOfElements())
                .items(page.getContent())
                .totalElements(page.getTotalElements())
                .build();
    }

}
