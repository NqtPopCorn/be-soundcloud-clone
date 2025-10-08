package com.popcorn.soundcloudclone.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    @Column(unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String password;

    @ManyToOne
    @JoinColumn(name = "avatar_upload_id")
    private FileUpload avatarUpload;

    @ManyToOne
    @JoinColumn(name = "background_upload_id")
    private FileUpload backgroundUpload;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "track_like_log",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    private Set<Track> likedTracks = new HashSet<>();

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String stageName;
    private int followersCount = 0;
    private int followingCount = 0;
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String bio;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN, ARTIST, USER
    }

}
