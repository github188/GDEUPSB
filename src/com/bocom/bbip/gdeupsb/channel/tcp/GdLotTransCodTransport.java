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
import com.bocom.bbip.utils.StringUtils;
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
				localObject1 = decode(localObject3,context);
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
			factory = XMLOutputFactory.newInstance();
			w = factory.createXMLStreamWriter(sw);
			w.writeStartElement("pkg");
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
				}else if("212".equals(action)) {  //系统角色登录
				    action_212(w,context);
				}else if("200".equals(action)) {  //系统对时
				    w.writeStartElement("pkgC");
				    w.writeCharacters(" ");
		            w.writeEndElement();
                }else if("209".equals(action)) {  //彩民信息查询
                    action_209(w,context);
                }else if("236".equals(action)){
					action_236(w,context);
				}else if("240".equals(action)){
					action_240(w,context);
				}else if("237".equals(action)){
					action_237(w,context);
				}else if("231".equals(action)) {  //投注
                    action_231(w,context);
                }else if("201".equals(action)) {  //注册
                    action_201(w,context);
                }else if("219".equals(action)) {  //注册
                    action_219(w,context);
                }
                else if("238".equals(action)) {  //文件下载
                    action_238(w,context);
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
	private void action_238(XMLStreamWriter w,Context context) throws XMLStreamException{
		 w.writeStartElement("pkgC");
         w.writeStartElement("file_type");
         w.writeCharacters(context.getData("file_type").toString());
         w.writeEndElement();
         w.writeStartElement("file_name");
         w.writeCharacters(context.getData("file_name").toString());
         w.writeEndElement();
         w.writeEndElement();
	}
	   private void action_201(XMLStreamWriter w,Context context) throws XMLStreamException{
	        w.writeStartElement("pkgC");
	            w.writeStartElement("gamblerBasicInfo");
    	            w.writeStartElement("gambler_name");
    	            w.writeCharacters(context.getData("lotNam").toString());
    	            w.writeEndElement();
    	            w.writeStartElement("gambler_pwd");
                    w.writeCharacters(context.getData("lotPsw").toString());
                    w.writeEndElement();
                    w.writeStartElement("regist_time");
                    w.writeCharacters(context.getData("regTim").toString());
                    w.writeEndElement();
                    w.writeStartElement("email");
                    w.writeCharacters(context.getData("email").toString());
                    w.writeEndElement();
	            w.writeEndElement();
	            w.writeStartElement("gamblerAdditionalInfo");
                    w.writeStartElement("city_id");
                    w.writeCharacters(context.getData("cityId").toString());
                    w.writeEndElement();
                    w.writeStartElement("ID_type");
                    w.writeCharacters(context.getData("idTyp").toString());
                    w.writeEndElement();
                    w.writeStartElement("ID_no");
                    w.writeCharacters(context.getData("idNo").toString());
                    w.writeEndElement();
                    w.writeStartElement("account_type");
                    w.writeCharacters("14");
                    w.writeEndElement();
                    w.writeStartElement("charge_type");
                    w.writeCharacters("5");
                    w.writeEndElement();
                    w.writeStartElement("prize_type");
                    w.writeCharacters("3");
                    w.writeEndElement();
                    w.writeStartElement("bindCard");
                        w.writeAttribute("isBind", "1");
                        w.writeStartElement("card_type");
                        w.writeCharacters("2");
                        w.writeEndElement();
                        w.writeStartElement("bank_id");
                        w.writeCharacters("COMM");
                        w.writeEndElement();
                        w.writeStartElement("bank_card");
                        w.writeCharacters(context.getData("crdNo").toString());
                        w.writeEndElement();
                    w.writeEndElement();
                    w.writeStartElement("real_name");
                    w.writeCharacters(context.getData("cusNam").toString());
                    w.writeEndElement();
                    w.writeStartElement("sex");
                    w.writeCharacters(context.getData("sex").toString());
                    w.writeEndElement();
                    w.writeStartElement("birthday");
                    w.writeCharacters("");
                    w.writeEndElement();
                    w.writeStartElement("mobile");
                    w.writeCharacters(context.getData("mobTel").toString());
                    w.writeEndElement();
                    w.writeStartElement("phone");
                    w.writeCharacters("");
                    w.writeEndElement();
                w.writeEndElement();
                w.writeStartElement("bindDealer");
                    w.writeAttribute("isBind", "1");
                    w.writeStartElement("dealer_id");
                    w.writeCharacters(context.getData("dealId").toString());
                    w.writeEndElement();
                w.writeEndElement();
	        w.writeEndElement();
	    }
	   
    private void action_219(XMLStreamWriter w,Context context) throws XMLStreamException{
        w.writeStartElement("pkgC");
            w.writeStartElement("gambler_name");
            w.writeCharacters(context.getData("lotNam").toString());
            w.writeEndElement();
            w.writeStartElement("modify_time");
            w.writeCharacters(context.getData("fTXNTm").toString());
            w.writeEndElement();
            w.writeStartElement("gambler_name");
            w.writeCharacters(context.getData("lotPsw").toString());
            w.writeEndElement();
        w.writeEndElement();
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
	private void action_209(XMLStreamWriter w,Context context) throws XMLStreamException{
        w.writeStartElement("pkgC");
            w.writeStartElement("gambler_name");
            w.writeCharacters(context.getData("gambler_name").toString());
            w.writeEndElement();
            w.writeStartElement("gambler_pwd");
            w.writeCharacters(context.getData("gambler_pwd").toString());
            w.writeEndElement();
            w.writeStartElement("modify_time");
            w.writeCharacters(context.getData("fTXNTm").toString());
            w.writeEndElement();
    }
	private void action_236(XMLStreamWriter w,Context context) throws XMLStreamException{
		w.writeStartElement("pkgC");
			w.writeStartElement("game_id");
			w.writeCharacters(context.getData("gameId").toString());
			w.writeEndElement();
			w.writeStartElement("draw_id");
			w.writeCharacters(context.getData("drawId").toString());
			w.writeEndElement();
			w.writeStartElement("keno_draw_id");
			w.writeCharacters(context.getData("kenoId").toString());
			w.writeEndElement();
		w.writeEndElement();
	}
	private void action_240(XMLStreamWriter w,Context context) throws XMLStreamException{
		w.writeStartElement("pkgC");
			w.writeStartElement("game_id");
			w.writeCharacters(context.getData("gameId").toString());
			w.writeEndElement();
			w.writeStartElement("draw_id");
			w.writeCharacters(context.getData("drawId").toString());
			w.writeEndElement();
			w.writeStartElement("keno_draw_id");
			w.writeCharacters(context.getData("kenoId").toString());
			w.writeEndElement();
		w.writeEndElement();
	}
	private void action_237(XMLStreamWriter w,Context context) throws XMLStreamException{
		w.writeStartElement("pkgC");
			w.writeStartElement("game_id");
			w.writeCharacters(context.getData("gameId").toString());
			w.writeEndElement();
			w.writeStartElement("draw_id");
			w.writeCharacters(context.getData("drawId").toString());
			w.writeEndElement();
			w.writeStartElement("file_type");
			w.writeCharacters(context.getData("filTyp").toString());
			w.writeEndElement();
		w.writeEndElement();
	}
	private void action_231(XMLStreamWriter w,Context context) throws XMLStreamException{
        w.writeStartElement("pkgC");
            w.writeStartElement("schemeInfo");
                w.writeStartElement("dealer_serial");
                w.writeCharacters(context.getData("txnLog").toString());
                w.writeEndElement();
                w.writeStartElement("scheme_type");
                w.writeCharacters(context.getData("schTyp").toString());
                w.writeEndElement();
                w.writeStartElement("scheme_title");
                w.writeCharacters(context.getData("schTit").toString());
                w.writeEndElement();
                w.writeStartElement("secrecy_level");
                w.writeCharacters(context.getData("secLev").toString());
                w.writeEndElement();
                w.writeStartElement("create_time");
                w.writeCharacters(context.getData("lotTxnTim").toString());
                w.writeEndElement();
                w.writeStartElement("city_id");
                w.writeCharacters(context.getData("cityId").toString());
                w.writeEndElement();
                w.writeStartElement("game_id");
                w.writeCharacters(context.getData("gameId").toString());
                w.writeEndElement();
                w.writeStartElement("draw_id");
                w.writeCharacters(context.getData("drawId").toString());
                w.writeEndElement();
                w.writeStartElement("keno_draw_id");
                w.writeCharacters(context.getData("kenoId").toString());
                w.writeEndElement();
                w.writeStartElement("play_id");
                w.writeCharacters(context.getData("playId").toString());
                w.writeEndElement();
                w.writeStartElement("bet_method");
                w.writeCharacters(context.getData("betMet").toString());
                w.writeEndElement();
                w.writeStartElement("bet_mode");
                w.writeCharacters(context.getData("betMod").toString());
                w.writeEndElement();
                w.writeStartElement("bet_multiple");
                w.writeCharacters(context.getData("betMul").toString());
                w.writeEndElement();
                w.writeStartElement("bet_money");
                w.writeCharacters(context.getData("txnAmt").toString());
                w.writeEndElement();
                w.writeStartElement("betInfo");
                    w.writeAttribute("group", context.getData("grpNum").toString());
                    w.writeAttribute("num", context.getData("betNum").toString());
                    w.writeStartElement("bet_line");
                    w.writeCharacters(context.getData("betLin").toString());
                    w.writeEndElement();
                w.writeEndElement();
            w.writeEndElement();
            
            w.writeStartElement("gamblerInfo");
                w.writeStartElement("gambler_name");
                w.writeCharacters(context.getData("lotNam").toString());
                w.writeEndElement();
                w.writeStartElement("chargeInfo");
                    w.writeAttribute("isSettled", "1");
                        w.writeStartElement("account_type");
                        w.writeCharacters("14");
                        w.writeEndElement();
                        w.writeStartElement("charge_id");
                        w.writeCharacters("14110000");
                        w.writeEndElement();
                    w.writeEndElement();
                w.writeEndElement();
            w.writeEndElement();
        w.writeEndElement();
    }
	/**
	 * 解包
	 * @param context
	 * @return
	 */
	protected Object decode(Object obj,Context context){
		XMLInputFactory factory = null;
		XMLStreamReader r = null;
		StringReader sr = null;
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			String data = new String((byte[])obj,"gbk");
			logger.info("data["+data+"]");
			factory = XMLInputFactory.newInstance();
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
			String body = data.substring(data.indexOf("<pkgC>"),data.indexOf("</pkg>"));
			logger.info("body:["+body+"]");
			sr = new StringReader(body);
			r = factory.createXMLStreamReader(sr);
			if("235".equals(action)){
				decode_235(r,map);
			}else if("212".equals(action)){
                decode_212(r,map);
            }else if("200".equals(action)){
                decode_200(r,map);
            }else if("236".equals(action)){
				decode_236(r,map);
			}else if("240".equals(action)){
				decode_240(r,map);
			}else if("237".equals(action)){
				decode_237(r,map);
			}else if("209".equals(action)){
			    decode_209(r,map);
            }else if("231".equals(action)){
                decode_231(r,map);
            }else if("201".equals(action)){
                decode_201(r,map);
            }else if("219".equals(action)){
                decode_219(r,map);
            }
            else if("238".equals(action)){
                decode_238(r,map,context);
            }
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return map;
	}
	private void decode_238_2(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
		 List<Map>list=new ArrayList<Map>();
		 List<Map>lst=new ArrayList<Map>();
		 Map m=new HashMap();
		 while(r.hasNext()){
	            int typ = r.next();
	            if(typ!=XMLStreamConstants.START_ELEMENT)
	                continue;
	            String name = r.getName().toString();
	            if("draw".equals(name)){
	                map.put("game_id", r.getAttributeValue(0));
	                map.put("draw_id", r.getAttributeValue(1));
	            }
	            if ("point".equals(name)) {
              map.put("db", r.getAttributeValue(0));
              map.put("cash",r.getAttributeValue(1));
              map.put("dump", r.getAttributeValue(2));
              
          }
	            if ("bonusItem".equals(name)) {
	            	if(lst.size()>0){
	            		List lt=new ArrayList();
	            		lt.addAll(lst);
	            		m.put("bonusNodes", lt);
	            		list.add(m);
	            		lst.clear();
	            	}
	            	m.put("sn", r.getAttributeValue(0));
	            	m.put("dumpID",r.getAttributeValue(1));
	                m.put("cipher", r.getAttributeValue(2));
	                m.put("bigBon", r.getAttributeValue(3));
	                m.put("totPrz",r.getAttributeValue(4));
	                m.put("tLogNo", r.getAttributeValue(5));
	                m.put("termId", r.getAttributeValue(6));
	                m.put("txnlog",r.getAttributeValue(7));
	                m.put("gameId", (String)map.get("game_id"));
	                m.put("drawId",(String)map.get("draw_id"));
	                m.put("kenoId","");
	                
          }
	            if ("bonusNode".equals(name)) {
	            	Map mm=new HashMap();
	            	mm.put("betMod", r.getAttributeValue(0));
	            	mm.put("betMul",r.getAttributeValue(1));
	                mm.put("classNo", r.getAttributeValue(2));
	                mm.put("przAmt", r.getAttributeValue(3));
	                mm.put("betLin",r.getAttributeValue(4));
	                
	                mm.put("tLogNo", (String)m.get("tLogNo"));
	                mm.put("txnLog",(String)m.get("txnlog"));
	                mm.put("gameId", (String)map.get("game_id"));
	                mm.put("drawId",(String)map.get("draw_id"));
	                mm.put("kenoId","");
	                lst.add(mm);
	            }
	       
	        }
		List lt=new ArrayList();
 		lt.addAll(lst);
 		m.put("bonusNodes", lt);
 		list.add(m);
 		lst.clear();
		map.put("ret", list);
	}
	private void decode_238_3(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
		 List<Map>list=new ArrayList<Map>();
		 List<Map>lst=new ArrayList<Map>();
		 Map m=new HashMap();
		String gameId="";
		while(r.hasNext()){
	            int typ = r.next();
	            if(typ!=XMLStreamConstants.START_ELEMENT)
	                continue;
	            String name = r.getName().toString();
	            if("draw".equals(name)){
	                map.put("game_id", r.getAttributeValue(0));
	                map.put("draw_id", r.getAttributeValue(1));
	                gameId=r.getAttributeValue(0);
	            }
	            if ("drawSum".equals(name)) {
             map.put("sucessNum", r.getAttributeValue(0));
             map.put("totalMoney", r.getAttributeValue(1));
         }
	         
	            if(StringUtils.isNotEmpty(gameId)){
	            	if("7".equals(gameId)){
	            		if ("keno".equals(name)) {
	            			if(lst.size()>0){
	    	            		List lt=new ArrayList();
	    	            		lt.addAll(lst);
	    	            		m.put("kenoNodes", lt);
	    	            		list.add(m);
	    	            		lst.clear();
	    	            	}
	            			m.put("KenoId", r.getAttributeValue(0));
	            		}
	            			
	            			if ("kenoSum".equals(name)) {
	            				m.put("KenoSucessNum", r.getAttributeValue(0));
	            				m.put("KenoTotalMoney", r.getAttributeValue(1));
	            			}
	            			if ("scheme".equals(name)) {
	            				Map mm=new HashMap();
	            				
	        	            	mm.put("seqNo", r.getAttributeValue(0));
	        	            	mm.put("schId",r.getAttributeValue(1));
	        	                mm.put("lotNam", r.getAttributeValue(2));
	        	                mm.put("txnLog", r.getAttributeValue(3));
	        	                mm.put("playId",r.getAttributeValue(4));
	        	                mm.put("txnTim",r.getAttributeValue(5));
	        	                mm.put("txnAmt",r.getAttributeValue(6));
	        	                mm.put("kenoId", (String)m.get("KenoId"));
	        	                mm.put("chkFlg", "0");
	        	                mm.put("drawId", map.get("draw_id"));
	        	                mm.put("gameId", map.get("game_id"));
	        	                
	        	                mm.put("chkDat", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
	        	                mm.put("chkTim", "");
	        	                lst.add(mm);
	            			}
	            			
	            		}
	            	
                     if("5".equals(gameId)){
                    	
	            	}
	            }
		 }
		List lt=new ArrayList();
		lt.addAll(lst);
		m.put("kenoNodes", lt);
		list.add(m);
		lst.clear();
		 map.put("ret", list);
	}
	
	 private void decode_238(XMLStreamReader r,Map<String,Object> map,Context context) throws XMLStreamException{
		   if("2".equalsIgnoreCase((String)context.getData("file_type"))){
			   decode_238_2(r,map);
		   }
		   if("3".equalsIgnoreCase((String)context.getData("file_type"))){
			   decode_238_3(r,map);
		   }
	       
	    }
	 private void decode_201(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
	        while(r.hasNext()){
	            int typ = r.next();
	            if(typ!=XMLStreamConstants.START_ELEMENT)
	                continue;
	            String name = r.getName().toString();
	            if("return".equals(name)){
	                map.put("resultCode", r.getAttributeValue(0));
	                map.put("resultDes", r.getAttributeValue(1));
	            }
	            if ("gambler_name".equals(name)) {
                    map.put("lotNam", r.getElementText());
                }
	            if ("gambler_status".equals(name)) {
                    map.put("status", r.getElementText());
                }
	            if ("account_type".equals(name)) {
                    map.put("lotAccTyp", r.getElementText());
                }
	            if ("charge_type".equals(name)) {
                    map.put("lotChgTyp", r.getElementText());
                }
	            if ("prize_type".equals(name)) {
                    map.put("lotPrzTyp", r.getElementText());
                }
	            if ("bindDealer".equals(name)) {
	                map.put("isBind", r.getAttributeValue(0));
	                if ("dealer_id".equals(name)) {
	                    map.put("lotDealId", r.getElementText());
	                }
                }
	        }
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
    
    private void decode_219(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
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
    private void decode_231(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
        while(r.hasNext()){
            int typ = r.next();
            if(typ!=XMLStreamConstants.START_ELEMENT)
                continue;
            String name = r.getName().toString();
            if("return".equals(name)){
                map.put("rRspCod", r.getAttributeValue(0));
                map.put("rRspMsg", r.getAttributeValue(1));
            }
            if ("schemeInfo".equals(name)) {
                if ("dealer_serial".equals(name)) {
                    map.put("txnLog", r.getElementText());
                }
                if ("scheme_id".equals(name)) {
                    map.put("schId", r.getElementText());
                }
                if ("serial_no".equals(name)) {
                    map.put("tLogNo", r.getElementText());
                }
                if ("cipher".equals(name)) {
                    map.put("cipher", r.getElementText());
                }
                if ("checksum".equals(name)) {
                    map.put("verify", r.getElementText());
                }
            }
            if ("gamblerInfo".equals(name)) {
                if ("gambler_name".equals(name)) {
                    map.put("lotNam", r.getElementText());
                }
                if ("gambler_balance".equals(name)) {
                    map.put("lotBal", r.getElementText());
                }
            }
        }
    }
    
    private void decode_209(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
        while(r.hasNext()){
            int typ = r.next();
            if(typ!=XMLStreamConstants.START_ELEMENT)
                continue;
            String name = r.getName().toString();
            if("return".equals(name)){
                map.put("rRspCod", r.getAttributeValue(0));
                map.put("rRspMsg", r.getAttributeValue(1));
            }
            if("gamblerBasicInfo".equals(name)) {
                if ("gambler_name".equals(name)) {
                    map.put("lotNam", r.getElementText());
                }
                if ("register_time".equals(name)) {
                    map.put("regTim", r.getElementText());
                }
                if ("email".equals(name)) {
                    map.put("email", r.getElementText());
                }
            }
            if("gamblerAdditionalInfo".equals(name)) {
                if ("city_id".equals(name)) {
                    map.put("cityId", r.getElementText());
                }
                if ("ID_type".equals(name)) {
                    map.put("lotIdTyp", r.getElementText());
                }
                if ("ID_no".equals(name)) {
                    map.put("idNo", r.getElementText());
                }
                if ("account_type".equals(name)) {
                    map.put("lotAccTyp", r.getElementText());
                }
                if ("charge_type".equals(name)) {
                    map.put("lotChgTyp", r.getElementText());
                }
                if ("prize_type".equals(name)) {
                    map.put("lotPrzTyp", r.getElementText());
                }
                if ("bindDealer".equals(name)) {
                    map.put("isDealId", "1");
                    map.put("dealId", r.getAttributeValue(1));
                }
                if ("bindCard".equals(name)) {
                    map.put("lotisBind", r.getAttributeValue(0));
                    if ("card_type".equals(name)) {
                        map.put("lotCrdTyp", r.getElementText());
                    }
                    if ("bank_id".equals(name)) {
                        map.put("lotBankId", r.getElementText());
                    }
                    if ("bank_card".equals(name)) {
                        map.put("crdNo", r.getElementText());
                    }
                }
                if ("real_name".equals(name)) {
                    map.put("cusNam", r.getElementText());
                }
                if ("sex".equals(name)) {
                    map.put("sex", r.getElementText());
                }
                if ("birthday".equals(name)) {
                    map.put("bthDay", r.getElementText());
                }
                if ("mobile".equals(name)) {
                    map.put("mobTel", r.getElementText());
                }
                if ("phone".equals(name)) {
                    map.put("fixTel", r.getElementText());
                }
                if ("gambler_status".equals(name)) {
                    map.put("status", r.getElementText());
                }
                if ("gambler_points".equals(name)) {
                    map.put("lotPot", r.getElementText());
                }
                if ("gambler_exp".equals(name)) {
                    map.put("lotExp", r.getElementText());
                }
                if ("gambler_exp_levelup".equals(name)) {
                    map.put("lotExpLvl", r.getElementText());
                }
            }
        }
    }

    private void decode_236(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
		List<Map<String,Object>> prizeItems = new ArrayList<Map<String,Object>>(); 
		List<Map<String,Object>> clses = new ArrayList<Map<String,Object>>(); 
		Map<String,Object> prizeItem = new HashMap<String,Object>();
		Map<String,Object> cls = new HashMap<String,Object>();
		while(r.hasNext()){
			int typ = r.next();
			if(typ!=XMLStreamConstants.START_ELEMENT)
				continue;
			String name = r.getName().toString();
			if("return".equals(name)){
				map.put("thdRspCde", r.getAttributeValue(0));
				map.put("resultDes", r.getAttributeValue(1));
			}else if("openPrize".equals(name)){
				map.put("gameNm", r.getAttributeValue(0));
				map.put("drawNm", r.getAttributeValue(1));
				map.put("kenoNm", r.getAttributeValue(2));
				map.put("strTim", r.getAttributeValue(3));
				map.put("stpTim", r.getAttributeValue(4));
				map.put("totPrz", r.getAttributeValue(5));
				map.put("jacPot", r.getAttributeValue(6));
			}else if("prize".equals(name)){
				map.put("opnTot", r.getAttributeValue(0));
			}else if("prizeItem".equals(name)){
				prizeItem = new HashMap<String,Object>();
				clses = new ArrayList<Map<String,Object>>(); 
				prizeItem.put("opnNum", r.getAttributeValue(0));
				prizeItem.put("bonCod", r.getAttributeValue(1));
				prizeItem.put("clsNum", r.getAttributeValue(2));
				prizeItem.put("clses", clses);
				prizeItems.add(prizeItem);
			}else if("class".equals(name)){
				cls = new HashMap<String,Object>();
				cls.put("clsNam", r.getAttributeValue(0));
				cls.put("bonAmt", r.getAttributeValue(1));
				cls.put("bonNum", r.getAttributeValue(2));
				clses.add(cls);
			}
		}
		map.put("prize", prizeItems);
	}
	private void decode_240(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
		List<Map<String,Object>> bonusItems = new ArrayList<Map<String,Object>>(); 
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>(); 
		Map<String,Object> bonusItem = new HashMap<String,Object>();
		Map<String,Object> node = new HashMap<String,Object>();
		while(r.hasNext()){
			int typ = r.next();
			if(typ!=XMLStreamConstants.START_ELEMENT)
				continue;
			String name = r.getName().toString();
			if("return".equals(name)){
				map.put("thdRspCde", r.getAttributeValue(0));
				map.put("resultDes", r.getAttributeValue(1));
			}else if("keno".equals(name)){
				map.put("gameId", r.getAttributeValue(0));
				map.put("drawId", r.getAttributeValue(1));
				map.put("kenoId", r.getAttributeValue(2));
			}else if("point".equals(name)){
				map.put("dump", r.getAttributeValue(0));
				map.put("cash", r.getAttributeValue(1));
				map.put("db", r.getAttributeValue(2));
			}else if("bounsItem".equals(name)){
				bonusItem = new HashMap<String,Object>();
				nodes = new ArrayList<Map<String,Object>>(); 
				bonusItem.put("txnLog", r.getAttributeValue(0));
				bonusItem.put("cipher", r.getAttributeValue(1));
				bonusItem.put("bigBon", r.getAttributeValue(2));
				bonusItem.put("totPrz", r.getAttributeValue(3));
				bonusItem.put("tLogNo", r.getAttributeValue(4));
				bonusItem.put("termId", r.getAttributeValue(5));
				bonusItem.put("nodes", nodes);
				bonusItems.add(bonusItem);
			}else if("bonusNode".equals(name)){
				node = new HashMap<String,Object>();
				node.put("betMod", r.getAttributeValue(0));
				node.put("betMul", r.getAttributeValue(1));
				node.put("class", r.getAttributeValue(2));
				node.put("przAmt", r.getAttributeValue(3));
				node.put("betLin", r.getAttributeValue(4));
				nodes.add(node);
			}
		}
		map.put("bonusItems", bonusItems);
	}
	private void decode_237(XMLStreamReader r,Map<String,Object> map) throws XMLStreamException{
		while(r.hasNext()){
			int typ = r.next();
			if(typ!=XMLStreamConstants.START_ELEMENT)
				continue;
			String name = r.getName().toString();
			if("return".equals(name)){
				map.put("thdRspCde", r.getAttributeValue(0));
				map.put("resultDes", r.getAttributeValue(1));
			}else if("file".equals(name)){
				map.put("file_name", r.getElementText());
			}
		}
	}
}
