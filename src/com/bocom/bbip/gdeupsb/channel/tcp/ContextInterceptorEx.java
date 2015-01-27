/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   SourceFile

package com.bocom.bbip.gdeupsb.channel.tcp;

import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.JumpRuntimeException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.ChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import java.util.Map;

public class ContextInterceptorEx
    implements ChannelInterceptor
{

    public ContextInterceptorEx()
    {
        a = "TransCode";
    }

    public void setIdName(String s)
    {
        a = s;
    }

    public void onRequest(ChannelContext channelcontext, ContextEx contextex)
        throws JumpException
    {
        if(channelcontext.getRequestPayload() == null)
            throw new JumpRuntimeException("JUMPCO8000", "no_request_payload");
        Map map = (Map)channelcontext.getRequestPayload();
        contextex.setDataMap(map);
        if(contextex.getProcessId() == null)
        {
            String s = (String)map.get(a);
            if(s == null)
                throw new JumpException("JUMPCO7000", "no_process_id");
            contextex.setProcessId(s);
        } 
        System.out.println("hahahahahahaha!"+contextex+"hohohohohohohohoho"+channelcontext);
    
    }

    protected void resolveAttributes(ChannelContext channelcontext, ContextEx contextex)
    {
    }

    public void onResponse(ChannelContext channelcontext, ContextEx contextex, Throwable throwable)
    {
       
    }

    protected void postAttributes(ChannelContext channelcontext, ContextEx contextex)
    {
    }

    private String a;
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\Users\Administrator\.m2\repository\com\bocom\jump\com.bocom.jump.bp.core\1.6.2\com.bocom.jump.bp.core-1.6.2.jar
	Total time: 89 ms
	Jad reported messages/errors:
The class file version is 49.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/