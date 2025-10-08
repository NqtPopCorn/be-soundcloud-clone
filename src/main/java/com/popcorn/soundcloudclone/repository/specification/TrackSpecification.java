package com.popcorn.soundcloudclone.repository.specification;

import com.popcorn.soundcloudclone.domain.entity.Track;
import com.popcorn.soundcloudclone.domain.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class TrackSpecification {

    /**
     * Find by keyword
     * @param keyword <String>
     * @return Specification
     */
    public static Specification<Track> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likeKeyword),
                    cb.like(cb.lower(root.<User>get("artist").get("stageName")), likeKeyword)
            );
        };
    }

    /**
     * Artist username 'like' specification
     */
    public static Specification<Track> hasArtist(String username) {
        return (root, query, cb) -> {
            // rong thi skip
            if (username == null || username.isBlank()) return null;
            String likeKeyword = username.toLowerCase();
            return cb.like(cb.lower(root.<User>get("artist").get("username")), likeKeyword);
        };
    }

    /**
     * Has privacy PRIVATE OR PUBLIC
     * @return Specification
     */
    public static Specification<Track> hasPrivacy(Track.Privacy privacy) {
        if(privacy == null) return null;
        return (root, query, cb) -> cb.equal(root.get("privacy"), privacy.name());
    }
}
