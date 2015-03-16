package com.bocom.bbip.gdeupsb.action.zh;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.ZHActNoInf;
import com.bocom.bbip.gdeupsb.entity.ZHAgtActno;
import com.bocom.bbip.gdeupsb.repository.ZHActNoInfRepository;
import com.bocom.bbip.gdeupsb.repository.ZHAgtActnoRespository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;
/** 481214 代发工资来盘文件转换*/
public class EupsTransferInFile  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsTransferInFile.class);
	private static final int ACT_NO=0;
	private static final int ActNam=1;
	private static final int TxnAmt=2;
    private static final int HEAD=0;
	
	public void process(Context context)throws CoreRuntimeException,IOException, JumpException{
		logger.info("代发工资来盘文件转换 start!!");
		 String inFileName=ContextUtils.assertDataHasLengthAndGetNNR(context, "inFileNme", ErrorCodes.EUPS_FIELD_EMPTY);
		 String outFileName=ContextUtils.assertDataHasLengthAndGetNNR(context, "outFileNme", ErrorCodes.EUPS_FIELD_EMPTY);
        inFileName=inFileName.trim();
         outFileName=outFileName.trim();
		 File srcFile=FileUtils.getFile("D:\\"+inFileName+".txt");
        File destFile=FileUtils.getFile("D:\\"+outFileName+".txt");
		Assert.isFalse(null==srcFile||null==destFile, ErrorCodes.EUPS_FILE_NOT_EXIST);
        List <String> lines=FileUtils.readLines(srcFile, GDConstants.CHARSET_ENCODING_GBK);
		Assert.isNotEmpty(lines, ErrorCodes.EUPS_QUERY_NO_DATA);
		/** 账号转换 */
		List <String>dest=transform(lines);
		Assert.isNotEmpty(dest, ErrorCodes.EUPS_QUERY_NO_DATA);
        FileUtils.writeLines(destFile, GDConstants.CHARSET_ENCODING_GBK, dest);
        /** 发送到BBOS */

		logger.info("代发工资来盘文件转换转换成功!!");
	}
private List<String> transform(List<String>list)throws CoreException{
	final int idx=1;
	List <String>ret= CollectionUtils.createList();
	ret.add(list.get(HEAD));
	for(int i=idx;i<list.size();i++){
		String src=list.get(i);
		String temp[]=StringUtils.split(src,Constants.EUPS_FILE_SPLIT_0);
		Assert.isFalse(temp.length==0, ErrorCodes.EUPS_FILE_PARESE_FAIL);
        String ActNo=temp[ACT_NO].trim();
		Assert.isFalse(StringUtils.isBlank(ActNo), ErrorCodes.EUPS_FILE_PARESE_FAIL);
			/*
			 * 需进一步细化，哪些帐号需要更新 旧18位，"63420"开头（珠海老系统帐号，需转换） 旧20位,"078"开头,第7位到10
			 * 位为9999（原零售3.0帐号，含普通帐户、一本通，需转换）
			 */
		if((ActNo.length()==18&&ActNo.substring(0, 5).equalsIgnoreCase(GDConstants.GD_ZH_WAGE_ACTNO_PREFIX_18_63420))
			||(ActNo.length()==20&&ActNo.substring(0, 3).equalsIgnoreCase(GDConstants.GD_ZH_WAGE_ACTNO_PREFIX_20_078)&&
			ActNo.substring(6,10).equalsIgnoreCase(GDConstants.GD_ZH_WAGE_ACTNO_SUFFIX_20_9999))){

		List<ZHActNoInf> lst=get(ZHActNoInfRepository.class).queryNewByOld(ActNo);
		Assert.isFalse(null==lst||lst.size()!=1, ErrorCodes.EUPS_QUERY_NO_DATA);
		List<ZHAgtActno> lt=get(ZHAgtActnoRespository.class).queryNewByOld(lst.get(0).getActNo());
		Assert.isFalse(null==lt||lt.size()!=1, ErrorCodes.EUPS_QUERY_NO_DATA);
		ActNo=lt.get(0).getActNo();
		}
		
        final String dest=new StringBuilder().append(ActNo).append("|")
                        .append(temp[ActNam].trim()).append("|")
                        .append(temp[TxnAmt].trim()).toString();
        ret.add(dest);
        
	}
	return ret;
}
	
	
}
