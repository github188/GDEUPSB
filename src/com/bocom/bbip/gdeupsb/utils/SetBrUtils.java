package com.bocom.bbip.gdeupsb.utils;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.jump.bp.core.CoreException;

public class SetBrUtils {

	@Autowired
	BBIPPublicService bbipPublicService;

	/**
	 * 设置签约网点用于清算
	 * 
	 * @param eupsBusTyp
	 * @return
	 */

	// 南方电网： ELEC00 01441800999
	// 广州电力： ELEC01 01441800999
	// 广东烟草： SGRT00 01441800999
	// 双色球： LOTR01 ---待考虑 01441800999
	// 快乐十分： LOTR02 ---待考虑 01441800999
	
	// 珠海路桥： TRSP00  01444001999
	// 珠海地税发票： PROF00 01444001999

	// 汕头电力： ELEC02 01445012999
	// 汕头水费： WATR00 01445007999
	
	// 惠州燃气： PGAS00 01491800999
	
	// 长途汽车： VECH00 
	// 华商校园一卡通： HSSC00  无
	
	// 广州文本代收付： GZAG00 more than one 
	// 佛山文本代收付： FSAG00 
	// 珠海文件代收付： ZHAG00 
	// 中山批量代收付： ZSAG00 

	public static String setBr(String eupsBusTyp) throws CoreException {
		if ("ELEC00".equals(eupsBusTyp) || "ELEC01".equals(eupsBusTyp) || "SGRT00".equals(eupsBusTyp) || "LOTR01".equals(eupsBusTyp) || "LOTR02".equals(eupsBusTyp) || "LOTR00".equals(eupsBusTyp)) {
			return "01441800999";
		} else if ("ELEC02".equals(eupsBusTyp)) {
			return "01445012999";
		} else if ("WATR00".equals(eupsBusTyp)) {
			return "01445007999";
		} else if ("PGAS00".equals(eupsBusTyp)) {
			return "01491800999";
		} else if ("TRSP00".equals(eupsBusTyp) || "PROF00".equals(eupsBusTyp)) {
			return "01444001999";
		} else if ("VECH00".equals(eupsBusTyp)) {
			return "";
		}else {
			throw new CoreException(GDErrorCodes.NO_SUPPORT_BUS_TYP);
		}
	}
	/**
	 * 设置电子柜员
	 * 
	 * @param br
	 * @return
	 */
	// public String setETxnTlr(String br) {
	// String tlr = bbipPublicService.getETeller(br);
	// return tlr;
	// }
}
