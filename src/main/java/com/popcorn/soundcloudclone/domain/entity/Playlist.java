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
    private List<PlaylistTrack> joinTracks;

    @Column
    private String name;

    private Boolean isPublic = false;

    private int likeCount = 0;
}