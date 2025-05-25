package nbu.team11.services;

import nbu.team11.entities.enums.PositionType;
import nbu.team11.entities.enums.Role;
import nbu.team11.services.customImplementations.models.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CustomPermissionEvaluator {

    private boolean hasRoleAndPosition(Authentication authentication, Role role, PositionType positionType) {
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals(role.toString()))
                && userDetails.getUserDto().getEmployeeDto().getPosition() == positionType;
    }

    public boolean hasRole(Authentication authentication, String roleName) {
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        try {
            Role roleEnum = Role.valueOf(roleName);
            return userDetails.getUserDto().getRole() == roleEnum;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAdmin(Authentication authentication) {
        return this.hasRoleAndPosition(authentication, Role.EMPLOYEE, PositionType.ADMIN);
    }
}