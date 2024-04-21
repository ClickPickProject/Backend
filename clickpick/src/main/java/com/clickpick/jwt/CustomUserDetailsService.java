package com.clickpick.jwt;

import com.clickpick.domain.Admin;
import com.clickpick.domain.User;
import com.clickpick.repository.AdminRepository;
import com.clickpick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> result = userRepository.findById(username);
        if(result.isPresent()){
            JWTUserDto jwtUserDto = new JWTUserDto(result.get().getId(),result.get().getNickname(),result.get().getPassword(),result.get().getStatus().toString());
            return new CustomUserDetails(jwtUserDto);
        }
        else{
            Optional<Admin> adminResult = adminRepository.findById(username);
            if(adminResult.isPresent()){
                JWTUserDto jwtAdminDto = new JWTUserDto(adminResult.get().getId(),"ADMIN",adminResult.get().getPassword(),"ADMIN");
                return new CustomUserDetails(jwtAdminDto);
            }
        }

        //return null;
        throw new UsernameNotFoundException("잘못된 아이디 또는 비밀번호 입니다.");
    }

}
