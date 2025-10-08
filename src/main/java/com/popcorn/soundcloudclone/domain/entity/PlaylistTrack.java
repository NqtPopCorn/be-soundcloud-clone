package com.popcorn.soundcloudclone.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "playlist_track", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"playlist_id", "track_id"})
})
public class PlaylistTrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @Column
    private Integer position;
}