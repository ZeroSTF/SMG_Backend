package tn.zeros.smg.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {
     @Around("execution(* tn.zeros.smg.services.VenteService.retrieveAllLignesByPiedFact(..))")
     public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
         long start = System.currentTimeMillis();
         Object proceed = joinPoint.proceed();
         long executionTime = System.currentTimeMillis() - start;
         log.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");
         return proceed;
     }
}
