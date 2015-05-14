package com.bocom.bbip.gdeupsb.channel.tcp;

import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.JumpRuntimeException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.ChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import java.util.Map;

public class ContextInterceptorExt<Request, Response>
  implements ChannelInterceptor<Request, Response>
{
  private String a = "TransCode";

  public void setIdName(String paramString) {
    this.a = paramString;
  }

  public void onRequest(ChannelContext<Request, Response> paramChannelContext, ContextEx paramContextEx) throws JumpException
  {
    if (paramChannelContext.getRequestPayload() == null)
      throw new JumpRuntimeException("JUMPCO8000", "no_request_payload");
    Map localMap = (Map)paramChannelContext.getRequestPayload();
    paramContextEx.setDataMap(localMap);

//    System.out.println("localMap="+localMap);
//    System.out.println("paramContextEx processId="+paramContextEx.getProcessId());
//    System.out.println("paramContextEx========="+paramContextEx);
//    System.out.println(this.a);
//    System.out.println((String)localMap.get(this.a));
    if (paramContextEx.getProcessId() == null) {
    		String str = (String)localMap.get(this.a);
      		if (str == null) {throw new JumpException("JUMPCO7000", "no_process_id");}
      		System.out.println("~~~!null~~~~~~~~~~~~~~~~~~");
      		paramContextEx.setProcessId(str);
    } else if (localMap.get(this.a) == null) {
    		System.out.println("~~~~~null~~~~~~~~~~~~~~~~");
      localMap.put(this.a, paramContextEx.getProcessId());
    }

    resolveAttributes(paramChannelContext, paramContextEx);
  }

  protected void resolveAttributes(ChannelContext<Request, Response> paramChannelContext, ContextEx paramContextEx)
  {
  }

  public void onResponse(ChannelContext<Request, Response> paramChannelContext, ContextEx paramContextEx, Throwable paramThrowable)
  {
    paramChannelContext.setState(paramContextEx.getState());
    paramChannelContext.setResponsePayload(paramContextEx.getDataMap());

    postAttributes(paramChannelContext, paramContextEx);
  }

  protected void postAttributes(ChannelContext<Request, Response> paramChannelContext, ContextEx paramContextEx)
  {
  }
}