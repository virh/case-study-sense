package virh.sense.trade.aspect;

import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecuteTimeAspect {
	
	private static Logger log = LoggerFactory.getLogger(LogExecuteTimeAspect.class);

	@Around("@annotation(virh.sense.trade.annotation.LogExecutionTime)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		MDC.put("uuid", UUID.randomUUID().toString());
		long start = System.currentTimeMillis();
		Object proceed = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - start;
		log.debug(joinPoint.getSignature() + " executed in " + executionTime + " ms");
		return proceed;
	}
	
}
