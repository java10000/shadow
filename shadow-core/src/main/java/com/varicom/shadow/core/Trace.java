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
	
	private long timestamp;
	
	private int action;

	private String traceId;
	
	private String spanId;
	
    private String parentSpanId; 
    
    private String spanName;
    
    private Map<String, String> sysMap;
    
    private Map<String, String> diyMap;
	
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
}
