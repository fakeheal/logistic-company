package nbu.team11.services;

import nbu.team11.entities.enums.PositionType;
import nbu.team11.entities.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CustomPermissionEvaluator {

    public boolean hasRoleAndPosition(Authentication authentication, Role role, PositionType positionType) {
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals(role.toString()))
                && userDetails.getPositionType() == positionType;
    }

    public boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }
}