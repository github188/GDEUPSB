/*@(#)GOMBBHttpSoapTrasnport.java   2015-4-24 
 * Copy Right 2015 Bank of Communications Co.Ltd.
 * All Copyright Reserved
 */

package com.bocom.bbip.gdeupsb.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transform;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.channel.TransportListener;
import com.bocom.jump.bp.core.Context;

/**
 * TODO Document GOMBBHttpSoapTrasnport
 * <p>
 * @version 1.0.0,2015-4-24
 * @author Administrator
 * @since 1.0.0
 */
public class GDUNCBHttpSoapTrasnport implements Transport{
	private static Logger log = LoggerFactory
			.getLogger(GDUNCBHttpSoapTrasnport.class);

	public GDUNCBHttpSoapTrasnport() {
	}

	public TransportListener getTranportLister() {
		return d;
	}

	public void setTranportLister(TransportListener transportlistener) {
		d = transportlistener;
	}

	public void setGateway(GDUNCBHttpSoapGateway gateway) {
		this.gateway = gateway;
	}

	public void setEncodeTransforms(Transform atransform[]) {
		b = atransform;
	}

	public void setDecodeTransforms(Transform atransform[]) {
		c = atransform;
	}

	@SuppressWarnings("unchecked")
	public Object submit(Object obj, Context context)
			throws CommunicationException, JumpException {
		Object obj1 = null;
		Object obj2 = transform(obj, b, context.getProcessId());
		if (getTranportLister() != null)
			getTranportLister().onSend(obj, context, obj2);

		gateway.setTransCode(context.<String> getData("TransCode"));
		Object obj3 = gateway.sendAndReceive(obj2, context.getRequestId());
		if (getTranportLister() != null)
			getTranportLister().onReceive(obj, context, obj3);
		if (obj3 != null)
			obj1 = transform(obj3, c, context.getProcessId());
		context.setVariable("obj1", obj1);
		return obj1;
	}

	protected Object transform(Object obj, Transform atransform[], String s)
			throws JumpException {
		Object obj1 = obj;
		if (atransform != null) {
			Transform atransform1[];
			int j = (atransform1 = atransform).length;
			for (int i = 0; i < j; i++) {
				Transform transform1 = atransform1[i];
				obj1 = transform1.transform(obj1, s);
			}

		}
	
		return obj1;
	}

	private GDUNCBHttpSoapGateway gateway;
	private Transform b[];
	private Transform c[];
	private TransportListener d;
}

