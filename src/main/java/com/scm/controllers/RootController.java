package com.scm.controllers;

import com.scm.entities.User;
import com.scm.helper.Helper;
import com.scm.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice
public class RootController {

    @Autowired
    UserService userService;

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication){
        if(authentication == null){
            return;
        }
        log.info("Adding logged in user information to the model");
        String emailOfLoggedInUser = Helper.getEmailOfLoggedInUser(authentication);
        log.info("Email Of Logged In User: {}", emailOfLoggedInUser);

        User user = userService.getUserByEmail(emailOfLoggedInUser);

        log.info("Logged In user: {}",user);

        model.addAttribute("user", user);
    }
}
