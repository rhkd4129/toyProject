package com.toyproject.backend.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class MethodLoggingAspect {

    // 포인트컷: 어떤 메서드에 적용할지 범위 정의 (재사용을 위해 별도 선언)
    @Pointcut("""
            execution(* com.toyproject.backend.domain..*.*(..)) ||
            execution(* com.toyproject.backend.storage..*.*(..)) ||
            execution(* com.toyproject.backend.redis..*.*(..)) ||
            execution(* com.toyproject.backend.emitter..*.*(..))
            """)
    private void applicationLayer() {}

    // ─── @Before: 메서드 실행 직전 ───────────────────────────────
    // JoinPoint = 지금 호출되는 메서드의 정보 꾸러미
    //   .getTarget()              → 실제 Bean 인스턴스 (PdfService 객체)
    //   .getSignature().getName() → 메서드 이름 ("createPdf")
    //   .getArgs()                → 전달된 인자값 배열
    @Before("applicationLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String className  = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args     = joinPoint.getArgs();

        log.info("[진입] {}.{}() | args={}", className, methodName, Arrays.toString(args));
    }

    // ─── @AfterReturning: 메서드가 정상적으로 반환된 직후 ─────────
    // returnValue = 메서드가 실제로 반환한 값
    @AfterReturning(pointcut = "applicationLayer()", returning = "returnValue")
    public void logAfterReturning(JoinPoint joinPoint, Object returnValue) {
        String className  = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("[반환] {}.{}() | result={}", className, methodName, returnValue);
    }

//    // ─── @AfterThrowing: 예외가 터진 직후 ────────────────────────
//    // ex = 던져진 예외 객체
//    @AfterThrowing(pointcut = "applicationLayer()", throwing = "ex")
//    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
//        String className  = joinPoint.getTarget().getClass().getSimpleName();
//        String methodName = joinPoint.getSignature().getName();
//        log.error("[THROW]  {}.{}() | exception={} message={}", className, methodName,
//                ex.getClass().getSimpleName(), ex.getMessage());
//    }
}
