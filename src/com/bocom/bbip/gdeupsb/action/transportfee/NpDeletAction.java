package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspZzManag;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspNpManagRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspZzManagRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class NpDeletAction extends BaseAction{
	private final static Log log = LogFactory.getLog(NpDeletAction.class);
	
	@Autowired
	GDEupsbTrspNpManagRepository gdEupsbTrspNpManagRepository;
	@Autowired
	GDEupsbTrspZzManagRepository gdEupsbTrspZzManagRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("NpDeletAction start......");
//		<Set>date1=GETDATETIME(YYYYMMDD)</Set>
		ctx.setData(GDParamKeys.NOD_NO, ctx.getData(ParamKeys.BR));
		ctx.setData(GDParamKeys.BR_NO, ctx.getData(ParamKeys.BK));
		ctx.setData(GDParamKeys.TLR_ID, ctx.getData(ParamKeys.TELLER));
		String today = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		ctx.setData(ParamKeys.AUTHOR_LEVEL, "01673478");
		GDEupsbTrspNpManag gdEupsbTrspNpManag = new GDEupsbTrspNpManag();
		gdEupsbTrspNpManag.setIdNo(ctx.getData(GDParamKeys.ID_NO).toString());
		gdEupsbTrspNpManag.setStatus("0");
		gdEupsbTrspNpManag.setPrtDat(new Date());
		gdEupsbTrspNpManag.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
		List<Integer> sumList = gdEupsbTrspNpManagRepository.findCount(gdEupsbTrspNpManag);

		if(sumList.get(0) == 0){
			ctx.setData(ParamKeys.RSP_MSG, "本网点无该年票记录或者该年票已作废");
			throw new CoreRuntimeException(ErrorCodes.EUPS_FIND_ISEMPTY);
		}else{

			GDEupsbTrspZzManag gdEupsbTrspZzManag = new GDEupsbTrspZzManag();
			gdEupsbTrspZzManag.setZzDat(today);
			gdEupsbTrspZzManag.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
			gdEupsbTrspZzManag.setFlg("0");
			List<Integer> sum1List = gdEupsbTrspZzManagRepository.findCount(gdEupsbTrspZzManag);
			if(sum1List.get(0) != 0){
				ctx.setData(ParamKeys.RSP_MSG, "网点已轧账，不能再进行作废交易！");
				throw new CoreRuntimeException(ErrorCodes.EUPS_CHECK_FAIL);
			}else{

				// 授权原因码并校验是否已授权
				String authTlr = ctx.getData(ParamKeys.AUTHOR_LEVEL);
				if (StringUtils.isEmpty(authTlr)) {
					throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
				}
				ctx.setData(GDConstants.AUTH_REASON, "484021");// 授权原因码 EFE000

				//查询需要打印的数据
				GDEupsbTrspNpManag gdEupsbTrspNpManag2 = new GDEupsbTrspNpManag();
				gdEupsbTrspNpManag2.setIdNo(ctx.getData(GDParamKeys.ID_NO).toString());
				gdEupsbTrspNpManag2.setStatus("0");
				List<GDEupsbTrspNpManag> npManagList = gdEupsbTrspNpManagRepository.find(gdEupsbTrspNpManag2);
				if(CollectionUtils.isEmpty(npManagList)){
					ctx.setData("oIdNo", npManagList.get(0).getIdNo());
					ctx.setData("oLogNo", npManagList.get(0).getSqn());
					ctx.setData("oCarNo", npManagList.get(0).getCarNo());
					ctx.setData("oBegDat", npManagList.get(0).getBegDat());
					ctx.setData("oEndDat", npManagList.get(0).getEndDat());
				}
				
				
				//修改 
				GDEupsbTrspNpManag gdEupsbTrspNpManag3 = new GDEupsbTrspNpManag();
				gdEupsbTrspNpManag3.setStatus("1");
				gdEupsbTrspNpManag3.setIdNo(ctx.getData(GDParamKeys.ID_NO).toString());
				gdEupsbTrspNpManagRepository.update(gdEupsbTrspNpManag3);
			}
		}
		
	}

}
