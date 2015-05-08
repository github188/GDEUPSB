package com.bocom.bbip.gdeupsb.action.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsBatchInfoDetailRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class BatchDetailsAction extends BaseAction{
		@Override
		public void execute(Context context) throws CoreException,
				CoreRuntimeException {
				String batNo=context.getData(ParamKeys.BAT_NO).toString();
				//由代收付文件得到批次号
				EupsBatchConsoleInfo eupsBatchConsoleInfos=new EupsBatchConsoleInfo();
				eupsBatchConsoleInfos.setRsvFld1(batNo);
				EupsBatchConsoleInfo eupsBatchConsoleInfo=get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfos).get(0);
				if(eupsBatchConsoleInfo==null){
						throw new CoreException("批次"+batNo+"没有返回信息");
				}
				String eupsBatNo=eupsBatchConsoleInfo.getBatNo();
				//根据批次号获得信息
				EupsBatchInfoDetail eupsBatchInfoDetails=new EupsBatchInfoDetail();
				eupsBatchInfoDetails.setBatNo(eupsBatNo);
				List<EupsBatchInfoDetail> list=get(EupsBatchInfoDetailRepository.class).find(eupsBatchInfoDetails);
				for (EupsBatchInfoDetail eupsBatchInfoDetail : list) {
						eupsBatchInfoDetail.setRmk2(eupsBatchInfoDetail.getAgtSrvCusId());
				}
				int pageNum = (Integer) context.getData("pageNum");
				int pageSize = (Integer) context.getData("pageSize");

				int totalElements = list.size();
				int totalPages = 0;
				if (totalElements % pageSize == 0) {
					totalPages = totalElements / pageSize;
				} else {
					totalPages = totalElements / pageSize + 1;
				}

				List<Map<String, Object>> pageableResponse = new ArrayList<Map<String, Object>>();
				Map<String, Object> pageMap = new HashMap<String, Object>();
				pageMap.put("totalElements", totalElements);
				pageMap.put("totalPages", totalPages);
				pageableResponse.add(pageMap);
				context.setData("pageableResponse", pageableResponse);
				
				context.setData("totalElements", totalElements);
				context.setData("totalPages", totalPages);
				
				context.setData("detailList", BeanUtils.toMaps(list));
		}
}
