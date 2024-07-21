package com.scm.config;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helper.AppConstants;
import com.scm.repositories.UserRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class OAuthAuthenicationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("OAuthenticationSuccessHandler");

        // identify the provider

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        String authorizedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        log.info(authorizedClientRegistrationId);

        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        defaultOAuth2User.getAttributes().forEach((key, value)->{
            log.info(key + " : " + value);
        });

        // create user and save in database
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("dummy");

        if(authorizedClientRegistrationId.equalsIgnoreCase("google")){
            // google attributes
            user.setEmail(defaultOAuth2User.getAttribute("email"));
            user.setProfilePic(defaultOAuth2User.getAttribute("picture"));
            user.setName(defaultOAuth2User.getAttribute("name"));
            user.setProviderUserId(defaultOAuth2User.getName());
            user.setProvider(Providers.GOOGLE);
            user.setAbout("This account is created using google.");

        } else if(authorizedClientRegistrationId.equalsIgnoreCase("github")){
            // gitHub attributes
            String email = defaultOAuth2User.getAttribute("email") != null ? defaultOAuth2User.getAttribute("email") : defaultOAuth2User.getAttribute("login") + "@gmail.com";
            String picture = defaultOAuth2User.getAttribute("avatar_url");
            String name = defaultOAuth2User.getAttribute("login");
            String providerUserId = defaultOAuth2User.getName();

            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProviderUserId(providerUserId);
            user.setProvider(Providers.GITHUB);

            user.setAbout("This account is created using github");
        } else{
            log.info("Provider Unknown");
        }

        if (userRepo.findByEmail(user.getEmail()).orElse(null) == null) {
            userRepo.save(user);
            log.info("User saved:" + user.getEmail());
        }

        /*
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        log.info("DefaultOAuth2User Name: {}",defaultOAuth2User.getName());

        defaultOAuth2User.getAttributes().forEach((key, value)->{
            log.info("Key {} => Value {}", key, value);
        });

        log.info(defaultOAuth2User.getAuthorities().toString());

        String email = defaultOAuth2User.getAttribute("email");
        String name = defaultOAuth2User.getAttribute("name");
        String picture = defaultOAuth2User.getAttribute("picture");

        // create user and save in database

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setProfilePic(picture);
        user.setPassword("password");
        user.setUserId(UUID.randomUUID().toString());
        user.setProvider(Providers.GOOGLE);
        user.setEnabled(true);

        user.setEmailVerified(true);
        user.setProviderUserId(defaultOAuth2User.getName());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setAbout("This account is created using google..");

        if (userRepo.findByEmail(email).orElse(null) == null) {
            userRepo.save(user);
            log.info("User saved:" + email);
        }
        */

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/dashboard");
    }
}
