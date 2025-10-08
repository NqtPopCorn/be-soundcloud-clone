package com.popcorn.soundcloudclone.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "play_track_log", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"track_id", "user_id"})
})
public class TrackPlay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    @ManyToOne
//    @JoinColumn(name = "track_id")
//    private Track track;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    private Integer trackId;
    private Integer userId;

    @Column
    private LocalDateTime createdAt;
}