package com.popcorn.soundcloudclone.repository.specification;

import com.popcorn.soundcloudclone.domain.entity.Playlist;
import org.springframework.data.jpa.domain.Specification;

public class PlaylistSpecification {

    /**
     * Find by keyword
     * @param keyword <String>
     * @return Specification
     */
    public static Specification<Playlist> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
//            return cb.or(
                    return cb.like(cb.lower(root.get("name")), likeKeyword);
//            );
        };
    }
}
