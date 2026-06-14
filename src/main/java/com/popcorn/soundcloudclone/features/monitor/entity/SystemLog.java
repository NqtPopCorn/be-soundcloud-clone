package com.popcorn.soundcloudclone.features.monitor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String level; // INFO, WARN, ERROR

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 255)
    private String loggerName;

    @Column(columnDefinition = "TEXT")
    private String exception;

    private LocalDateTime timestamp;
}
