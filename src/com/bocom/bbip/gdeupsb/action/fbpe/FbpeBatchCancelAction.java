package com.bocom.bbip.gdeupsb.action.fbpe;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 佛山文件-批扣撤销
 * @version 1.0.0
 * @author Guilin.Li
 * Date 2015-03-17
 */
public class FbpeBatchCancelAction extends BaseAction {

    public void execute(Context context) throws CoreException {
       
        final String batNo=context.getData("dskNo");
        log.info("---批次："+batNo+"开始撤销---");
        GDEupsBatchConsoleInfo info=new GDEupsBatchConsoleInfo();
        info.setBatNo(batNo);
        info.setBatSts(GDConstants.BATCH_STATUS_CANCEL);
        /**更新批次状态为已经撤销*/
        get(GDEupsBatchConsoleInfoRepository.class).updateConsoleInfo(info);
        /**清楚批量临时表中该批次的信息*/
        GdFbpeFileBatchTmp tmp=new GdFbpeFileBatchTmp();
        tmp.setRsvFld8(batNo);
        get(GdFbpeFileBatchTmpRepository.class).delete(tmp);
        log.info("---批次："+batNo+"成功撤销---");
    }
}
