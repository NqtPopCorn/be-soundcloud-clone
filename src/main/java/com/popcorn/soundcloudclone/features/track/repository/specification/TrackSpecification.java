package com.popcorn.soundcloudclone.features.track.repository.specification;

import com.popcorn.soundcloudclone.features.album.entity.Album;
import com.popcorn.soundcloudclone.features.album.entity.AlbumTrack;
import com.popcorn.soundcloudclone.features.genre.entity.Genre;
import com.popcorn.soundcloudclone.features.playlist.entity.PlaylistTrack;
import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.users.entity.User;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class TrackSpecification {

    /**
     * Find by keyword
     * 
     * @param keyword <String>
     * @return Specification
     */
    public static Specification<Track> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return null;
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likeKeyword),
                    cb.like(cb.lower(root.<User>get("artist").get("stageName")), likeKeyword));
        };
    }

    /**
     * Artist username 'like' specification
     */
    public static Specification<Track> hasArtistName(String username) {
        return (root, query, cb) -> {
            // rong thi skip
            if (username == null || username.isBlank())
                return null;
            return cb.equal(root.<User>get("artist").get("username"), username);
        };
    }

    public static Specification<Track> hasArtistId(Integer artistId) {
        return (root, query, cb) -> {
            // rong thi skip
            if (artistId == null)
                return null;
            return cb.equal(root.<User>get("artist").get("id"), artistId);
        };
    }

    public static Specification<Track> hasGenre(String genre) {
        return (root, query, cb) -> {
            // rong thi skip
            if (genre == null || genre.isBlank())
                return null;
            Join<Track, Genre> genreJoin = root.join("genres", JoinType.INNER);
            return cb.equal(cb.lower(genreJoin.get("name")), genre.toLowerCase());
        };
    }

    public static Specification<Track> privacy(Track.Privacy privacy) {
        if (privacy == null)
            return null;
        return (root, query, cb) -> cb.equal(root.get("privacy"), privacy);
    }

    public static Specification<Track> isInAlbum(Integer albumId) {
        return (root, query, cb) -> {
            if (albumId == null)
                return null;

            Subquery<AlbumTrack> subquery = query.subquery(AlbumTrack.class);

            Root<AlbumTrack> subRoot = subquery.from(AlbumTrack.class);

            subquery.select(subRoot);

            // Tạo điều kiện WHERE (Quan trọng nhất)
            subquery.where(cb.and(
                    // Điều kiện A: Liên kết Subquery với Query cha (Correlated)
                    // "Track trong AlbumTrack phải bằng Track đang query ở ngoài (root)"
                    cb.equal(subRoot.get("track"), root),

                    // Điều kiện B: Lọc theo albumId
                    cb.equal(subRoot.get("album").get("id"), albumId)));

            return cb.exists(subquery);
        };
    }

    public static Specification<Track> isInPlaylist(Integer playlistId) {
        return (root, query, cb) -> {
            if (playlistId == null)
                return null;

            Subquery<PlaylistTrack> subquery = query.subquery(PlaylistTrack.class);

            Root<PlaylistTrack> subRoot = subquery.from(PlaylistTrack.class);

            subquery.select(subRoot);

            // Tạo điều kiện WHERE (Quan trọng nhất)
            subquery.where(cb.and(
                    // Điều kiện A: Liên kết Subquery với Query cha (Correlated)
                    // "Track trong PlaylistTrack phải bằng Track đang query ở ngoài (root)"
                    cb.equal(subRoot.get("track"), root),

                    // Điều kiện B: Lọc theo albumId
                    cb.equal(subRoot.get("playlist").get("id"), playlistId)));

            return cb.exists(subquery);
        };
    }
}
