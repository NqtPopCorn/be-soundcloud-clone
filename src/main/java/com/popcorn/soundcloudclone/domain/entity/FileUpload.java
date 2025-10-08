package com.popcorn.soundcloudclone.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_upload")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String filePath;

    @Column
    private String fileName;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column
    private LocalDateTime uploadedAt;

    public enum FileType {
        audio, image
    }
}