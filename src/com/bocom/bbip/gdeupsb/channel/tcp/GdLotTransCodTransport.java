package com.bocom.bbip.gdeupsb.channel.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.adaptor.exception.DecodeNotDoneException;
import com.bocom.bbip.eups.adaptor.exception.EncodeNotDoneException;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Gateway;
import com.bocom.jump.bp.channel.Transform;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.channel.TransportListener;
import com.bocom.jump.bp.core.Context;

@SuppressWarnings("rawtypes")
public class GdLotTransCodTransport implements Transport{

	private static Logger logger = LoggerFactory.getLogger(GdLotTransCodTransport.class);
	
	private Gateway gateway;
	private Transform[] b;
	private Transform[] c;
	private TransportListener transportListener;
	public void setTransportListener(TransportListener transportListener) {
		this.transportListener = transportListener;
	}
	 public TransportListener getTranportLister() {
	        return this.transportListener;
	    }
	public void setGateway(Gateway gateway) {
		this.gateway = gateway;
	}
	 public void setEncodeTransforms(Transform[] paramArrayOfTransform) {
        this.b = paramArrayOfTransform;
    }

    public void setDecodeTransforms(Transform[] paramArrayOfTransform) {
        this.c = paramArrayOfTransform;
    }
	

	@SuppressWarnings("unchecked")
	@Override
	public Object submit(Object paramRequest, Context context) throws CommunicationException, JumpException {
		Object localObject1 = null;
		Object localObject2 = null;//待发送报文字节数组
		Object localObject3 = null;//待解析报文字节数组
		/*按配置文件中的格式对本地代发数据进行组装，得到字节数组*/
		logger.info("----------encode start-----------");
		try {
			logger.info("transCode:["+context.getProcessId()+"]");
            logger.info("this.b=" + this.b + ",this.c=" + c + ",this.gateway=" + this.gateway);
            localObject2 = transform(paramRequest,this.b,context.getProcessId());//待发送报文字节数组
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new EncodeNotDoneException("encode error");
		}
		logger.info("----------encode end-----------");
//		if(getTranportLister()!=null){
//			getTranportLister().onSend(paramRequest, context, localObject2);
//		}
		
		localObject3 = gateway.sendAndReceive(localObject2, "");
		
//		if(getTranportLister()!=null){
//			getTranportLister().onReceive(paramRequest, context, localObject3);
//		}
		/*对得到的字节数组按解包配置文件进行解包*/
		logger.info("----------decode start-----------");
		if(localObject3!=null){
			try{
				localObject1 = transform(localObject3,this.c,context.getProcessId());
			}catch(Exception e){
				logger.error(e.getMessage());
				throw new DecodeNotDoneException("decode error");
			}
		}
		logger.info("----------decode end-----------");
		return localObject1;
	}
	/**
	 * 根据配置文件组装报文，并转换为字节数组
	 * @param paramObject:初始字节数组
	 * @param paramArrayOfTransform:配置文件
	 * @param paramString:交易码
	 * @return:组装完成的报文字节数组
	 */
	protected Object transform(Object paramObject,Transform[] paramArrayOfTransform, String paramString ){
		Object localObject = paramObject;
		if(paramArrayOfTransform!=null){
			Transform[] atransform = paramArrayOfTransform;
            for(int i = 0; i < atransform.length; i++){
                Transform localTransform = atransform[i];
                try{
                    localObject = localTransform.transform(localObject, paramString);
                }catch(NullPointerException nullpointerexception) {
                	
                } catch (JumpException e) {
					e.printStackTrace();
				}
            }
		}
		return localObject;
	}
}
