package com.example.p2k._core.security;

import com.example.p2k.user.UserRepository;
import lombok.RequiredArgsConstructor;
import com.example.p2k.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;

    //로그인할 때 들어온 username으로 DB에서 정보 찾기
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return null;
        } else {
            User userPS = user.get();
            return new CustomUserDetails(userPS);
        }
    }
}
