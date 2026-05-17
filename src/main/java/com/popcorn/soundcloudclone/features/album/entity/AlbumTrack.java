package com.popcorn.soundcloudclone.features.album.entity;

import com.popcorn.soundcloudclone.features.track.entity.Track;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "album_track", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"album_id", "track_id"})
})
public class AlbumTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @Column
    private Integer position;
}