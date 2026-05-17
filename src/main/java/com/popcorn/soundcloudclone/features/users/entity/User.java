package com.popcorn.soundcloudclone.features.users.entity;

import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.album.entity.Album;
import com.popcorn.soundcloudclone.features.playlist.entity.Playlist;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String city;
    private String country;
    private String stageName;
    private int followersCount = 0;
    private int followingCount = 0;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String bio;
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN, ARTIST, USER
    }

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "background_url")
    private String backgroundUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "track_like_log", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "track_id"))
    private List<Track> likedTracks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "album_like_log", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "album_id"))
    private List<Album> likedAlbums = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "playlist_like_log", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "playlist_id"))
    private List<Playlist> likedPlaylists = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_followers", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
    private List<User> followingUsers = new ArrayList<>();

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private List<Playlist> playlists = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
