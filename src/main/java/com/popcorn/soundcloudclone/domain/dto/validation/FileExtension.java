package com.popcorn.soundcloudclone.domain.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validate multipart file extension with a string as values. <br>
 * Example: @FileExtension(values = ".jpeg, .png, .mp3"
 */
@Documented
@Constraint(validatedBy = FileExtensionValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileExtension {

    String message() default "INVALID_FILE_EXTENSION";

    String values();

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
