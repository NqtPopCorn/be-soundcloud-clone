package com.popcorn.soundcloudclone.repository.specification;

import com.popcorn.soundcloudclone.domain.entity.Album;
import com.popcorn.soundcloudclone.domain.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class AlbumSpecification {

    /**
     * Find by keyword
     * @param keyword <String>
     * @return Specification
     */
    public static Specification<Album> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likeKeyword),
                    cb.like(cb.lower(root.<User>get("artist").get("stageName")), likeKeyword),
                    cb.like(cb.lower(root.<User>get("artist").get("username")), likeKeyword)
            );
        };
    }
}
