package com.bocom.bbip.gdeupsb.ext;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.*;
import com.bocom.bbip.eups.repository.*;
import com.bocom.bbip.utils.*;
import com.bocom.jump.bp.core.*;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

public class ManageChlSignInOutActionExt extends BaseAction
{

    public ManageChlSignInOutActionExt()
    {
    }
    
    @Autowired
    EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
    
    public void execute(Context context)
        throws CoreException, CoreRuntimeException
    {
        Assert.hasLengthInData(context, "txnTyp", "BBIP0004EU0002", new Object[] {
            "\u4EA4\u6613\u72B6\u6001"
        });
        String sigInOutFlg = (String)context.getData(ParamKeys.SIG_IN_OUT_FLG);
        if(StringUtils.isBlank(sigInOutFlg))
            thdSignInOut(context);
        else
        if("0".equals(sigInOutFlg))
            thdSignInOut(context);
        else
        if("1".equals(sigInOutFlg))
            chnSignInOut(context);
        else
        if("2".equals(sigInOutFlg))
            comAndChnSignInOut(context);
        else{
        	throw new CoreException("BBIP0004EU0407");
        }
        context.setData("txnDte", new Date());
        context.setData("txnTme", new Date());
        
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^context="+context.getDataMap());
        
    }

    private void thdSignInOut(Context context)
        throws CoreException
    {
        log.info("========== Start ManageChlSignInOutAction >>> comSignInOut()..");
        Assert.hasLengthInData(context, "comNo", "BBIP0004EU0002", new Object[] {
            "\u5355\u4F4D\u7F16\u53F7"
        });
        String txnTyp = (String)context.getData("txnTyp");
        log.info((new StringBuilder(">>>>> the txnTyp is:{")).append(txnTyp).append("}").toString());
        if("0".equals(txnTyp))
            thdSignIn(context);
        else
        if("1".equals(txnTyp))
            thdSignOut(context);
        else
            throw new CoreException("BBIP0004EU0065");
    }

    private void chnSignInOut(Context context)
        throws CoreException
    {
        log.info("========== Start ManageChlSignInOutAction >>> chnSignInOut()..");
        Assert.hasLengthInData(context, "chlTyp", "BBIP0004EU0002", new Object[] {
            "\u6E20\u9053\u7C7B\u578B"
        });
        String txnTyp = (String)context.getData("txnTyp");
        log.info((new StringBuilder(">>>>> the txnTyp is:{")).append(txnTyp).append("}").toString());
        if("0".equals(txnTyp))
            chnSignIn(context);
        else
        if("1".equals(txnTyp))
            chnSignOut(context);
        else
            throw new CoreException("BBIP0004EU0065");
    }

    private void comAndChnSignInOut(Context context)
        throws CoreException
    {
        log.info("========== Start ManageChlSignInOutAction >>> comAndChnSignInOut()..");
        Assert.hasLengthInData(context, "chlTyp", "BBIP0004EU0002", new Object[] {
            "\u6E20\u9053\u7C7B\u578B"
        });
        Assert.hasLengthInData(context, "comNo", "BBIP0004EU0002", new Object[] {
            "\u5355\u4F4D\u7F16\u53F7"
        });
        String txnTyp = (String)context.getData("txnTyp");
        log.info((new StringBuilder(">>>>> the txnTyp is:{")).append(txnTyp).append("}").toString());
        if("0".equals(txnTyp))
            comAndChnSignIn(context);
        else
        if("1".equals(txnTyp))
            comAndChnSignOut(context);
        else
            throw new CoreException("BBIP0004EU0065");
    }

