
package com.bocom.bbip.gdeupsb.action.transportfee;

import java.text.NumberFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.repository.EupsAmountInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 第三方费用查询结果保存.
 * 
 * @version 1.0.0,2014-2-19
 * @author cain.boc
 * @since 1.0.0
 */

public class SaveQryAmtResultAction extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(SaveQryAmtResultAction.class);

    @Override
    public void execute(Context context) throws CoreException, CoreRuntimeException {
        if (context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)) {
            logger.info("SaveQryAmtResultAction start!");
            /* 如果对象为null直接从context中获取数据 */
            EupsAmountInfo eupsAmountInfo = context.getData(ParamKeys.EUPS_AMOUNT_INFO);
            if (null == eupsAmountInfo) {
                eupsAmountInfo = BeanUtils.toObject(context.getDataMap(), EupsAmountInfo.class);
            } else {
                String sqn = context.getData(ParamKeys.SEQUENCE);
                logger.info("the [sqn] in context is:" + sqn);
                eupsAmountInfo.setSqn(sqn);
            }
            // 如果收付类型为空则默认为代收
            if (null == context.getData(ParamKeys.BUS_TYP)) {
                eupsAmountInfo.setRapTyp(Constants.BUS_TYP_2); // 交易类型为代缴
            }else{
            	context.setData(ParamKeys.RAP_TYPE,context.getData(ParamKeys.BUS_TYP));
            }
            get(EupsAmountInfoRepository.class).insert(eupsAmountInfo);
        }
      
    }
    
}
