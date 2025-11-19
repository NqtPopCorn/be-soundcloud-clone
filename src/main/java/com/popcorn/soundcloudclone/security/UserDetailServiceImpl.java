package com.popcorn.soundcloudclone.security;

import com.popcorn.soundcloudclone.repository.UserRepository;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.popcorn.soundcloudclone.domain.entity.User;

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
