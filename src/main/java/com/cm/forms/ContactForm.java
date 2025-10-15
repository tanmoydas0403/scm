package com.cm.forms;

import org.springframework.web.multipart.MultipartFile;
import com.cm.validators.ValidFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactForm {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Invalid phone number")
    private String phoneNumber;
    
    @NotBlank(message = "Address is required")
    private String address;
    private String description;
    private boolean favorite = false;
    private String websiteLink;
    private String linkedInLink;

    @ValidFile(
        message = "Please upload a valid image file (JPEG, PNG, JPG, GIF) under 2MB",
        checkEmpty = false
    )
    private MultipartFile contactImage;
}