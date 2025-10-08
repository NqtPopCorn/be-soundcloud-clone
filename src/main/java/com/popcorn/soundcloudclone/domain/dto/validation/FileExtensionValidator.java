package com.popcorn.soundcloudclone.domain.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileExtensionValidator implements ConstraintValidator<FileExtension, MultipartFile> {

    private List<String> allowedExtensions;

    @Override
    public void initialize(FileExtension constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.allowedExtensions = List.of(constraintAnnotation.values().split(", "));
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null || value.getOriginalFilename() == null) return true;
        String extension = value.getOriginalFilename().substring(value.getOriginalFilename().lastIndexOf("."));
        return allowedExtensions.contains(extension);
    }
}
