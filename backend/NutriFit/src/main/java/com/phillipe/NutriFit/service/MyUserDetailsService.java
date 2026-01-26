package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.dao.UserRepo;
import com.phillipe.NutriFit.model.User;
import com.phillipe.NutriFit.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return new UserPrincipal(user);
    }
}
