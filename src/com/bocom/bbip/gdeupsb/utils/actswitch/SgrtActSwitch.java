package com.bocom.bbip.gdeupsb.utils.actswitch;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 广东各个分行账号判断
 * @author Glen.Li 2015-1-10
 * @version 1.0.0 
 */
public class SgrtActSwitch {

    @Autowired
    ThirdPartyAdaptor thirdPartyAdaptor;
    /**
     * 江门分行账号判断
     */
    public int AcJud_445999(String ActNo) {
        int ActCls = 0;
        int acLen = ActNo.length();
        switch (acLen) {
            case 21:
                if (ActNo.substring(4, 5).trim().equals("9")) {
                    ActCls = 2;
                } else {
                    if (ActNo.substring(20, 22).trim().equals("99")) {
                        ActCls = 1;
                    } else {
                        ActCls = 0;
                    }
                }
                break;
            case 19:
            case 17:
            case 16:
                ActCls = 3;
                break;
            default:
                ActCls = 7;
                break;
            }
        return ActCls;
    }

    /**
     * 广东省分行账号判断
     */
    public int AcJud_441999(String ActNo) {
        int ActCls = 0;
        int acLen = ActNo.length();
        switch (acLen) {
            case 21:
                if (ActNo.substring(4, 5).trim().equals("9")) {
                    ActCls = 2;
                } else {
                    if (ActNo.substring(20, 22).trim().equals("99")) {
                        ActCls = 0;
                    } else {
                        ActCls = 1;
                    }
                }
                break;
            case 19:
            case 17:
            case 16:
                ActCls = 3;
                break;
            default:
                ActCls = 7;
                break;
        }
        return ActCls;
    }
    
    /**
     *中山分行账号判断
     */
    public int AcJud_484999(String ActNo) {
      /*//TODO; 
        <DynSentence name="QryNActNo"><!--查询新旧帐号对照表=-->
        <Sentence>
          SELECT ActNo FROM savoldact WHERE OldAct='%s'
        </Sentence>
        <Fields>OActNo|</Fields>
      </DynSentence>*/

        int actCls = 0;
        int acLen = ActNo.length();
        switch (acLen) {
            case 21:
                if (ActNo.substring(4, 5).trim().equals("9")) {
                    actCls = 2;
                } else {
                    if (ActNo.substring(1, 4).trim().equals("4846")) {
                        actCls = 0;
                    } else {
                        actCls = 1;
                    }
                }
                break;
            case 20:
                if (ActNo.substring(1, 4).trim().equals("075")) {
                    actCls = 5;
//                    int  NewActNoCount= get(GdeupstbccusagtRepository.class).find(ActNo);
//                    if (NewActNoCount != 0) {
//                        actCls = 7;
//                    }
                } else {
                    if (ActNo.substring(1, 2).trim().equals("6")) {
                        actCls = 3;
                    } else {
                        actCls = 7;
                    }
                }
                break;
            case 18:
                actCls = 0;
                if (ActNo.substring(1, 3).trim().equals("60")) {
                    ActNo = "484" + ActNo;
                }
            case 19:
            case 17:
            case 16:
                actCls = 3;
                break;
            default:
                actCls = 7;
                break;
        }
        return actCls;
    }
    