    private void thdSignIn(Context context)
        throws CoreException, CoreRuntimeException
    {
        EupsThdTranCtlInfo eupsThdTranCtlInfo = (EupsThdTranCtlInfo)BeanUtils.toObject(context.getDataMap(), EupsThdTranCtlInfo.class);
        EupsThdTranCtlInfo resultThdTranCtlInfo = (EupsThdTranCtlInfo)(get(EupsThdTranCtlInfoRepository.class)).findOne(eupsThdTranCtlInfo.getComNo());
        if(resultThdTranCtlInfo == null)
            throw new CoreException("BBIP0004EU0408");
        if(resultThdTranCtlInfo.getTxnCtlSts().equals("0"))
            throw new CoreException("BBIP0004EU0112");
        if(resultThdTranCtlInfo.getTxnCtlSts().equals("1"))
        {
            eupsThdTranCtlInfo.setTxnCtlSts("0");
            eupsThdTranCtlInfo.setTxnDte(new Date());
            eupsThdTranCtlInfo.setTxnTme(new Date());
            eupsThdTranCtlInfo.setChlTyp("90");
            (get(EupsThdTranCtlInfoRepository.class)).update(eupsThdTranCtlInfo);
        }
    }

    private void thdSignOut(Context context)
        throws CoreException, CoreRuntimeException
    {
        EupsThdTranCtlInfo eupsThdTranCtlInfo = (EupsThdTranCtlInfo)BeanUtils.toObject(context.getDataMap(), EupsThdTranCtlInfo.class);
        EupsThdTranCtlInfo resultThdTranCtlInfo = (get(EupsThdTranCtlInfoRepository.class)).findOne(eupsThdTranCtlInfo.getComNo());
        if(resultThdTranCtlInfo == null)
            throw new CoreException("BBIP0004EU0408");
        if(resultThdTranCtlInfo.getTxnCtlSts().equals("1"))
            throw new CoreException("BBIP0004EU0113");
        if(resultThdTranCtlInfo.getTxnCtlSts().equals("0"))
        {
            eupsThdTranCtlInfo.setTxnCtlSts("1");
            eupsThdTranCtlInfo.setTxnDte(new Date());
            eupsThdTranCtlInfo.setTxnTme(new Date());
            eupsThdTranCtlInfo.setChlTyp("90");
            (get(EupsThdTranCtlInfoRepository.class)).update(eupsThdTranCtlInfo);
        }
    }

    private void chnSignIn(Context context)
        throws CoreException, CoreRuntimeException
    {
        EupsChlCtlInfo eupsChlCtlInfo = (EupsChlCtlInfo)BeanUtils.toObject(context.getDataMap(), EupsChlCtlInfo.class);
        EupsChlCtlInfo resultEupsChlCtlInfo = (EupsChlCtlInfo)(get(EupsChlCtlInfoRepository.class)).findOne(eupsChlCtlInfo.getChlTyp());
        if(resultEupsChlCtlInfo == null)
            throw new CoreException("BBIP0004EU0409");
        if(resultEupsChlCtlInfo.getTxnCtlSts().equals("0"))
            throw new CoreException("BBIP0004EU0112");
        if(resultEupsChlCtlInfo.getTxnCtlSts().equals("1"))
        {
            eupsChlCtlInfo.setTxnCtlSts("0");
            eupsChlCtlInfo.setTxnDte(new Date());
            eupsChlCtlInfo.setTxnTme(new Date());
            ((EupsChlCtlInfoRepository)get(EupsChlCtlInfoRepository.class)).update(eupsChlCtlInfo);
        }
    }

    private void chnSignOut(Context context)
        throws CoreException, CoreRuntimeException
    {
        EupsChlCtlInfo eupsChlCtlInfo = (EupsChlCtlInfo)BeanUtils.toObject(context.getDataMap(),EupsChlCtlInfo.class);
        EupsChlCtlInfo resultEupsChlCtlInfo = (EupsChlCtlInfo)((EupsChlCtlInfoRepository)get(EupsChlCtlInfoRepository.class)).findOne(eupsChlCtlInfo.getChlTyp());
        if(resultEupsChlCtlInfo == null)
            throw new CoreException("BBIP0004EU0409");
        if(resultEupsChlCtlInfo.getTxnCtlSts().equals("1"))
            throw new CoreException("BBIP0004EU0113");
        if(resultEupsChlCtlInfo.getTxnCtlSts().equals("0"))
        {
            eupsChlCtlInfo.setTxnCtlSts("1");
            eupsChlCtlInfo.setTxnDte(new Date());
            eupsChlCtlInfo.setTxnTme(new Date());
            ((EupsChlCtlInfoRepository)get(EupsChlCtlInfoRepository.class)).update(eupsChlCtlInfo);
        }
    }

