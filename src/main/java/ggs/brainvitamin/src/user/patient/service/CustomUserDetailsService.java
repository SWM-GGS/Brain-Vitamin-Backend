package ggs.brainvitamin.src.user.patient.service;

import ggs.brainvitamin.config.BaseException;
import ggs.brainvitamin.config.BaseResponseStatus;
import ggs.brainvitamin.config.Status;
import ggs.brainvitamin.src.user.entity.UserEntity;
import ggs.brainvitamin.src.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws BaseException {
        return userRepository.findOneWithAuthoritiesByPhoneNumber(phoneNumber)
                .map(userEntity -> createUser(phoneNumber, userEntity))
                .orElseThrow(() -> new BaseException(BaseResponseStatus.PHONENUMBER_NOT_EXISTS));
    }

    private User createUser(String phoneNumber, UserEntity userEntity) {

        if (userEntity.getStatus() == Status.INACTIVE) {
            throw new RuntimeException(phoneNumber + "-> 활성화되어 있지 않습니다");
        }

        List<GrantedAuthority> grantedAuthorities = userEntity.getAuthorities().stream()
                .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthorityName()))
                .collect(Collectors.toList());

        return new User(userEntity.getId().toString(), userEntity.getPhoneNumber(), grantedAuthorities);
    }
}
