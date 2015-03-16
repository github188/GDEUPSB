package com.bocom.bbip.gdeupsb.action.zh;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.common.BBIPCoreConstants;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.ZHAgtActno;
import com.bocom.bbip.gdeupsb.repository.ZHAgtActnoRespository;
import com.bocom.bbip.thd.org.apache.commons.io.Charsets;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.FileUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;
/** 481215 代发工资返盘文件转换*/
public class EupsTransferOutFile  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsTransferOutFile.class);
	private static final int ACT_NO=2;
	private static final int ActNam=3;
	private static final int TxnAmt=4;
	private static final int SucFlg=6;
    private static final int HEAD=0;

	public void process(Context context)throws CoreException,CoreRuntimeException,IOException{
		logger.info("返盘文件转换 start!!");
		final String inFileName=ContextUtils.assertDataHasLengthAndGetNNR(context, "inFileNme", ErrorCodes.EUPS_FIELD_EMPTY);
		final String outFileName=ContextUtils.assertDataHasLengthAndGetNNR(context, "outFileNme", ErrorCodes.EUPS_FIELD_EMPTY);
        File srcFile=FileUtils.getFile("D:\\"+inFileName+".txt");
        File destFile=FileUtils.getFile("D:\\"+outFileName+".txt");
		Assert.isFalse(null==srcFile||null==destFile, ErrorCodes.EUPS_FILE_NOT_EXIST);
        List <String> lines=FileUtils.readLines(srcFile, "GBK");
		Assert.isNotEmpty(lines, ErrorCodes.EUPS_FILE_PARESE_FAIL);
		/** 账号转换 */
		List <String>dest=transform(lines);
		Assert.isNotEmpty(dest, ErrorCodes.EUPS_FILE_PARESE_FAIL);
        FileUtils.writeLines(destFile, "GBK", dest);
        /** 发送到BBOS */
		logger.info("返盘文件转换成功!!");
	}
private List<String> transform(List<String>list)throws CoreException{
	final int idx=1;
	List <String>ret= CollectionUtils.createList();
	ret.add(list.get(HEAD));
	for(int i=idx;i<list.size();i++){
		String src=list.get(i);
		String temp[]=StringUtils.split(src,Constants.EUPS_FILE_SPLIT_0);
		Assert.isFalse(temp.length==0||null==temp, ErrorCodes.EUPS_FILE_PARESE_FAIL);
        String ActNo=temp[ACT_NO].trim();
		Assert.isFalse(StringUtils.isBlank(ActNo), ErrorCodes.EUPS_FILE_PARESE_FAIL);
		/*需进一步细化，哪些帐号需要更新
                          新21位实体帐号,"4449"开头,"202"结尾,转换为新21位主帐号"4449"开头,"998"结尾,主帐号）*/
		if(21==ActNo.length()&&ActNo.substring(0, 4).equalsIgnoreCase(GDConstants.GD_ZH_WAGE_ACTNO_PREFIX_4449)&&
				ActNo.substring(18).equalsIgnoreCase(GDConstants.GD_ZH_WAGE_ACTNO_SUFFIX_202)){
	        logger.info("帐号:"+ActNo+"转换");
	        List<ZHAgtActno> lt=get(ZHAgtActnoRespository.class).queryOldByNew(ActNo);
	        Assert.isNotEmpty(lt, ErrorCodes.EUPS_QUERY_NO_DATA);
	        ActNo=lt.get(0).getOldAct();		}
		
        final String dest=new StringBuilder().append(ActNo).append("|")
                        .append(temp[ActNam].trim()).append("|")
                        .append(temp[TxnAmt].trim()).append("|")
                        .append(temp[SucFlg].trim()).toString();
        ret.add(dest);
        
	}
	return ret;
}
	
	
	
}
