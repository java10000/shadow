package com.varicom.shadow.aop.aspect;

import static com.varicom.shadow.aop.constant.AopConstant.DTS_PARENT_SPAN_ID;
import static com.varicom.shadow.aop.constant.AopConstant.DTS_SERVICE_IP;
import static com.varicom.shadow.aop.constant.AopConstant.DTS_SERVICE_NAME;
import static com.varicom.shadow.aop.constant.AopConstant.DTS_SPAN_ID;
import static com.varicom.shadow.aop.constant.AopConstant.DTS_TRACE_ID;
import static com.varicom.shadow.aop.constant.AopConstant.one;
import static com.varicom.shadow.aop.constant.AopConstant.point;
import static com.varicom.shadow.aop.constant.AopConstant.root;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.varicom.shadow.core.Trace;
import com.varicom.shadow.core.util.NetworkUtil;
import com.varicom.shadow.logger.LoggerComponent;

//@Aspect
public class AuditAspect {

	private static Logger logger = LoggerFactory.getLogger(AuditAspect.class);

	private ThreadLocal<Trace> traceLocal = new ThreadLocal<Trace>();

	private Properties props = System.getProperties(); //系统属性
	
	@Autowired
	private LoggerComponent loggerComponent;

	// 控制指定目录和固定注解的方法
	// @Pointcut(value="execution(@com.varicom.shadow.aop.annotations.PerfLog * com.varicom..*(..))")
//	@Pointcut(value = "execution(* com.varicom.shadow.aop.demo.controller..*(..))")
	public void traceTargets() {
	}

	// 指定传送参数的方法
//	@Pointcut(value = "execution(private * com.varicom.api.core.DefaultVaricomClient._do*(..))")
	public void traceTrans() {
	}

//	@Before(value = "traceTargets()")
	public void beforeTraceTargets(JoinPoint joinpoint) {
		try {
			logger.debug("Before =============");
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes()).getRequest();
			Trace trace = this.getTraceFromJoinPoint(request);

			if (null != trace) {
				traceLocal.remove();
				traceLocal.set(trace);
				// 日志写入
				loggerComponent.write(trace.toJson());
			}
			logger.debug("Before =============");
		} catch (Exception e) {
			logger.error("trace before logger occur error: ", e);
		}
	}

//	@Before(value = "traceTrans()")
	public void beforeTraceTrans(JoinPoint joinpoint) {
		try {
			logger.debug("fill param =============");
			Map<String, String> map = this.fillTraceParam();
			this.fillTraceParamToHeaderMap(joinpoint, map);
			logger.debug("fill param =============");
		} catch (Exception e) {
			logger.error("trace trans param occur error: ", e);
		}
	}

