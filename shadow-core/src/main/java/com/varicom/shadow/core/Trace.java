package com.varicom.shadow.core;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author wdw
 *
 */
public class Trace implements Serializable {

	private static final long serialVersionUID = 4753054380679741934L;
	
	//时间戳
	private long timestamp;
	
	//Trace的动作：0：进入 1：返回
	private int action;

	//全局追踪的unique编号
	private String traceId;
	
	//当前调用链路的id
	private String spanId;
	
	//当前调用链路的父id
    private String parentSpanId; 
    
	//当前调用链路的子id
    private String childSpanId; 
    
    public String getChildSpanId() {
		return childSpanId;
	}

	public void setChildSpanId(String childSpanId) {
		this.childSpanId = childSpanId;
	}

	//当前调用链路的名称（一般使用方法名称或url访问的路径标识）
    private String spanName;
    
    //扩展系统级别的参数
    private Map<String, String> sysMap;
    
    //扩展业务级别的参数
    private Map<String, String> diyMap;
    
    private boolean isRpc;
	
	public boolean isRpc() {
		return isRpc;
	}

	public void setRpc(boolean isRpc) {
		this.isRpc = isRpc;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getSpanId() {
		return spanId;
	}

	public void setSpanId(String spanId) {
		this.spanId = spanId;
	}

	public String getParentSpanId() {
		return parentSpanId;
	}

	public void setParentSpanId(String parentSpanId) {
		this.parentSpanId = parentSpanId;
	}

	public String getSpanName() {
		return spanName;
	}

	public void setSpanName(String spanName) {
		this.spanName = spanName;
	}

	public Map<String, String> getSysMap() {
		return sysMap;
	}

	public void setSysMap(Map<String, String> sysMap) {
		this.sysMap = sysMap;
	}

	public Map<String, String> getDiyMap() {
		return diyMap;
	}

	public void setDiyMap(Map<String, String> diyMap) {
		this.diyMap = diyMap;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	/**
	 * to json
	 * @return
	 */
    public String toJson()
    {
    	return JSON.toJSONString(this);
    }

    /**
     * to bean
     * @param json
     * @return
     */
    public Trace toBean(String json)
    {
    	return JSON.parseObject(json, Trace.class);
    }
    
    
//    public static void main(String[] args) {
//    	Trace trace = new Trace();
//    	
//    	trace.setTraceId("00123456789");
//    	trace.setSpanId("0.1");
//    	trace.setParentSpanId("0");
//    	trace.setSpanName("/api/user/query");
//    	trace.setAction(0);
//    	trace.setTimestamp(System.currentTimeMillis());
//    	
//    	Map<String, String> sysMap = new HashMap<String, String>();
//    	sysMap.put("ip", "172.16.1.4");
//    	sysMap.put("service", "user center");
//    	
//    	trace.setSysMap(sysMap);
//    	
//    	System.out.println(trace.toJson());
//	}
}
