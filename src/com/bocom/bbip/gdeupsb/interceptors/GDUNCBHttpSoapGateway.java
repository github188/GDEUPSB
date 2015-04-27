/*@(#)GDMOBBHttpSoapGateway.java   2015-4-24 
 * Copy Right 2015 Bank of Communications Co.Ltd.
 * All Copyright Reserved
 */

package com.bocom.bbip.gdeupsb.interceptors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Gateway;
import com.bocom.jump.bp.util.Base64;

/**
 * TODO Document GDMOBBHttpSoapGateway
 * <p>
 * @version 1.0.0,2015-4-24
 * @author Administrator
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class GDUNCBHttpSoapGateway implements Gateway{

	protected Logger log = LoggerFactory.getLogger(GDUNCBHttpSoapGateway.class);
	private final static String errTranCde = "errors";
	private String url;
	private int timeOut;
	private String charSet;
	private String transCode;

	private String desCharSet = "GB18030";
	public void setUrl(String url) {
		this.url = url;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public String getTransCode() {
		return transCode;
	}

	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}

	@Override
	public byte[] send(Object arg0, String arg1) throws CommunicationException {
		return null;
	}

	@Override
	public byte[] receive(Object obj, String s) throws CommunicationException {
		return null;
	}

	@Override
	public byte[] sendAndReceive(Object arg0, String arg1)
			throws CommunicationException {

		log.info("==============transCode" + transCode);
		if (StringUtils.isEmpty(charSet)) {
			charSet = "UTF-8";
		}
		log.info("charSet : " + charSet);

		log.info("url : " + url);

		log.info("come in ws sendAndReceive, url = " + url);
		StringBuffer newMess = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		/*newMess =newMess.append
				("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope\"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");*/
		String msg = null;
		if (arg0 instanceof byte[]) {
			try {
				msg = new String((byte[]) arg0, charSet);
				/*msg = msg.substring(msg.indexOf("<soapenv:Body>"),
						msg.indexOf("</root>"));*/
				if(msg.indexOf("<qryUserProInfoREQ>")>0){
					url=url+"gdip/cxf/UserInfoService:qryUserProInfo";
					msg = msg.substring(msg.indexOf("<qryUserProInfoREQ>"),
							msg.indexOf("</qryUserProInfo>"));
					log.info("old msg：" + msg);
					//msg= msg.replace("<qryUserProInfo>","<qryUserProInfo xmlns=\"http://userInfoService.service.protocol.cxf.linkage.com/\">");
					msg = msg.replace("<qryUserProInfoREQ>", "<com.bankcomm.bcm.modules.unicom.model.QryUserProInfoREQ>");
					msg= msg.replace("</qryUserProInfoREQ>","</com.bankcomm.bcm.modules.unicom.model.QryUserProInfoREQ>");
				}else{
					url=url+"gdip/cxf/AcctInfoService:acctInfoChange";
					msg = msg.substring(msg.indexOf("<acctInfoChangeREQ>"),
							msg.indexOf("</acctInfoChange>"));
					log.info("old msg：" + msg);
					//msg= msg.replace("<acctInfoChange>","<acctInfoChange xmlns=\"http://acctInfoService.service.protocol.cxf.linkage.com/\">");
					msg = msg.replace("<acctInfoChangeREQ>", "<com.bankcomm.bcm.modules.unicom.model.AcctInfoChangeREQ>");	
					msg = msg.replace("</acctInfoChangeREQ>", "</com.bankcomm.bcm.modules.unicom.model.AcctInfoChangeREQ>");	
				}
			
				newMess.append(msg);
			} catch (UnsupportedEncodingException e) {
				log.error("Encoding change error");
				throw new CommunicationException(false, e.getMessage());
			}
		} else {
			log.error("报文类型错误");
			return null;
		}
		//newMess.append("</soapenv:Envelope>");
		msg = newMess.toString();
		log.info("req msg : " + msg);

		String response = "";
		try {
			URL _url = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", "IBS");
			conn.setRequestProperty("Content-Type",
					"application/soap+xml;charset=UTF-8;action=LSSB");
			conn.setRequestProperty("Content-Length",
					String.valueOf(msg.getBytes(charSet).length));
			conn.setConnectTimeout(timeOut * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			// conn.setUseCaches(false);
			conn.connect();
			log.info("conn ok");

			OutputStream os = conn.getOutputStream();
			os.write(msg.getBytes(charSet));
			os.flush();
			log.info("send ok and resHeader is " + conn.getHeaderFields());

			InputStream is = null;
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, charSet));

				String readLine = null;
				while ((readLine = br.readLine()) != null) {
					response = response + readLine;
				}
				log.info("rsp msg ：" + response);
				response = response.substring(
						response.indexOf("<soapenv:Body>"),
						response.indexOf("</soapenv:Envelope>"));
				String func = response.substring(
						response.indexOf("soap-rpc\">") + 10,
						response.indexOf("</ns2:result>"));
				String encStr = response.substring(
						response.indexOf("xsd:string\">") + 12,
						response.indexOf("</" + func));
				String resStr = new String(Base64.decode(encStr), desCharSet);
				resStr = resStr.substring(resStr.indexOf("<XML>"));
				String ret = "0";
				if (resStr.contains("errorInfo")) {
					// 返回错误
					ret = "-1";
					transCode = errTranCde;
				} /*else if ("0".equals(HiXmlUtil.getXmlNodValue(resStr,
						"totalCount"))) {
					// 数据为空
					ret = "2";
				}
				resStr = HiXmlUtil.setXmlNodVal(resStr, "errCde", ret);*/
				StringBuffer resMess = new StringBuffer(transCode);
				resMess.append("<root>");
				resMess.append(resStr);
				resMess.append("</root>");
				response = resMess.toString();
				log.info("==============response:" + response);
			} else {
				log.error("ws return code = " + conn.getResponseCode()
						+ "  err msg：" + conn.getResponseMessage());
				throw new CommunicationException(false,
						conn.getResponseMessage());
			}

			if (os != null) {
				os.close();
			}
			if (is != null) {
				is.close();
			}
			conn.disconnect();
			log.info("conn closed");
		} catch (Exception e) {
			log.error("ws err：", e);
			throw new CommunicationException(false, e.getMessage());
		}

		try {
			return response.getBytes(charSet);
		} catch (UnsupportedEncodingException e) {
			throw new CommunicationException(false, e.getMessage());
		}
	}
}