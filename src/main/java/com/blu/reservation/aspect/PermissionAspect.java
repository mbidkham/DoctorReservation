package com.blu.reservation.aspect;

import com.blu.reservation.model.PanelUser;
import com.blu.reservation.model.repository.UserRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.management.relation.RoleNotFoundException;
import java.lang.reflect.Method;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class PermissionAspect {

    private final UserRepository userRepository;

    public PermissionAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Around("@annotation(com.blu.reservation.aspect.CheckAccess) && args(userId,..)")
    public Object checkPermission(ProceedingJoinPoint pjp, Integer userId) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        PanelUser panelUser = userRepository.findById(userId).orElseThrow(RoleNotFoundException::new);
        CheckAccess access = method.getAnnotation(CheckAccess.class);
        String[] allowedPermissions = access.value();
        for (String requiredPermission : allowedPermissions) {
            if (panelUser.getRole().name().equals(requiredPermission)) {
                return pjp.proceed();
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
