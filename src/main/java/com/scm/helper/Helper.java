package com.scm.helper;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Slf4j
public class Helper {
    public static String getEmailOfLoggedInUser(Authentication authentication){

        if(authentication instanceof OAuth2AuthenticationToken){

            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            String clientId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String userName = "";

            if(clientId.equalsIgnoreCase("google")){
                log.info("Getting userName from google");
                userName = oAuth2User.getAttribute("email");
            } else if (clientId.equalsIgnoreCase("github")) {
                log.info("Getting userName from github");
                userName = oAuth2User.getAttribute("email") != null ? oAuth2User.getAttribute("email") : oAuth2User.getAttribute("login") + "@gmail.com";
            }

            return userName;

        } else {
            log.info("Getting data from local database");
            return authentication.getName();
        }
    }
}
