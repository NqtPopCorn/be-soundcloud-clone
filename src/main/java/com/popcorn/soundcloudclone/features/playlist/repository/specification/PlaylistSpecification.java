package com.popcorn.soundcloudclone.features.playlist.repository.specification;

import com.popcorn.soundcloudclone.features.album.entity.Album;
import com.popcorn.soundcloudclone.features.playlist.entity.Playlist;
import com.popcorn.soundcloudclone.features.users.entity.User;

import org.springframework.data.jpa.domain.Specification;

public class PlaylistSpecification {

    /**
     * Find by keyword
     * 
     * @param keyword <String>
     * @return Specification
     */
    public static Specification<Playlist> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return null;
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            // return cb.or(
            return cb.like(cb.lower(root.get("name")), likeKeyword);
            // );
        };
    }

    public static Specification<Playlist> ownByArtistId(Integer artistId) {
        return (root, query, cb) -> {
            if (artistId == null)
                return null;
            return cb.or(
                    cb.equal(root.<User>get("creator").get("id"), artistId));
        };
    }
}
