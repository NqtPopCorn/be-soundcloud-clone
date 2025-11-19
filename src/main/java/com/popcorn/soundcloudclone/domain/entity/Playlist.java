package com.popcorn.soundcloudclone.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @OneToMany(mappedBy = "playlist")
//    @JoinTable(
//            name = "playlist_track",
//            joinColumns = @JoinColumn(name = "playlist_id"),
//            inverseJoinColumns = @JoinColumn(name = "track_id")
//    )
    private List<PlaylistTrack> tracks;

    @Column
    private String name;

    private Boolean isPublic = false;
}