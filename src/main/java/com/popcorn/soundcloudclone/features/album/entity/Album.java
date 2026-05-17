package com.popcorn.soundcloudclone.features.album.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import com.popcorn.soundcloudclone.features.users.entity.User;

@Entity
@Table(name = "albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private User artist;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;

    private String tags;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @OneToMany(mappedBy = "album")
    private List<AlbumTrack> joinTracks;

    @Column
    private LocalDate releaseDate;

    private int likeCount = 0;
}