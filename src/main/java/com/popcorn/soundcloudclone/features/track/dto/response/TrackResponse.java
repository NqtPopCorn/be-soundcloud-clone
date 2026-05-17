package com.popcorn.soundcloudclone.features.track.dto.response;

import com.popcorn.soundcloudclone.features.track.entity.Track;
import com.popcorn.soundcloudclone.features.users.dto.response.UserSummaryResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
public class TrackResponse {

    Integer id;

    String name;

    int playCount, likeCount;

    int duration;

    String audioUrl, imageUrl;

    LocalDate uploadDate;

    List<String> genres;

    UserSummaryResponse artist;

    boolean isLiked;

    String description;

    List<String> tags;

    Track.Privacy privacy;

}
