package com.ecobank.ecounion.util;

import com.ecobank.ecounion.model.User;
import com.ecobank.ecounion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

public class SecurityUtil {

    private SecurityUtil() {

    }

    // Get currently logged-in user from spring context
  //  public static User getCurrentAccount() {
 //       Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  //      return (User) principal;
//    }

    // This method retrieves the currently authenticated user
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // or throw an exception
        }
        return authentication.getName(); // Get the username (email in this case);
    }
}

