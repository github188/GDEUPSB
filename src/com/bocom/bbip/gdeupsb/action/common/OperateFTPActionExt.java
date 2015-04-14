package com.bocom.bbip.gdeupsb.action.common;




import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.file.transfer.ftp.FTPTransfer;
import com.bocom.bbip.utils.IOUtils;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 获取FTP相关信息.登陆FTP取指定路径FTP文件.上传文件到FTP指定路径.
 * @version 1.0.0,2014-2-28
 * @author kejy.sdc
 * @since 1.0.0
 */
public class OperateFTPActionExt{
    
    private static final Logger logger = LoggerFactory.getLogger(OperateFTPActionExt.class);

    /** 文件源 */
    private Resource resource;
    
    /**FTP组件*/
    private FTPTransfer ftpTransfer;
    /**O
     * 获取FTP信息
     * @param context
     * @return
     * @throws CoreException 
     */
    public EupsThdFtpConfig getFTPInfo(String ftpNo,EupsThdFtpConfigRepository eupsThdFtpConfigRepository) throws CoreException{
        EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne(ftpNo);
        if (null == eupsThdFtpConfig) {
            logger.error("can not find the FTP infromation for ftpNo");
            throw new CoreException(ErrorCodes.EUPS_FTP_INFO_NOTEXIST);
        }
        return eupsThdFtpConfig;
    }
    
    /**
     * FTP登陆
     * @return
     * @throws CoreException
     * @throws CoreRuntimeException
     */
    public FTPTransfer loginFTP(EupsThdFtpConfig eupsThdFtpConfig) throws CoreException, CoreRuntimeException {
        FTPTransfer ftpTransfer = new FTPTransfer();
        ftpTransfer.setHost(eupsThdFtpConfig.getThdIpAdr().trim());
        ftpTransfer.setPort(Integer.valueOf(eupsThdFtpConfig.getBidPot().trim()));
        ftpTransfer.setUserName(eupsThdFtpConfig.getOppNme().trim());
        ftpTransfer.setPassword(eupsThdFtpConfig.getOppUsrPsw().trim());
        ftpTransfer.setControlEncoding("GBK");
        try {
            ftpTransfer.logon();
            logger.info("FTP login success!");
        } catch (Exception e) {
            logger.error("FTP login fail:"+ e.getMessage());
            throw new CoreException(ErrorCodes.EUPS_FTP_LOGIN_FAIL);
        }
        return ftpTransfer;
    }
    
    /**
     * FTP登陆设置编码格式，默认为GBK
     * @return
     * @throws CoreException
     * @throws CoreRuntimeException
     */
    public FTPTransfer loginFTPSetEncod(EupsThdFtpConfig eupsThdFtpConfig) throws CoreException, CoreRuntimeException {
        FTPTransfer ftpTransfer = new FTPTransfer();
        ftpTransfer.setHost(eupsThdFtpConfig.getThdIpAdr().trim());
        ftpTransfer.setPort(Integer.valueOf(eupsThdFtpConfig.getBidPot().trim()));
        ftpTransfer.setUserName(eupsThdFtpConfig.getOppNme().trim());
        ftpTransfer.setPassword(eupsThdFtpConfig.getOppUsrPsw().trim());
        ftpTransfer.setControlEncoding("GBK");
        try {
            ftpTransfer.logon();
            logger.info("FTP login success!");
        } catch (Exception e) {
            logger.error("FTP login fail:"+ e.getMessage());
            throw new CoreException(ErrorCodes.EUPS_FTP_LOGIN_FAIL);
        }
        return ftpTransfer;
    }
    
    /**
     * 从FTP取文件
     * 1.登陆FTP
     * 2.获取FTP服务器上的文件源
     * 3.在本地建立一个空文件
     * 4.将文件复制到本地文件中
     * 5.退出FTP
     * @throws CoreException 
     * @throws CoreRuntimeException 
     */
    public void getFileFromFtp(EupsThdFtpConfig eupsThdFtpConfig) throws CoreRuntimeException, CoreException {
        /* Modify by metoo for the database column name changed*/
        /*if(Constants.FTP_THIRD_OPR_UPLOAD.equals(eupsThdFtpConfig.getTyp())){*/
        if(Constants.FTP_THIRD_OPR_DOWNLOAD.equals(eupsThdFtpConfig.getFtpDir())){
            try {
                ftpTransfer = loginFTP(eupsThdFtpConfig);
                resource = ftpTransfer.getResource(eupsThdFtpConfig.getRmtWay().trim(), eupsThdFtpConfig.getRmtFleNme().trim());
                if (resource.exists()) {
                    OutputStream fos = new FileOutputStream(new File(eupsThdFtpConfig.getLocDir().trim(), eupsThdFtpConfig.getRmtFleNme().trim()));
                    IOUtils.copy(resource.getInputStream(), fos);
                    fos.close();
                }else{
                    logger.error("Download file resource is null!");
                    throw new CoreException(ErrorCodes.EUPS_FTP_FILEDOWN_FAIL);
                }
            } catch (Exception e) {
                logger.error("Download file error happen : ", e.getMessage());
                throw new CoreException(ErrorCodes.EUPS_FTP_FILEDOWN_FAIL);
            } finally {
                ftpTransfer.logout();
            }
        }else{
            logger.error("Download file fail, type is not Download ! ");
            throw new CoreException(ErrorCodes.EUPS_FTP_FILE_TYPE_ERROR);
        }
    }
    
    
    
    /**
     * 上传文件到FTP
     * 1.登陆FTP
     * 2.得到本地文件的文件源
     * 3.上传文件源到FTP服务器上
     * 4.退出FTP
     * @param 
     */
    public void putCheckFile(EupsThdFtpConfig eupsThdFtpConfig) throws CoreRuntimeException, CoreException {
        /* Modify by metoo for the database column name changed*/
    	/*if(Constants.FTP_THIRD_OPR_UPLOAD.equals(eupsThdFtpConfig.getTyp())){*/
    	if(Constants.FTP_THIRD_OPR_UPLOAD.equals(eupsThdFtpConfig.getFtpDir())){
            try {
                ftpTransfer = loginFTP(eupsThdFtpConfig);
                Resource resource = new FileSystemResource(TransferUtils.resolveFilePath(eupsThdFtpConfig.getLocDir().trim(), eupsThdFtpConfig.getLocFleNme().trim()));
                if(resource.exists()){
                    ftpTransfer.putResource(resource, eupsThdFtpConfig.getRmtWay().trim(),eupsThdFtpConfig.getLocFleNme().trim());
                }else {
                    logger.error("Put file resource is null ");
                    throw new CoreException(ErrorCodes.EUPS_FTP_FILEPUT_NFAIL); 
                }
            } catch (Exception e) {
                logger.error("Put file error happen : " + e.getMessage());
                throw new CoreException(ErrorCodes.EUPS_FTP_FILEPUT_NFAIL);
            } finally {
                if(ftpTransfer != null){
                    ftpTransfer.logout();
                }
            }
        }else{
            logger.error("Put file fail, type is not upLoad ! ");
            throw new CoreException(ErrorCodes.EUPS_FTP_FILE_TYPE_ERROR);
        }
    }
     
}
        
