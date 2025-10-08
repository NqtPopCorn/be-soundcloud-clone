package com.popcorn.soundcloudclone.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private User artist;

    @ManyToOne // OneToOne ???
    @JoinColumn(name = "audio_upload_id", nullable = false)
    private FileUpload audioUpload;

    @ManyToOne // OneToOne ???
    @JoinColumn(name = "image_upload_id", nullable = false)
    private FileUpload imageUpload;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", nullable = false)
    private Privacy privacy = Privacy.PUBLIC;

    @ManyToMany(mappedBy = "likedTracks")
    private Set<User> likedByUsers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "track_genre",
            joinColumns = @JoinColumn(name = "track_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PUBLISHED;

    @ManyToOne
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    private String name;
    private int playCount = 0;
    private int likeCount = 0;
    private int duration;
    private String description;
    private String tags = "";

    private LocalDate uploadDate ;
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDate.now();
    }

    public enum Privacy {
        PUBLIC, PRIVATE
    }

    public enum Status {
        DRAFT, PUBLISHED, SUSPENDED
    }

}
