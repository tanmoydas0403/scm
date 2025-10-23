package com.cm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cm.service.EmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest
class CmApplicationTests {


private static final Logger logger = LoggerFactory.getLogger(CmApplicationTests.class);

    @Autowired
    private EmailService emailService;

    @Test
    void contextLoads() {
    }

    @Test
    void sendEmailTest() {
        try {
			logger.info("Starting email test...");
            emailService.sendEmail(
                "dtanmoy0403@gmail.com",
                "Test Subject from Mailtrap",
                "Hi Tanmoy..!"
            );
			logger.info("Email sent successfully!");
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
			logger.error("Failed to send email", e);
        }
    }
}