package com.bocom.bbip.gdeupsb.action.vo.gduncb;

import java.util.HashMap;
import java.util.Map;

/**
 * @category 广东联动
 * @author tandun
 *
 */
public class GduncbHeaderVo{
	private String  msg_sender;//消息发送方代码
	private String  msg_receiver;//消息直接接收方代码
	private String  trans_ido;//服务调用方流水号
	private String  process_time;//服务处理时间
	private String  eparchy_code;//地市编码
	private String  channel_id;//渠道编码
	private String  oper_id;//操作员工号
	private String  service_name;//服务名称
	private String  operate_name;//操作名称
	private String  rsp_code;//应答/错误代码
	private String  rsp_desc;//应答/错误描述
	private String  test_flag;//测试标记
	
	public Map<String,Object> toMap(){
		Map<String,Object> GduncaMap = new HashMap<String,Object>();
		GduncaMap.put("msg_sender", getMsg_sender());
		GduncaMap.put("msg_receiver", getMsg_receiver());
		GduncaMap.put("trans_ido", getTrans_ido());
		GduncaMap.put("process_time", getProcess_time());
		GduncaMap.put("eparchy_code", getEparchy_code());
		GduncaMap.put("channel_id", getChannel_id());
		GduncaMap.put("service_name", getService_name());
		GduncaMap.put("operate_name", getOperate_name());
		GduncaMap.put("oper_id", getOper_id());
		GduncaMap.put("rsp_code", getRsp_code());
		GduncaMap.put("rsp_desc",getRsp_desc());
		GduncaMap.put("test_flag",getTest_flag());
		return GduncaMap;
	}

	/**
	 * @return The msg_sender
	 */
	public String getMsg_sender() {
		return msg_sender;
	}

	/**
	 * @param The msg_sender
	 */
	public void setMsg_sender(String msg_sender) {
		this.msg_sender = msg_sender;
	}

	/**
	 * @return The msg_receiver
	 */
	public String getMsg_receiver() {
		return msg_receiver;
	}

	/**
	 * @param The msg_receiver
	 */
	public void setMsg_receiver(String msg_receiver) {
		this.msg_receiver = msg_receiver;
	}

	/**
	 * @return The trans_ido
	 */
	public String getTrans_ido() {
		return trans_ido;
	}

	/**
	 * @param The trans_ido
	 */
	public void setTrans_ido(String trans_ido) {
		this.trans_ido = trans_ido;
	}

	/**
	 * @return The process_time
	 */
	public String getProcess_time() {
		return process_time;
	}

	/**
	 * @param The process_time
	 */
	public void setProcess_time(String process_time) {
		this.process_time = process_time;
	}

	/**
	 * @return The eparchy_code
	 */
	public String getEparchy_code() {
		return eparchy_code;
	}

	/**
	 * @param The eparchy_code
	 */
	public void setEparchy_code(String eparchy_code) {
		this.eparchy_code = eparchy_code;
	}

	/**
	 * @return The channel_id
	 */
	public String getChannel_id() {
		return channel_id;
	}

	/**
	 * @param The channel_id
	 */
	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	/**
	 * @return The oper_id
	 */
	public String getOper_id() {
		return oper_id;
	}

	/**
	 * @param The oper_id
	 */
	public void setOper_id(String oper_id) {
		this.oper_id = oper_id;
	}

	/**
	 * @return The service_name
	 */
	public String getService_name() {
		return service_name;
	}

	/**
	 * @param The service_name
	 */
	public void setService_name(String service_name) {
		this.service_name = service_name;
	}

	/**
	 * @return The operate_name
	 */
	public String getOperate_name() {
		return operate_name;
	}

	/**
	 * @param The operate_name
	 */
	public void setOperate_name(String operate_name) {
		this.operate_name = operate_name;
	}

	/**
	 * @return The rsp_code
	 */
	public String getRsp_code() {
		return rsp_code;
	}

	/**
	 * @param The rsp_code
	 */
	public void setRsp_code(String rsp_code) {
		this.rsp_code = rsp_code;
	}

	/**
	 * @return The rsp_desc
	 */
	public String getRsp_desc() {
		return rsp_desc;
	}

	/**
	 * @param The rsp_desc
	 */
	public void setRsp_desc(String rsp_desc) {
		this.rsp_desc = rsp_desc;
	}

	/**
	 * @return The test_flag
	 */
	public String getTest_flag() {
		return test_flag;
	}

	/**
	 * @param The test_flag
	 */
	public void setTest_flag(String test_flag) {
		this.test_flag = test_flag;
	}
	
	
	
	

	
}
