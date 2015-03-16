package com.bocom.bbip.gdeupsb.action.vech;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.gdeupsb.entity.GDVechIndentInfo;
import com.bocom.bbip.gdeupsb.repository.GDVechIndentInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.util.Hex;

/**
 * 长途汽车电子票取消
 * @author WangMQ
 *
 */
public class CancelVECHTicketAction extends BaseAction{
	
	private Logger logger = LoggerFactory.getLogger(CancelVECHTicketAction.class);
	
	
	public void execute(Context context) throws CoreException{
		logger.info("Enter in CancelVECHTicketAction!.....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

		//TODO 判断可操作时间，必须在班次时间前（发车前） 当前时间 txnTme<长途汽车订单表.TIME
		GDVechIndentInfo gdVechIndentInfo = new GDVechIndentInfo();
		gdVechIndentInfo.setOrderId((String)context.getData("orderId"));
		List<GDVechIndentInfo> indentInfos = get(GDVechIndentInfoRepository.class).find(gdVechIndentInfo);
		
		Date ticTime = DateUtils.parse(indentInfos.get(0).getClaTim());
		Date txnTime = (Date)context.getData(ParamKeys.TXN_DTE);
		if(ticTime.getTime() < txnTime.getTime()){ //当前时间>班次时间，不得退票
			throw new CoreException("已发车，不得退票");
		}
		
		//TODO 组发易票联报文
		//请求参数
//		<eticketOrderCancel>
//		  <storeId>PC711</storeId>
//		  <terminalId>35170003</terminalId>
//		  <account>abc071</account>
//		  <requestTime>20110623172209</requestTime>
//		  <storeSeq></storeSeq>
//		  <orderId>MV00000002463</orderId>
//		  <sign>77234bbdbe7b0493c6ae5d02757e87b2</sign>
//		</eticketOrderCancel>

//		参数名			类型			说明			必填
//		storeId			String		商家ID		y
//		terminalId		String		终端ID		y
//		account			String		员工帐户	
//		requestTime		String		请求时间		y
//		storeSeq		String		商户订单号	
//		orderId			String		系统订单号	
//		sign						签名MD5(storeId+terminalId+account +requestTime+storeSeq+orderId +MD5(pwd))	y

		
//		MD5(storeId+terminalId+account +requestTime+storeSeq+orderId +MD5(pwd))
		String storeId = context.getData("storeId").toString();//商家ID
        String terminalId = context.getData("terminalId").toString();//终端ID
        String account = context.getData("account").toString();//商户帐户
        String requestTime = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);//请求时间
        String orderId =context.getData("orderId").toString();//订单号
        String pwd = context.getData("pwd").toString();
        String storeSeq = context.getData("storeSeq").toString();//商户订单号
        String sign =null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update((storeId+terminalId+account +requestTime+storeSeq+orderId +pwd.getBytes()).getBytes());
            sign = Hex.encode(md.digest());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        context.setData("requestTime", requestTime);
		context.setData("sign", sign);
		
		
		//发易票联
		Map<String, Object> thdRspMsgMap = get(ThirdPartyAdaptor.class).trade(context);
		context.setDataMap(thdRspMsgMap);
		
		logger.info("============thdRspMsgMap=======" + thdRspMsgMap.size());
		logger.info("=========context:" + context);
		
//		返回参数：
//		属性名		类型		说明
//		respCode	String	返回码
//		respDesc	String	返回说明
//		respTime			返回时间
//
//		返回参数
//		<eticketOrderCancel>
//		  <respCode>1</respCode>
//		  <respDesc>成功</respDesc>
//		  <respTime>2011-09-12</respTime>
//		</eticketOrderCancel>
		
		
		
		
		
		// 取消车票则执行以下操作
		//TODO 抹帐（不一定是购票当天， how to do ?）	 【每个工作日18:00 为日切时间，系统会自动发起日终清算】
		
		
		if(!"1".equals(context.getData("respDesc"))){
			throw new CoreException("电子票取消失败");
		}
		
		//取消成功
		//TODO 更新EUPS流水表	BAK_FLD5为车票状态订单状态  '订单状态（0:未支付，1已支付(订单成功) 2,已作废 3：已提交支付请求）'; lyw:无需更新该状态
		EupsTransJournal eupsTransJournal = new EupsTransJournal();
		
		
		//TODO 长途汽车订单表对应订单信息作废	ORDER_STATE改为2 GDEUPS_VECH_INDENT_INFO
		GDVechIndentInfo updateInfo =  new GDVechIndentInfo();
		updateInfo.setOrderId((String)context.getData("orderId"));
		updateInfo.setOrdSta("2");
		get(GDVechIndentInfoRepository.class).update(updateInfo);
		
		
	}
	
	
	
}
