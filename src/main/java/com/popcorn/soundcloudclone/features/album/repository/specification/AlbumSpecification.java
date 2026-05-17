package com.popcorn.soundcloudclone.features.album.repository.specification;

import com.popcorn.soundcloudclone.features.album.entity.Album;
import com.popcorn.soundcloudclone.features.users.entity.User;

import org.springframework.data.jpa.domain.Specification;

public class AlbumSpecification {

    /**
     * Find by keyword
     * 
     * @param keyword <String>
     * @return Specification
     */
    public static Specification<Album> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return null;
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likeKeyword),
                    cb.like(cb.lower(root.<User>get("artist").get("stageName")), likeKeyword));
        };
    }

    public static Specification<Album> ownByArtistId(Integer artistId) {
        return (root, query, cb) -> {
            if (artistId == null)
                return null;
            return cb.or(
                    cb.equal(root.<User>get("artist").get("id"), artistId));
        };
    }
}
