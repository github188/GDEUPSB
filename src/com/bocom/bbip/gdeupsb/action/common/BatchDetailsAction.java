package com.bocom.bbip.gdeupsb.action.common;

import java.util.List;

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
				//GDEupsBatchConsoleInfo 得到代收付文件名字
				GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=get(GDEupsBatchConsoleInfoRepository.class).findOne(batNo);
				String fleNme=gdEupsBatchConsoleInfo.getRsvFld8();
				//由代收付文件得到批次号
				EupsBatchConsoleInfo eupsBatchConsoleInfos=new EupsBatchConsoleInfo();
				eupsBatchConsoleInfos.setFleNme(fleNme);
				String eupsBatNo=get(EupsBatchConsoleInfoRepository.class).find(eupsBatchConsoleInfos).get(0).getBatNo();
				//根据批次号获得信息
				EupsBatchInfoDetail eupsBatchInfoDetails=new EupsBatchInfoDetail();
				eupsBatchInfoDetails.setBatNo(eupsBatNo);
				List<EupsBatchInfoDetail> list=get(EupsBatchInfoDetailRepository.class).find(eupsBatchInfoDetails);
				String eupsBusTyp=context.getData(ParamKeys.EUPS_BUSS_TYPE).toString();
				if(eupsBusTyp!="ZHAG00"){
						for (EupsBatchInfoDetail eupsBatchInfoDetail : list) {
								eupsBatchInfoDetail.setRmk2(eupsBatchInfoDetail.getAgtSrvCusId());
						}
				}else{
						for (EupsBatchInfoDetail eupsBatchInfoDetail : list) {
								eupsBatchInfoDetail.setRmk2(eupsBatchInfoDetail.getAgtSrvCusId());
						}
				}
				context.setData("detailList", BeanUtils.toMaps(list));
		}
}
