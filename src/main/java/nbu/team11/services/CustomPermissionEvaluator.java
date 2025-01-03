package nbu.team11.services;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CustomPermissionEvaluator {

    public boolean hasRoleAndPosition(Authentication authentication, String role, String positionType) {
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role)) &&
                userDetails.getPositionType().equals(positionType);
    }
}