    private void comAndChnSignIn(Context context)
        throws CoreException
    {
        EupsTransCtlInfo eupsTransCtlInfo = (EupsTransCtlInfo)BeanUtils.toObject(context.getDataMap(), EupsTransCtlInfo.class);
        EupsTransCtlInfo eupsTransCtlInfoTemp = new EupsTransCtlInfo();
        eupsTransCtlInfoTemp.setChlTyp((String)context.getData("chlTyp"));
        eupsTransCtlInfoTemp.setComNo((String)context.getData("comNo"));
        List eupsTransCtlInfoList = ((EupsTransCtlInfoRepository)get(EupsTransCtlInfoRepository.class)).find(eupsTransCtlInfoTemp);
        if(CollectionUtils.isEmpty(eupsTransCtlInfoList))
            throw new CoreException("BBIP0004EU0410");
        if(eupsTransCtlInfoList.size() > 1)
            throw new CoreException("BBIP0004EU0411");
        if("0".equals(((EupsTransCtlInfo)eupsTransCtlInfoList.get(0)).getTxnCtlSts()))
            throw new CoreException("BBIP0004EU0112");
        if("1".equals(((EupsTransCtlInfo)eupsTransCtlInfoList.get(0)).getTxnCtlSts()))
        {
            eupsTransCtlInfo.setSysNo(((EupsTransCtlInfo)eupsTransCtlInfoList.get(0)).getSysNo());
            eupsTransCtlInfo.setTxnCtlSts("0");
            eupsTransCtlInfo.setTxnDte(new Date());
            eupsTransCtlInfo.setTxnTme(new Date());
            ((EupsTransCtlInfoRepository)get(EupsTransCtlInfoRepository.class)).update(eupsTransCtlInfo);
        }
    }

    private void comAndChnSignOut(Context context)
        throws CoreException
    {
        EupsTransCtlInfo eupsTransCtlInfo = (EupsTransCtlInfo)BeanUtils.toObject(context.getDataMap(), EupsTransCtlInfo.class);
        EupsTransCtlInfo eupsTransCtlInfoTemp = new EupsTransCtlInfo();
        eupsTransCtlInfoTemp.setChlTyp((String)context.getData("chlTyp"));
        eupsTransCtlInfoTemp.setComNo((String)context.getData("comNo"));
        List eupsTransCtlInfoList = ((EupsTransCtlInfoRepository)get(EupsTransCtlInfoRepository.class)).find(eupsTransCtlInfoTemp);
        if(CollectionUtils.isEmpty(eupsTransCtlInfoList))
            throw new CoreException("BBIP0004EU0410");
        if(eupsTransCtlInfoList.size() > 1)
            throw new CoreException("BBIP0004EU0411");
        if("1".equals(((EupsTransCtlInfo)eupsTransCtlInfoList.get(0)).getTxnCtlSts()))
            throw new CoreException("BBIP0004EU0113");
        if("0".equals(((EupsTransCtlInfo)eupsTransCtlInfoList.get(0)).getTxnCtlSts()))
        {
            eupsTransCtlInfo.setSysNo(((EupsTransCtlInfo)eupsTransCtlInfoList.get(0)).getSysNo());
            eupsTransCtlInfo.setTxnCtlSts("1");
            eupsTransCtlInfo.setTxnDte(new Date());
            eupsTransCtlInfo.setTxnTme(new Date());
            ((EupsTransCtlInfoRepository)get(EupsTransCtlInfoRepository.class)).update(eupsTransCtlInfo);
        }
    }
}
