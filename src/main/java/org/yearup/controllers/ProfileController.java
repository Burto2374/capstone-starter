package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
public class ProfileController
{
    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    public ResponseEntity<Profile> getProfile(Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUsername(username);
        Profile profile = profileDao.getByUserId(user.getUserId());
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(@RequestBody Profile profile, Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUsername(username);
        profile.setUserId(user.getUserId());
        profileDao.update(profile);
        return ResponseEntity.noContent().build();
    }
}
