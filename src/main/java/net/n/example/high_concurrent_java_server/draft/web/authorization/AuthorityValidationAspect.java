package net.n.example.high_concurrent_java_server.draft.web.authorization;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Aspect
public class AuthorityValidationAspect {

    private final HttpServletRequest request;

    public AuthorityValidationAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Before("@annotation(RequiresAuthority)")
    public void validateAuthority(JoinPoint joinPoint) {
        // Get the current Principal
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            throw new SecurityException("Unauthorized: No principal found");
        }

        // Fetch the required authorities from the annotation
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresAuthority requiresAuthority = method.getAnnotation(RequiresAuthority.class);
        List<String> requiredAuthorities = Arrays.asList(requiresAuthority.value());

        // Validate against the user's authorities (implement your logic to fetch user authorities)
        List<String> userAuthorities = getUserAuthorities(principal);

        if (!userAuthorities.containsAll(requiredAuthorities)) {
            throw new SecurityException("Forbidden: Insufficient permissions");
        }
    }

    // Dummy implementation: Replace with actual authority fetching logic
    private List<String> getUserAuthorities(Principal principal) {
        // Example: If the principal's name is "admin", return "ADMIN" authority
        if ("admin".equals(principal.getName())) {
            return List.of("ADMIN", "USER");
        } else if ("test001".equals(principal.getName())) {
            return List.of("USER");
        }
        return List.of(); // No authorities for unknown users
    }
}
