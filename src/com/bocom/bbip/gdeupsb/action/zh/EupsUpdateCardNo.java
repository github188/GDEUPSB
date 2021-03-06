package com.bocom.bbip.gdeupsb.action.zh;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.ZHActNoInf;
import com.bocom.bbip.gdeupsb.repository.ZHActNoInfRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;
/** 481210 珠海文件代收付系统对私客户代扣账号/卡号变更*/
public class EupsUpdateCardNo  extends BaseAction {
	private static final Log logger = LogFactory.getLog(EupsUpdateCardNo.class);
	private static String fileName = "";
	private static String year = "";
	private static String month = "";
	private static String day = "";
	private static String time = "";
	private String func;
	private String actName;
	private String oldActNO;
	private String ActNo;
	private String ActTyp;
	private String cusId;
	private String tlrId;
	private String Sup1Id;
	private String opn;

	public void process(Context context)throws CoreException,CoreRuntimeException,IOException{
		logger.info(" start!!");
		/**操作类型 0-查询  1-修改  2-新增  3-删除*/
		 func=ContextUtils.assertDataHasLengthAndGetNNR(context, "Func", ErrorCodes.EUPS_FIELD_EMPTY);
		 actName=ContextUtils.assertDataHasLengthAndGetNNR(context, "ActNam", ErrorCodes.EUPS_FIELD_EMPTY);
		 oldActNO=ContextUtils.assertDataHasLengthAndGetNNR(context, "OldAct", ErrorCodes.EUPS_FIELD_EMPTY);
		 ActNo=ContextUtils.assertDataHasLengthAndGetNNR(context, "ActNo", ErrorCodes.EUPS_FIELD_EMPTY);
		/** 客户类型*/
		 ActTyp=ContextUtils.assertDataHasLengthAndGetNNR(context, "ActTyp", ErrorCodes.EUPS_FIELD_EMPTY);
		final String br=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.BR, ErrorCodes.EUPS_FIELD_EMPTY );
		final String actDat=DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		/** 客户号*/
		 cusId=ContextUtils.assertDataHasLengthAndGetNNR(context, "CusId", ErrorCodes.EUPS_FIELD_EMPTY );
		 tlrId=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.TELLER, ErrorCodes.EUPS_FIELD_EMPTY );
		 Sup1Id=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.SUPER_TELLER1_ID, ErrorCodes.EUPS_FIELD_EMPTY );
         opn=context.getData("OpnNod");
		year = actDat.substring(0, 4);
		month = actDat.substring(4, 6);
		day = actDat.substring(6, 8);
		time = DateUtils.formatAsHHmmss(new Date());
		fileName = "fbp_" + tlrId + "hk_" + time + ".txt";
		context.setData("FleNam", fileName);
		if (!func.equalsIgnoreCase(GDConstants.GD_ZH_OPERATE_QUERY)) {
			/** 需要发送到BBOS的文件 */
			File file = FileUtils.getFile( "D:\\upd.txt");
			Assert.isFalse(null == file, ErrorCodes.EUPS_FILE_NOT_EXIST);
			/** 业务处理 */
			List<String> lines = operate(context, func);
				/** 写入文件 */
			Assert.isNotEmpty(lines, ErrorCodes.EUPS_FILE_NOT_EXIST);
			FileUtils.writeLines(file, GDConstants.CHARSET_ENCODING_GBK,lines);
				
			
		}else{
			 query(context);
		}
		logger.info("成功!!");
	}

	private List<String> operate(Context context,final String operator) throws CoreException {
		List<String>ret=CollectionUtils.createList();
		if (operator.equalsIgnoreCase(GDConstants.GD_ZH_OPERATE_QUERY)) {
			ret = query(context);
		} else if (operator.equalsIgnoreCase(GDConstants.GD_ZH_OPERATE_MODIFY)) {
			ret = update(context);
		} else if (operator.equalsIgnoreCase(GDConstants.GD_ZH_OPERATE_ADD)) {
			ret = add(context);
		} else if (operator.equalsIgnoreCase(GDConstants.GD_ZH_OPERATE_DELETE)) {
			ret = delete(context);
		} else {
			throw new CoreException(ErrorCodes.EUPS_OPR_TYP_ERR);
		}
		return ret;
	}

	private List<String> query(Context context)throws CoreException {
		Map<String,String> map=new HashMap<String,String>();
		map.put("OldAct", (String)context.getData("OldAct"));
		List<ZHActNoInf> list=get(ZHActNoInfRepository.class).
		queryNewByOld(map);
		Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
		context.getDataMapDirectly().putAll(BeanUtils.toFlatMap(list.get(0)));
		return null;
	}

	private List<String> update(Context context) throws CoreException {
		List<String> ret = CollectionUtils.createList();
		ret.addAll(fileTemplate());
		Map<String,String> map=new HashMap<String,String>();
		map.put("OldAct", (String)context.getData("OldAct"));
		map.put("ActNo", (String)context.getData("ActNo"));
		get(ZHActNoInfRepository.class).updateInfo(map);
		ret.add("               "+Sup1Id+"                   "+tlrId);
		return ret;
	}
	private List<String> add(Context context) throws CoreException {
		List<String> ret = CollectionUtils.createList();
		ret.addAll(fileTemplate());
		Map<String,String> map=new HashMap<String,String>();
		map.put("OldAct", (String)context.getData("OldAct"));
		List<ZHActNoInf> list=get(ZHActNoInfRepository.class).
		queryNewByOld(map);
		Assert.isEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
		ZHActNoInf info=ContextUtils.getDataAsObject(context, ZHActNoInf.class);
		get(ZHActNoInfRepository.class).insertInfo(info);
		ret.add("            代缴费业务中旧帐号视同为新帐号："+ActNo);
		ret.add("            客户签名：");
		ret.add("               "+Sup1Id+"                   "+tlrId);
		return ret;
	}

	private List<String> delete(Context context) throws CoreException {
		List<String> ret = CollectionUtils.createList();
		ret.addAll(fileTemplate());
		Map<String,String> map=new HashMap<String,String>();
		map.put("OldAct", (String)context.getData("OldAct"));
		map.put("ActNo", (String)context.getData("ActNo"));
		get(ZHActNoInfRepository.class).deleteInfo(map);
		ret.add("            代缴费业务中旧帐号不再视同为新帐号："+ActNo);
		ret.add("            客户签名：");
		ret.add("               "+Sup1Id+"                   "+tlrId);
		return ret;
	}
	private List<String>fileTemplate(){
		List <String>list=CollectionUtils.createList();		
		list.add("                                          新旧帐号对照管理");
		list.add("\n\n");
		list.add("                                    "+year+"  "+month+"  "+day);
		list.add("\n\n");
		list.add("            功  能"+"["+operatorSwitch(func)+"]");
		list.add("            旧账号"+"["+oldActNO+"]");
		list.add("            新  帐  号"+"["+ActNo+"]");
		list.add("            凭 证  种 类"+"["+ActTyp+"]");
		list.add("            客   户   号"+"["+cusId+"]");
		list.add("            帐 户 名  称"+"["+actName+"]");
		list.add("            开 户 网  点"+"["+opn+"]");
		list.add("            ");
		return list;
	}
private String operatorSwitch(final String operator){
	String ret="";
	if("1".equalsIgnoreCase(operator)){
		ret="修改";
	}else if("2".equalsIgnoreCase(operator)){
		ret="新增";
	}else if("3".equalsIgnoreCase(operator)){
		ret="删除";
	}
	return ret;
}
}
