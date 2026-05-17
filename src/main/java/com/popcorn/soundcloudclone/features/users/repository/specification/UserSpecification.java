package com.popcorn.soundcloudclone.features.users.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.popcorn.soundcloudclone.features.users.entity.User;

public class UserSpecification {

    public static Specification<User> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return cb.conjunction();
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("username")), likeKeyword),
                    cb.like(cb.lower(root.get("email")), likeKeyword),
                    cb.like(cb.toString(root.get("id")), likeKeyword));
        };
    }
}
