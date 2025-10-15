package com.cm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.cm.entities.Providers;
import com.cm.entities.User;
import com.cm.helpers.AppConstants;
import com.cm.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = org.slf4j.LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepository userRepo;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, 
            HttpServletResponse response,
            Authentication authentication
        ) throws IOException, ServletException {
        
        logger.info("OAuthAuthenticationSuccessHandler");

        //identify the provider
        var oauth2AuthenticationToken =(OAuth2AuthenticationToken)authentication;

        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        
        logger.info(authorizedClientRegistrationId);

        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        oauthUser.getAttributes().forEach((key, value) -> logger.info(key + " : " + value));

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoles(List.of(AppConstants.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("dummy");


        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            //google 

            user.setEmail(oauthUser.getAttribute("email").toString());
            user.setProfilePic(oauthUser.getAttribute("picture").toString());
            user.setName(oauthUser.getAttribute("name").toString());
            user.setProvider(Providers.GOOGLE);
            user.setProviderUserId(oauthUser.getName());
            user.setAbout("This account created by google oauth login.");

        }else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            //github

            String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email").toString() : oauthUser.getAttribute("login").toString() + "@gmail.com";
            String picture = oauthUser.getAttribute("avatar_url").toString();
            String name = oauthUser.getAttribute("login").toString();
            String providerUserId = oauthUser.getName();

            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProvider(Providers.GITHUB);
            user.setProviderUserId(providerUserId);
            user.setAbout("This account created by github oauth login.");


        }else if (authorizedClientRegistrationId.equalsIgnoreCase("facebook")) {
            //facebook
            
        }else{
            logger.info("Unknown provider");
        }

        

/*
        DefaultOAuth2User user = (DefaultOAuth2User)authentication.getPrincipal();

        // logger.info(user.getName());
        // user.getAttributes().forEach((key, value) -> logger.info(key + " : " + value));
        // logger.info(user.getAttributes().toString());

        String email = user.getAttribute("email").toString();
        String name = user.getAttribute("name").toString();
        String picture = user.getAttribute("picture").toString();

        //create user and save in database
        User user1 = new User();
        user1.setEmail(email);
        user1.setName(name);
        user1.setProfilePic(picture);
        user1.setPassword("password");
        user1.setUserId(UUID.randomUUID().toString());
        user1.setProvider(Providers.GOOGLE);
        user1.setEnabled(true);
        user1.setEmailVerified(true);
        user1.setProviderUserId(user.getName());
        user1.setRoles(List.of(AppConstants.ROLE_USER));
        user1.setAbout("About me");

        userRepo.findByEmail(email).ifPresentOrElse(u -> userRepo.save(u), () -> userRepo.save(user1));
 */

        userRepo.findByEmail(user.getEmail())
        .ifPresentOrElse(u -> userRepo.save(u), () -> userRepo.save(user));       

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
        
    }

}