    /**
     *珠海分行账号判断
     */
    public void AcJud_444999(String actNo, Context context)throws CoreException{
      /*//TODO; 
       <DynSentence name="QryActInf"><!--查询帐户信息-->
            <Sentence>
                SELECT Count(*) FROM ActNoInf444 WHERE OldAct='%s' OR ActNo='%s' FETCH FIRST 1 ROWS ONLY
            </Sentence>
            <Fields>OActNo|OActNo|</Fields>
        </DynSentence>
*/

        actNo = actNo.trim();
        context.setData("ActNam", null);
        context.setData("OpnNod", null);
        context.setData("CusId", null);
        context.setData("ACTSQN", null);
        context.setData("ACTTYP", null);
        
//        int  count= get(GdeupstbccusagtRepository.class).find(actNo);
//        if (count <= 0) {
//            throw new CoreException("无此帐户！");
//        } else{
//            int acLen = actNo.length();
//                if (acLen == 21) {
//                    if (actNo.substring(4, 5).trim().equals("9")){
//                        context.setData("HTxnCd","476520");
//                        context.setData("TTxnCd","476520");
//                        context.setData("ActTyp", "2");
//                        context.setData("CCYCOd","CNY");
//                    } else {
//                        context.setData("HTxnCd","109000");
//                        context.setData("TTxnCd","109000");
//                        context.setData("ActTyp", "6");
//                    }
//                }else{
//                    if (actNo.substring(1, 4).trim().equals("000")||(actNo.substring(1, 3).trim().equals("60")&&acLen==15)){
//                        if (acLen == 18) {
//                            actNo = "444"+ actNo;
//                        } else {
//                            actNo = "444000" +actNo.substring(3,6)+"01"+actNo.substring(6,16);
//                        }
//                        context.setData("HTxnCd","109000");
//                        context.setData("TTxnCd","109000");
//                        context.setData("ActTyp", "6");
//                    } else {
//                        context.setData("HTxnCd","476520");
//                        context.setData("TTxnCd","476520");
//                        context.setData("ActTyp", "4");
//                        context.setData("CCYCOd","CNY");
//                    }
//                }
//                Map<String, Object> ret = thirdPartyAdaptor.trade(context);
//                if (ret != null && !"000000".equals(ret.get("thdRspCde"))) {
//                    String tableName = "actnoinf444"; //TODO;
//                    get(GdeupstbccusagtRepository.class).insert(tableName);
//                    context.setData(ParamKeys.RESPONSE_MESSAGE, ret.get("thdRspMsg"));
//                } else {
//                    context.setData("ActTyp","9");
//                }
//        }
    }
    
    /**
     *佛山分行账号判断
     */
    public int AcJud_446999(String actNo, Context context)throws CoreException{
      /*//TODO; 
     <DynSentence name="QryNActNo"><!--查询新旧帐号对照表=-->
      <Sentence>
        SELECT Count (ActNo) FROM savoldac WHERE OldAct='%s'
      </Sentence>
      <Fields>OActNo|</Fields>
    </DynSentence>
*/
        int actCls = 0;
        int acLen = actNo.length();
        
        switch (acLen) {
        case 20:
            if (actNo.substring(0, 3).trim().equals("371")) {
                actCls = 5;
                if ("NewFlg" != context.getData("FieldName")) {
//                    int count = get(GdeupstbccusagtRepository.class).QryNActNo(actNo);
//                    if (count != 0) {
//                        actCls = 5;
//                    }
                }
                
            } else {
                actCls=7;
            }
            break;
        case 19:
        case 17:
        case 16:
            actCls = 3;
            break;
        default:
            actCls = 7;
            break;
    }
    return actCls;
    }
    /**
     *揭阳分行账号判断
     */
    public int AcJud_485999(String actNo)throws CoreException{
   
        int actCls = 0;
        int acLen = actNo.length();
        
        switch (acLen) {
        case 21:
            if (actNo.substring(3, 4).trim().equals("9")) {
                actCls = 2;
            } else {
                if (actNo.substring(19, 21).trim().equals("99")) {
                    actCls = 1;
                } else {
                    actCls = 0;
                }
            }
            break;
        case 19:
        case 17:
        case 16:
            actCls = 3;
            break;
        default:
            actCls = 7;
            break;
    }
    return actCls;
    }
    /**
     *汕头分行账号判断
     */
    public int AcJud_445999(String actNo, Context context)throws CoreException{
   
        int actCls = 0;
        int acLen = actNo.length();
        
        switch (acLen) {
        case 21:
            if (actNo.substring(3, 4).trim().equals("9")) {
                actCls = 2;
            } else {
                if (actNo.substring(19, 21).trim().equals("99")) {
                    actCls = 1;
                } else {
                    actCls = 0;
                }
            }
            break;
        case 19:
        case 17:
        case 16:
            actCls = 3;
            break;
        default:
            actCls = 7;
            break;
    }
    return actCls;
    }
    
    
}
