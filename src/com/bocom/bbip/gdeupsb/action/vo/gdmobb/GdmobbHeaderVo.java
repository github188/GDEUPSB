
package com.bocom.bbip.gdeupsb.action.vo.gdmobb;

import java.util.HashMap;
import java.util.Map;

/**
 * @category 广东移动
 * @author tanyuan
 * 
 */
public class GdmobbHeaderVo {

    private String category; // <!--01：A接口；02：C接口；03：T接口；06：M接口；08：N接口-->

    private String sub_command; // <!--01：业务请求;02：业务请求应答-->

    private String opcode; // <!--操作码-->

    private String command_status; // <!--命令状态-->

    private String sou_addr; // <!--消息源地址,同步保留-->

    private String des_addr; // <!--消息目的地址，同步保留-->

    private String seq_no; // <!--序列号，同步保留-->

    public Map<String, Object> toMap() {
        Map<String, Object> GdmobbMap = new HashMap<String, Object>();
        GdmobbMap.put("category", getCategory());
        GdmobbMap.put("sub_command", getSub_command());
        GdmobbMap.put("opcode", getOpcode());
        GdmobbMap.put("command_status", getCommand_status());
        GdmobbMap.put("sou_addr", getSou_addr());
        GdmobbMap.put("des_addr", getDes_addr());
        GdmobbMap.put("seq_no", getSeq_no());
        return GdmobbMap;
    }

	/**
	 * @return The category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param The category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return The sub_command
	 */
	public String getSub_command() {
		return sub_command;
	}

	/**
	 * @param The sub_command
	 */
	public void setSub_command(String sub_command) {
		this.sub_command = sub_command;
	}

	/**
	 * @return The opcode
	 */
	public String getOpcode() {
		return opcode;
	}

	/**
	 * @param The opcode
	 */
	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}

	/**
	 * @return The command_status
	 */
	public String getCommand_status() {
		return command_status;
	}

	/**
	 * @param The command_status
	 */
	public void setCommand_status(String command_status) {
		this.command_status = command_status;
	}

	/**
	 * @return The sou_addr
	 */
	public String getSou_addr() {
		return sou_addr;
	}

	/**
	 * @param The sou_addr
	 */
	public void setSou_addr(String sou_addr) {
		this.sou_addr = sou_addr;
	}

	/**
	 * @return The des_addr
	 */
	public String getDes_addr() {
		return des_addr;
	}

	/**
	 * @param The des_addr
	 */
	public void setDes_addr(String des_addr) {
		this.des_addr = des_addr;
	}

	/**
	 * @return The seq_no
	 */
	public String getSeq_no() {
		return seq_no;
	}

	/**
	 * @param The seq_no
	 */
	public void setSeq_no(String seq_no) {
		this.seq_no = seq_no;
	}

 

   
}
