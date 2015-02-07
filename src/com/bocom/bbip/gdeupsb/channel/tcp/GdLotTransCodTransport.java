package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.adaptor.exception.DecodeNotDoneException;
import com.bocom.bbip.eups.adaptor.exception.EncodeNotDoneException;
import com.bocom.bbip.utils.DateUtils;
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
//            localObject2 = transform(paramRequest,this.b,context.getProcessId());//待发送报文字节数组
            localObject2 = encode(context);
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
//				localObject1 = transform(localObject3,this.c,context.getProcessId());
				localObject1 = decode(localObject3);
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
	/**
	 * 组包
	 * @param context
	 * @return
	 */
	protected Object encode(Context context){
		XMLOutputFactory factory = null;
		XMLStreamWriter w = null;
		StringWriter sw = null;
		Object obj = null;
		try {
			sw = new StringWriter();
			factory = XMLOutputFactory.newFactory();
			w = factory.createXMLStreamWriter(sw);
			w.writeStartElement("root");
				w.writeStartElement("pkgH");
					w.writeStartElement("type");
					w.writeCharacters("3");
					w.writeEndElement();
					w.writeStartElement("action");
					w.writeCharacters(context.getData("action").toString());
					w.writeEndElement();
					w.writeStartElement("version");
					w.writeCharacters("0");
					w.writeEndElement();
					w.writeStartElement("dealer_id");
					w.writeCharacters(context.getData("dealer_id").toString());
					w.writeEndElement();
					w.writeStartElement("terminal_id");
					w.writeCharacters("0");
					w.writeEndElement();
					w.writeStartElement("mobile");
					w.writeCharacters("0");
					w.writeEndElement();
					w.writeStartElement("phone");
					w.writeCharacters("0");
					w.writeEndElement();
					w.writeStartElement("sent_time");
					w.writeCharacters(DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
					w.writeEndElement();
				w.writeEndElement();
				
				//根据action组不同的报文体
				String action = context.getData("action").toString().trim();
				if("235".equals(action)){
//					w.writeStartElement("pkgC");
//						w.writeStartElement("game_id");
//						w.writeCharacters(context.getData("gameId").toString());
//						w.writeEndElement();
//					w.writeEndElement();
					action_235(w,context);
				}else if("212".equals(action)) {
				    action_212(w,context);
				}else if("200".equals(action)) {
				    w.writeStartElement("pkgC");
				    w.writeCharacters(" ");
		            w.writeEndElement();
                }
			w.writeEndElement();
			w.flush();
			String data = sw.toString();
			obj = data.getBytes("gbk");
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally{
				try {
					if(sw!=null)
						sw.close();
					if(w!=null)
						w.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
		}
		return obj;
	}
	private void action_235(XMLStreamWriter w,Context context) throws XMLStreamException{
		w.writeStartElement("pkgC");
			w.writeStartElement("game_id");
			w.writeCharacters(context.getData("gameId").toString());
			w.writeEndElement();
		w.writeEndElement();
	}
	
	private void action_212(XMLStreamWriter w,Context context) throws XMLStreamException{
        w.writeStartElement("pkgC");
            w.writeStartElement("user");
            w.writeCharacters(context.getData("usrPam").toString());
            w.writeEndElement();
            w.writeStartElement("pwd");
            w.writeCharacters(context.getData("usrPas").toString());
            w.writeEndElement();
    }
	/**
	 * 解包
	 * @param context
	 * @return
	 */
	protected Object decode(Object obj){
		XMLInputFactory factory = null;
		XMLStreamReader r = null;
		StringReader sr = null;
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String data = new String((byte[])obj,"gbk");
			logger.info("data["+data+"]");
			factory = XMLInputFactory.newFactory();
			sr = new StringReader(data);
			r = factory.createXMLStreamReader(sr);
			String action = "";
			while(r.hasNext()){
				int typ = r.next();
				if(typ!=XMLStreamConstants.START_ELEMENT)
					continue;
				String name = r.getName().toString();
				if(!"action".equals(name))
					continue;
				logger.info("name:["+name+"]");
				action = r.getElementText().trim();
				map.put("action", action);
			}
			String body = data.substring(data.indexOf("<pkgC>"),data.indexOf("</root>"));
			logger.info("body:["+body+"]");
			sr = new StringReader(body);
			r = factory.createXMLStreamReader(sr);
			if("235".equals(action)){
				decode_235(r,map);
			}else if("212".equals(action)){
                decode_212(r,map);
            }else if("200".equals(action)){
                decode_200(r,map);
            }
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return map;
	}
	private void decode_235(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(); 
		while(r.hasNext()){
			int typ = r.next();
			if(typ!=XMLStreamConstants.START_ELEMENT)
				continue;
			String name = r.getName().toString();
			if("return".equals(name)){
				map.put("thdRspCde", r.getAttributeValue(0));
				map.put("resultDes", r.getAttributeValue(1));
			}else if("draw".equals(name)){
				map.put("drawId", r.getAttributeValue(0));
				map.put("drawNm", r.getAttributeValue(1));
			}else if("sale".equals(name)){
				map.put("salStr", r.getAttributeValue(0));
				map.put("salEnd", r.getAttributeValue(1));
			}else if("cash".equals(name)){
				map.put("cshStr", r.getAttributeValue(0));
				map.put("cshEnd", r.getAttributeValue(1));
			}else if("kdraw".equals(name)){
				map.put("isKeno", r.getAttributeValue(0));
				map.put("KenoNum", r.getAttributeValue(1));
			}else if("keno".equals(name)){
				Map<String,Object> m = new HashMap<String,Object>();
				m.put("kenoId", r.getAttributeValue(0));
				m.put("kenoNm", r.getAttributeValue(1));
				m.put("ksalSt", r.getAttributeValue(2));
				m.put("ksalEd", r.getAttributeValue(3));
				list.add(m);
			}
		}
		map.put("kdraw", list);
	}
	
    private void decode_212(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(); 
        while(r.hasNext()){
            int typ = r.next();
            if(typ!=XMLStreamConstants.START_ELEMENT)
                continue;
            String name = r.getName().toString();
            if("return".equals(name)){
                map.put("resultCode", r.getAttributeValue(0));
                map.put("resultDes", r.getAttributeValue(1));
            }
        }
    }
   
    private void decode_200(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
       List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(); 
       while(r.hasNext()){
           int typ = r.next();
           if(typ!=XMLStreamConstants.START_ELEMENT)
               continue;
           String name = r.getName().toString();
           if("return".equals(name)){
               map.put("rRspCod", r.getAttributeValue(0));
               map.put("rRspMsg", r.getAttributeValue(1));
           }
       }
   }
   
//	public static void main(String[] args) {
//		XMLInputFactory factory = null;
//		XMLStreamReader r = null;
//		StringReader sr = null;
//		Map<String,Object> map = new HashMap<String,Object>();
//		try {
//			String data = "<action>123</action>";
//			factory = XMLInputFactory.newFactory();
//			sr = new StringReader(data);
//			r = factory.createXMLStreamReader(sr);
//			while(r.hasNext()){
//				int typ = r.next();
//				if(typ!=XMLStreamConstants.START_ELEMENT)
//					continue;
//				String name = r.getName().toString();
//				String value = r.getElementText();
//				System.out.println(name);
//				System.out.println(value);
//			}
//		}catch(Exception e){
//			
//		}
//	}
}
