package com.popcorn.soundcloudclone.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artist_link")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private User artist;

    @Enumerated(EnumType.STRING)
    private LinkType type;

    @Column(columnDefinition = "TEXT")
    private String value;

    public enum LinkType {
        contact, donate
    }
}
