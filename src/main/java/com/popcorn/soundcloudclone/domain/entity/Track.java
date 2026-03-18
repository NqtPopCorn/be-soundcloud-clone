package com.popcorn.soundcloudclone.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artist_id")
    private User artist;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "audio_upload_id", nullable = false)
    private FileUpload audioUpload;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_upload_id", nullable = false)
    private FileUpload imageUpload;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy", nullable = false)
    private Privacy privacy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "track_genre", joinColumns = @JoinColumn(name = "track_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;

    private String name;
    private int playCount = 0;
    private int likeCount = 0;
    private int duration;
    private String description;
    private String tags;

    private LocalDate uploadDate;
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        uploadDate = LocalDate.now();
        privacy = Privacy.PUBLIC;
    }

    public enum Privacy {
        PUBLIC, PRIVATE
    }

}
