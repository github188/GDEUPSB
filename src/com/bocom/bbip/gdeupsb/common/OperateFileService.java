package com.bocom.bbip.gdeupsb.common;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.file.fmt.FileMarshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * @category 解析文件类
 * @author Administrator
 *
 */
public class OperateFileService extends BaseAction implements ApplicationContextAware{
	private static final Logger logger = LoggerFactory.getLogger(OperateFileService.class);
	@Autowired
	private Resource resource;
	@Autowired
	private FileMarshaller marshaller;
	@SuppressWarnings("unchecked")
	public Map<String,Object> pareseFile(EupsThdFtpConfig eupsThdFtpConfig, String fileId)
			throws CoreException, CoreRuntimeException
			{
		logger.info((new StringBuilder("this is path:")).append(TransferUtils.resolveFilePath(eupsThdFtpConfig.getLocDir().trim(), eupsThdFtpConfig.getLocFleNme().trim())).toString());
		Map<String,Object> map = new HashMap<String,Object>();
		try
		{
			resource = new FileSystemResource(TransferUtils.resolveFilePath(eupsThdFtpConfig.getLocDir().trim(), eupsThdFtpConfig.getLocFleNme().trim()));
			map = marshaller.unmarshal(fileId, resource, Map.class);
		}
		catch(JumpException e)
		{
			logger.error("BBIP0004EU0015");
			throw new CoreException("BBIP0004EU0015");
		}
		return map;
			}

	@SuppressWarnings("unchecked")
	public Map<String,Object> pareseFileByPath(String filePath, String fileName, String fileId)
			throws CoreException, CoreRuntimeException
			{
		logger.info((new StringBuilder("this is path:")).append(TransferUtils.resolveFilePath(filePath, fileName)).toString());
		Map<String,Object> map = new HashMap<String,Object>();
		try
		{
			resource = new FileSystemResource(TransferUtils.resolveFilePath(filePath, fileName));
			map = marshaller.unmarshal(fileId, resource, Map.class);
		}
		catch(JumpException e)
		{
			logger.error("BBIP0004EU0015");
			throw new CoreException("BBIP0004EU0015");
		}
		return map;
			}
}
