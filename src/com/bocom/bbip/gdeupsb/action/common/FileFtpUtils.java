package com.bocom.bbip.gdeupsb.action.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.file.Transfer;
import com.bocom.bbip.file.transfer.ftp.FTPTransfer;
import com.bocom.bbip.thd.org.apache.commons.io.IOUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.CoreException;

/**
 * 
 * @category ftp获取文件
 * @description
 * @version 1.0
 * @create 
 * @author geyl
 * @editor
 */
public class FileFtpUtils{
	private static final Logger log = LoggerFactory.getLogger(FileFtpUtils.class);
	
	public static File getFtpFile(FTPTransfer transfer, String remoteFilePath,
			String localFilePath, String fleNme) throws CoreException {
		File localFile = null;
		try {
			transfer.logon();
//			transfer.setPassiveMode(true);
			Resource remoteFileRes = transfer.getResource(remoteFilePath + fleNme);
			localFile = new File(localFilePath + fleNme);
			OutputStream output = null;
			if (!localFile.exists()) {
				localFile.createNewFile();
			}
			output = new FileOutputStream(localFile);
			IOUtils.copy(remoteFileRes.getInputStream(), output);
			output.close();
		} catch (FileNotFoundException e) {
			log.error("System err by FileNotFoundException", e);
			throw new CoreException(ErrorCodes.EUPS_FAIL);
		} catch (IOException e) {
			log.error("System err by IOException", e);
			throw new CoreException(ErrorCodes.EUPS_FAIL);
		} finally {
			transfer.logout();
			log.info("ftp logout success");
		}
		return localFile;
	}
	
	
	public static void putFtpFile(Transfer transfer, String remoteFilePath,
			String localFilePath, String fleNme) throws CoreException {
		try {
			transfer.logon();
			Resource localFileRes = new FileSystemResource(localFilePath + fleNme);
			transfer.putResource(localFileRes, remoteFilePath, fleNme);
		} finally {
			transfer.logout();
			log.info("ftp logout success");
		}
	}
	
	
	public static int systemAndWait(String[] args, boolean wait) {
		if (args.length < 1) {
			log.error("参数少于一个");
		}
		for (int i = 0; i < args.length; i++) {
			if (StringUtils.isEmpty(args[i])) {
				log.error("第{}个参数为空", i + 1);
			}
		}

		Process p = null;
		String cmd = args[0];
		try {
			p = Runtime.getRuntime().exec(args);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("{}执行失败", cmd);
			return -1;
		}

		int ret = 0;
		if (wait) {// 同步等待执行结果
			try {
				ret = p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error("获取{}执行结果失败", cmd);
			}
		} else { // 不同步等待执行结果
			return 0;
		}
		InputStream errIs = p.getErrorStream();
		InputStream stdIs = p.getInputStream();
		try {
			int len = errIs.available();
			byte[] buf = null;
			if (len != 0) {
				buf = new byte[len];
				log.info("============err msg============");
				errIs.read(buf);
				log.info(new String(buf, 0, len));
			}

			len = stdIs.available();
			if (len != 0) {
				buf = new byte[len];
				log.info("============out msg===========");
				stdIs.read(buf);
				log.info(new String(buf, 0, len));
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("获取{}执行结果失败", cmd);
		}
		if (ret != 0) {
			log.info("{}执行返回结果为：{}", cmd, ret);
		}
		return ret;
	}

}
