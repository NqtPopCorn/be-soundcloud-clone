package com.popcorn.soundcloudclone.domain.dto.track;

import com.popcorn.soundcloudclone.domain.dto.user.UserSummaryResponse;
import com.popcorn.soundcloudclone.domain.entity.Track;
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
