package com.bocom.bbip.gdeupsb.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;

/**
 * 判断指定单位控制状态是否为签到状态
 * 
 * @author qc.w
 * 
 */
public class CheckCmpSignSts {

	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;

	@Autowired
	EupsThdBaseInfoRepository eupsThdBaseInfoRepository;

	public boolean checkSignSts(String eupsBusTyp) {

		EupsThdBaseInfo eupsThdBaseInfo = eupsThdBaseInfoRepository.findOne(eupsBusTyp);

		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(eupsThdBaseInfo.getComNo());

		return eupsThdTranCtlInfo.isTxnCtlStsSignin();
	}

	public boolean checkSignSts(String comNo, String eupsBusTyp) {

		EupsThdTranCtlInfo eupsThdTranCtlInfo = eupsThdTranCtlInfoRepository.findOne(comNo);
		return eupsThdTranCtlInfo.isTxnCtlStsSignin();
	}

}
