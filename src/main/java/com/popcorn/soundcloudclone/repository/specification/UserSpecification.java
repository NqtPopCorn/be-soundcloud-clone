package com.popcorn.soundcloudclone.repository.specification;

import com.popcorn.soundcloudclone.domain.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("username")), likeKeyword),
                    cb.like(cb.lower(root.get("email")), likeKeyword),
                    cb.like(cb.toString(root.get("id")), likeKeyword)
                );
        };
    }
}
