package com.example.springboot.aspect;

import com.example.springboot.entity.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.example.springboot.controller.UserController.*(..))")
    public void loggingPointCut() {
        // Pointcut for all methods in UserController
    }

    @Around("loggingPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Before method invoked: {}", joinPoint.getSignature());
        logger.info("Request: {}", getRequestDetails(joinPoint.getArgs()));

        Object result = joinPoint.proceed();

        logger.info("After method invoked: {}", joinPoint.getSignature());

        return result;
    }

    @AfterReturning(pointcut = "loggingPointCut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Response: {}", getUserResponse(result));
    }

    private String getRequestDetails(Object[] args) {
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof User) {
                    User user = (User) arg;
                    return getUserResponse(user);
                }
            }
        }
        return "No user data in the request";
    }

    private String getUserResponse(Object result) {
        if (result instanceof User) {
            User user = (User) result;
            return "User [firstName=" + user.getFirstName() + ", lastName=" + user.getLastName()
                    + ", phoneNo=" + user.getPhoneNo() + ", emailId=" + user.getEmailId() + "]";
        }
        return "No user data in the response";
    }
}
