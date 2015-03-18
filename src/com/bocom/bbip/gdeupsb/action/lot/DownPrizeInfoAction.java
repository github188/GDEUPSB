package com.bocom.bbip.gdeupsb.action.lot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class DownPrizeInfoAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(DownPrizeInfoAction.class);

	public void process(Context context) throws CoreException {
          logger.info("开始下载奖期信息");
          context.setData("action", "235");
          final String GameId=ContextUtils.assertDataHasLengthAndGet(context, "gameId", ErrorCodes.EUPS_QUERY_NO_DATA);
          context.setData("gameId", GameId);
          Transport ts = context.getService("STHDLOT1");
          Map<String,Object> resultMap = null;
          try {
              resultMap = (Map<String, Object>) ts.submit(context.getDataMapDirectly(), context);
              context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
          } catch (CommunicationException e1) {
              e1.printStackTrace();
          } catch (JumpException e1) {
              e1.printStackTrace();
          }  
          
          if(!Constants.RESPONSE_CODE_SUCC.equals(resultMap.get("thdRspCde"))){
              log.info("QueryLot Fail!");
              
              return;
          }
          Map map=new HashMap();
          map.put("GameId", GameId);
          map.put("drawId", (String)resultMap.get("drawId"));
          List<Map> ret=get(GdLotDrwTblRepository.class).checkPrizeInfo(map);
          if(!(null!=ret&&ret.size()>0)){
        	  if("1".equalsIgnoreCase((String)resultMap.get("isKeno"))){
        		  saveOne(resultMap,context);
        		  List<Map<String,Object>>list=(List<Map<String,Object>>)resultMap.get("kdraw");
        		  Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
        		  for(Map m:list){
        			  String kenoId=(String)m.get("kenoId");
        			  String kenoNm=(String)m.get("kenoNm");
        			  String ksalSt=(String)m.get("ksalSt");
        			  String ksalEd=(String)m.get("ksalEd");
        			  
        			  
        			  GdLotDrwTbl tbl=new GdLotDrwTbl();
        			  tbl.setDrawId((String)resultMap.get("drawId"));
        			  tbl.setGameId((String)context.getData("gameId"));
        			  tbl.setChkFlg("0");
        			  tbl.setFlwCtl("0");
        			  tbl.setDowPrz("0");
        			  tbl.setKenoId(kenoId);
        			/*  tbl.setClrTim(" ");
        			  tbl.setCshEnd(" ");
        			  tbl.setCshStr(" ");
        			  tbl.setDifAmt(" ");
        			  tbl.setDifFlg(" ");*/
        			  tbl.setKenoNm(kenoNm);
        			  tbl.setkSalEd(ksalEd);
        			  tbl.setkSalSt(ksalSt);
        			 /* tbl.setPayAmt(" ");
        			  tbl.setPayFlg(" ");
        			  tbl.setPrzAmt(" ");
        			  tbl.setRtnTim(" ");
        			  tbl.setSalEnd(" ");
        			  tbl.setSalStr(" ");
        			  tbl.setTotAmt(" ");
        			  tbl.setXfeFlg(" ");*/
        			  get(GdLotDrwTblRepository.class).insert(tbl);
        		  }
        	  }
          }else{
        	  logger.info("   ");
          }
	}
	private void saveOne(Map map,Context context)throws CoreException {
		  GdLotDrwTbl tbl=new GdLotDrwTbl();
		  tbl.setDrawId((String)map.get("drawId"));
		  tbl.setGameId((String)context.getData("gameId"));
		  tbl.setChkFlg("0");
		  tbl.setFlwCtl("0");
		  tbl.setDowPrz("0");
		  tbl.setKenoId("AAAAA");
		 /* tbl.setClrTim(" ");
		  tbl.setCshEnd(" ");
		  tbl.setCshStr(" ");
		  tbl.setDifAmt(" ");
		  tbl.setDifFlg(" ");
		  tbl.setKenoNm(" ");
		  tbl.setkSalEd(" ");
		  tbl.setkSalSt(" ");
		  tbl.setPayAmt(" ");
		  tbl.setPayFlg(" ");
		  tbl.setPrzAmt(" ");
		  tbl.setRtnTim(" ");
		  tbl.setSalEnd(" ");
		  tbl.setSalStr(" ");
		  tbl.setTotAmt(" ");
		  tbl.setXfeFlg(" ");*/
		  get(GdLotDrwTblRepository.class).insert(tbl);
	}
}
