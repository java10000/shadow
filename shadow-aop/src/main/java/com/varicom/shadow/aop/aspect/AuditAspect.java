package com.varicom.shadow.aop.aspect;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.varicom.shadow.core.Trace;
import com.varicom.shadow.logger.LoggerComponent;

@Aspect
public class AuditAspect {

	private static final String root = "0";
	
	private static final int one = 1;
	
	private static final String point = ".";
	
    private static Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    
    private ThreadLocal<Trace> traceLocal = new ThreadLocal<Trace>();
    
    @Autowired
    private LoggerComponent loggerComponent;
    
    
    
//    控制指定目录和固定注解的方法
//    @Pointcut(value="execution(@com.varicom.shadow.aop.annotations.PerfLog * com.varicom..*(..))")
    @Pointcut(value="execution(* com.varicom.shadow.aop.demo.controller..*(..))")
    public void performanceTargets(){}
   
    @Before(value="performanceTargets()")
    public void before(JoinPoint joinpoint) {
    	logger.info("Before =============");
    	
    	Trace trace = this.getTraceFromJoinPoint(joinpoint);
    	if (null != trace) {
			traceLocal.remove();
			traceLocal.set(trace);
			// 日志写入
			loggerComponent.write(trace.toJson());
		}
    	
    	logger.info("Before =============");
    }

//    @Around(value="execution(* com.varicom.api.core.DefaultVaricomClient.*(..))")
    public Object logPerformanceStats(ProceedingJoinPoint joinpoint) {
        try {
        	Map<String, String> map = this.fillTraceParam();
        	
        	
            long start = System.nanoTime();
            Object result = joinpoint.proceed();
            long end = System.nanoTime();
            logger.info(String.format("%s took %d ns", joinpoint.getSignature().toShortString(), (end - start)));
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
   
    @After(value="performanceTargets()")
    public void after(JoinPoint joinpoint) {
    	logger.info("After ======================");
    	
    	Trace trace = traceLocal.get();
    	trace.setAction(one);
    	trace.setTimestamp(System.currentTimeMillis());
    	traceLocal.remove();
		// 日志写入
		loggerComponent.write(trace.toJson());
		
    	logger.info("After ======================");
    }    
    
    
    /**
     * 填充rpc传输过程中的参数
     * @return
     */
    private Map<String, String> fillTraceParam()
    {
    	Map<String, String> map = new HashMap<String, String>();
    	
    	Trace trace = traceLocal.get();
    	map.put("traceId", trace.getTraceId());
    	map.put("spanId", trace.getSpanId());
    	map.put("parentSpanId", trace.getParentSpanId());
    	
    	return map;
    }
    
    
    /**
     * 根据JoinPoint进行参数整合
     * @param joinpoint
     * @return
     */
    private Trace getTraceFromJoinPoint(JoinPoint joinpoint)
    {	
    	Trace trace = new Trace();
    	HttpServletRequest request = this.getHttpServletRequest(joinpoint);
    	
    	if (StringUtils.isBlank(traceLocal.get().getTraceId())) 
    	{
    		if (null != request) 
    		{
    			String traceId = request.getHeader("traceId");

    			String spanName = this.getSpanName(request);
    			String spanId = request.getHeader("spanId");
    			String parentSpanId = request.getHeader("parentSpanId");
    			
    			trace.setTraceId(traceId);
    			trace.setSpanId(spanId);
    			trace.setParentSpanId(parentSpanId);
    			trace.setSpanName(spanName);
    			
    			if (StringUtils.isBlank(parentSpanId)) 
    			{
					parentSpanId = root;
					trace.setSpanId(root);
					trace.setParentSpanId(root);
				}
    			
    			// 构建新的span id
    			this.genNewSpanId(trace);
			}
		}
    	else 
    	{
    		BeanUtils.copyProperties(traceLocal.get(), trace);
    		trace.setSpanName(this.getSpanName(request));
    		this.genNewSpanId(trace);
		}
    	
    	long time = System.currentTimeMillis();
    	trace.setTimestamp(time);
    	trace.setAction(0);
    	
    	return StringUtils.isNotBlank(trace.getTraceId()) ? trace : null;
    }
    
    /**
     * 
     * @param request
     * @return
     */
    private String getSpanName(HttpServletRequest request) {
    	return request.getRequestURI();   
    }
    
    
    /**
     * 生成新的span id
     * @param spanId
     * @return
     */
    private void genNewSpanId(Trace trace)
    {
    	String spanId = trace.getSpanId();
    	
    	if (root.equals(spanId)) 
    	{
			spanId = root + point + one;
		}
    	else 
    	{
    		if (trace.getParentSpanId().length() == spanId.length()) 
    		{
    			spanId = trace.getSpanId() + point + one;
			}
    		else
    		{
    			String lastSpanNum = StringUtils.substringAfterLast(trace.getSpanId(), point);
    			spanId = trace.getSpanId() + point + (Integer.valueOf(lastSpanNum) + one);
			}
		}
    	
    	trace.setSpanId(spanId);
    }
    
    
    /**
     * 对JoinPoint的参数进行类型判断
     * @param obs
     * @return
     */
    private HttpServletRequest getHttpServletRequest(JoinPoint joinpoint)
    {
    	Object[] obs = joinpoint.getArgs();
    	for (Object ob : obs) {
    		if (ob instanceof HttpServletRequest) {
    			HttpServletRequest hsr = (HttpServletRequest) ob;
				if (StringUtils.isNotBlank(hsr.getHeader("traceId"))) {
	    			return (HttpServletRequest) ob;
	    		}
    		}
		}
    	
    	return null;
    }
}