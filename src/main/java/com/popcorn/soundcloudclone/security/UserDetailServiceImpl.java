package com.popcorn.soundcloudclone.security;

import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.popcorn.soundcloudclone.domain.entity.User;
import com.popcorn.soundcloudclone.domain.repository.UserRepository;

/**
 * Spring Security UserDetailsService implementation
 */
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new MyUserDetails(appUser);
    }
}
