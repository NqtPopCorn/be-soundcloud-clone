package com.popcorn.soundcloudclone.features.playlist.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.popcorn.soundcloudclone.features.users.entity.User;

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

    @Builder.Default
    private Boolean isPublic = false;

    @Builder.Default
    private int likeCount = 0;
}