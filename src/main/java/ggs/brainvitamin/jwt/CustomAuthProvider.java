package ggs.brainvitamin.jwt;

import ggs.brainvitamin.src.user.patient.service.CustomUserDetailsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class CustomAuthProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String phoneNumber = authentication.getName();

        UserDetails user = customUserDetailsService.loadUserByUsername(phoneNumber);

        return new UserToken(user.getUsername(), phoneNumber,null, user);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public class UserToken extends UsernamePasswordAuthenticationToken {

        private static final long serialVersionUID = 1L;

        @Getter
        @Setter
        UserDetails user;

        public UserToken(Object principal,
                         Object credentials,
                         Collection<? extends GrantedAuthority> authorityEntities,
                         UserDetails user) {

            super(principal, credentials, user.getAuthorities());
            this.user = user;
        }

        @Override
        public Object getDetails() {
            return user;
        }
    }

}
