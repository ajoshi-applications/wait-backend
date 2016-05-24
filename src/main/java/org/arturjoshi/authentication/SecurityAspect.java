package org.arturjoshi.authentication;

import org.arturjoshi.users.domain.User;
import org.arturjoshi.users.repository.UserRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    @Autowired
    private UserAuthenticationManager userAuthenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Before("execution(* org.arturjoshi.users.controller.UserController.*(..))" +
            "|| execution(* org.arturjoshi.tracking.controller.TrackingController.*(..))")
    public void isLegal(JoinPoint joinPoint) {
        Long id = (Long) joinPoint.getArgs()[0];
        User user = userRepository.findOne(id);
        if(!userAuthenticationManager.isLegal(user)) {
            throw new AccessDeniedException("Access is denied");
        }
    }
}