//	@After(value = "traceTargets()")
	public void afterTraceTargets(JoinPoint joinpoint) {
		try {
			logger.debug("After ======================");
			Trace trace = traceLocal.get();
			if (null != trace) {
				trace.setAction(one);
				trace.setTimestamp(System.currentTimeMillis());
				// 日志写入
				loggerComponent.write(trace.toJson());
				traceLocal.remove();
			}
			logger.debug("After ======================");
		} catch (Exception e) {
			logger.error("trace after logger occur error: ", e);
		}
	}

	/************************** 各种私有方法 **********************************/

	
	/**
	 * 填充rpc传输过程中的参数--http header
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private void fillTraceParamToHttpHeader(Map<String, String> map) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		if (null != request) {
			this.fillParamToRequest(request, map);
		}
	}

	/**
	 * 填充rpc传输过程中的参数--header map
	 * 
	 * @return
	 */
	private void fillTraceParamToHeaderMap(JoinPoint joinpoint,
			Map<String, String> map) {
		Object[] args = joinpoint.getArgs();
		if (null != args && args.length > 0
				&& args[args.length - 1] instanceof Map) {
			Map<String, String> headerMap = (Map<String, String>) args[args.length - 1];
			headerMap.putAll(map);
		}
	}

	/**
	 * 填充rpc传输过程中的参数
	 * 
	 * @return
	 */
	private Map<String, String> fillTraceParam() {
		Map<String, String> map = new HashMap<String, String>();

		Trace trace = traceLocal.get();
		if (null != trace) {
			map.put(DTS_TRACE_ID, trace.getTraceId());
			map.put(DTS_SPAN_ID, trace.getSpanId());
			map.put(DTS_PARENT_SPAN_ID, trace.getParentSpanId());
		}

		return map;
	}

	/**
	 * 填充自定义参数至request header中，进行跨rpc传输
	 * 
	 * @param request
	 * @param map
	 */
	private HttpServletRequest fillParamToRequest(HttpServletRequest request,
			Map<String, String> map) {
		HeaderMapRequestWrapper wrapper = new HeaderMapRequestWrapper(request);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			System.out.println("key= " + entry.getKey() + " and value= "
					+ entry.getValue());
			wrapper.addHeader(entry.getKey(), entry.getValue());
		}

		return wrapper;
	}

	/**
	 * 根据JoinPoint进行参数整合
	 * 
	 * @param joinpoint
	 * @return
	 */
	private Trace getTraceFromJoinPoint(HttpServletRequest request) {
		Trace trace = new Trace();
		Trace traceTemp = traceLocal.get();

		if (null == traceTemp || StringUtils.isBlank(traceTemp.getTraceId())) {
			if (null != request) {
				String traceId = request.getHeader(DTS_TRACE_ID);
				String spanName = this.getSpanName(request);
				String spanId = request.getHeader(DTS_SPAN_ID);
				String parentSpanId = request.getHeader(DTS_PARENT_SPAN_ID);

				trace.setTraceId(traceId);
				trace.setSpanId(spanId);
				trace.setParentSpanId(parentSpanId);
				trace.setSpanName(spanName);

				if (StringUtils.isBlank(parentSpanId)) {
					parentSpanId = root;
					trace.setSpanId(root);
					trace.setParentSpanId(root);
				}

				// 构建新的span id
				this.genNewSpanId(trace);
			}
		} else {
			BeanUtils.copyProperties(traceTemp, trace);
			trace.setSpanName(this.getSpanName(request));
			this.genNewSpanId(trace);
		}

		long time = System.currentTimeMillis();
		trace.setTimestamp(time);
		trace.setAction(0);
		trace.setSysMap(this.genSysMap());
		
		return StringUtils.isNotBlank(trace.getTraceId()) ? trace : null;
	}

	/**
	 * 构建应用级参数
	 * @return
	 */
	private Map<String, String> genSysMap()
	{
		Map<String, String> sysMap = new HashMap<String, String>();
		sysMap.put(DTS_SERVICE_NAME, props.getProperty(DTS_SERVICE_NAME));
		sysMap.put(DTS_SERVICE_IP, NetworkUtil.getLocalIp());
		return sysMap;
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
	 * 
	 * @param spanId
	 * @return
	 */
	private void genNewSpanId(Trace trace) {
		String spanId = trace.getSpanId();

		if (root.equals(spanId)) {
			spanId = root + point + one;
		} else {
			if (trace.getParentSpanId().length() == spanId.length()) {
				spanId = trace.getSpanId() + point + one;
			} else {
				String lastSpanNum = StringUtils.substringAfterLast(
						trace.getSpanId(), point);
				spanId = trace.getSpanId() + point
						+ (Integer.valueOf(lastSpanNum) + one);
			}
		}

		trace.setSpanId(spanId);
	}

	/**
	 * 对JoinPoint的参数进行类型判断
	 * 
	 * @param obs
	 * @return
	 */
	private HttpServletRequest getHttpServletRequest(JoinPoint joinpoint) {
		Object[] obs = joinpoint.getArgs();
		for (Object ob : obs) {
			if (ob instanceof HttpServletRequest) {
				HttpServletRequest hsr = (HttpServletRequest) ob;
				if (StringUtils.isNotBlank(hsr.getHeader(DTS_TRACE_ID))) {
					return (HttpServletRequest) ob;
				}
			}
		}

		return null;
	}
}