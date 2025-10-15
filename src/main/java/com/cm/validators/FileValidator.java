package com.cm.validators;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private static final long MAX_FILE_SIZE = 1024 * 1024 * 2; // 2MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg",
        "image/png", 
        "image/jpg",
        "image/gif"
    );
    
    private boolean checkEmpty;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.checkEmpty = constraintAnnotation.checkEmpty();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        
        // If file is empty and we're not checking for empty files, return true
        if (file == null || file.isEmpty()) {
            if (!checkEmpty) {
                return true;
            } else {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("File is required")
                       .addConstraintViolation();
                return false;
            }
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size should be less than 2MB")
                   .addConstraintViolation();
            return false;
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Only image files are allowed (JPEG, PNG, JPG, GIF)")
                   .addConstraintViolation();
            return false;
        }

        // Validate file name
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid file name")
                   .addConstraintViolation();
            return false;
        }

        // Check file extension
        if (!hasValidExtension(originalFilename)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Invalid file extension. Allowed: .jpg, .jpeg, .png, .gif")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean hasValidExtension(String filename) {
        if (filename == null) return false;
        
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        String lowerCaseFilename = filename.toLowerCase();
        
        for (String ext : allowedExtensions) {
            if (lowerCaseFilename.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}