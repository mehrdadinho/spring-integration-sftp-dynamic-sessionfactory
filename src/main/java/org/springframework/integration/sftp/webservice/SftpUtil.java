package org.springframework.integration.sftp.webservice;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.file.remote.RemoteFileTemplate;
import org.springframework.integration.file.remote.session.DefaultSessionFactoryLocator;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.enums.BaseEnum;
import org.springframework.integration.sftp.enums.RequestValidation;
import org.springframework.integration.sftp.pojo.SftpTransferRequest;
import org.springframework.integration.sftp.pojo.SftpTransferResponse;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.UnexpectedException;

/**
 * @author  Mehrdad Peykari
 * @version 1.0
 * @since   2019-02-01
 */
@Component
public class SftpUtil {

    private static final Logger logger = LoggerFactory.getLogger(SftpUtil.class);
    private ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("/META-INF/spring/integration/SftpFirstOutboundTransfer-context.xml",SftpUtil.class);
    /**
     * the main goal of this method is creating a new file with name and content from parameters
     * @param content content of file.
     * @param path local path to save file on it.
     * @param fileName name of the file.
     * @return created file.
     *
     */
    public File createFile(String content,String path,String fileName) throws IOException {
        logger.trace("content:"+content+",path:"+path+",fileName:"+fileName);
        File file = new File(path+"/"+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content.getBytes());
        fos.flush();
        fos.close();
        logger.trace("return:"+file);
        return file;
    }
    /**
     * this method send string content to sftp server through spring integration
     * @param file a file that send via message.
     * @param fileName name of the file.
     * @param host URL of Sftp server.
     * @param savingPath a path from Sftp server to save file on it.
     * @param username username of Sftp server.
     * @param password password of Sftp server.
     * @return the result of transferring file to Sftp server.
     *          "1" : successful.
     */
    public String sendDataToHost(File file, String fileName, String host, String savingPath, String username, String password) throws Exception {
        logger.trace("file:"+file+",fileName:"+fileName+",host:"+host+",directory:"+savingPath);

        try {
            createOrSetSessionFactory(host,username,password);

            Message<File> message = MessageBuilder.withPayload(file)
                    .setHeader("host", host)
                    .setHeader("fileName", fileName)
                    .setHeader("directory", savingPath).build();

            MessageChannel requestChannel = context.getBean("inputChannel", MessageChannel.class);
            requestChannel.send(message);
            logger.trace("return:" + 1);
            return "1";
        }catch(Exception e){
                e.printStackTrace();
                throw e;
        }
    }
    /**
     * removing file that coming from parameter.
     */
    public boolean removeFile(File file) {
        logger.trace("file:"+file);
        return file.delete();
    }

    /**
     * this method set the resCode and Message to the Response.
     * @param response SftpTransferResponse .
     * @param result result Enum.
     * @return response.
     *          "1" : successful.
     */
    public SftpTransferResponse setResultToResponse(SftpTransferResponse response,BaseEnum result){
        return this.setResultToResponse(response,result,"");
    }
    public SftpTransferResponse setResultToResponse(SftpTransferResponse response, BaseEnum result, String additionalMessage){
        response.setResCode(result.getResCode());
        response.setMessage(result.getMessage()+additionalMessage);
        return response;
    }

    /**
     * this method looking for session factory with key 'username@Host:password'. if it does not exist, create a new session factory.
     * @param host URL of Sftp server.
     * @param username username of Sftp server.
     * @param password password of Sftp server.
     *          "1" : successful.
     */
    public void createOrSetSessionFactory(String host, String username, String password){
        DelegatingSessionFactory<ChannelSftp.LsEntry> dsf = context.getBean("dsf", DelegatingSessionFactory.class);
        DefaultSessionFactoryLocator<ChannelSftp.LsEntry> factoryLocator = (DefaultSessionFactoryLocator<ChannelSftp.LsEntry>) dsf.getFactoryLocator();
        SessionFactory<ChannelSftp.LsEntry> existedFactory = factoryLocator.getSessionFactory(username+"@"+host+":"+password);
        if(existedFactory==null){
            logger.info("create new session factory:"+host);
            DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
            factory.setHost(host);
            factory.setUser(username);
            factory.setPassword(password);
            factory.setAllowUnknownKeys(true);
            factory.setTimeout(3000);
            SessionFactory<ChannelSftp.LsEntry> newSessionFactory = factory;
            factoryLocator.addSessionFactory(username+"@"+host+":"+password,newSessionFactory); // unique key for sessionFactory is: "username@host:password".
        }

        dsf.setThreadKey(username+"@"+host+":"+password);
    }
    public RequestValidation validateRequestParameters(SftpTransferRequest request, SftpTransferResponse response){
        if(request == null){
            return RequestValidation.NullRequest;
        }
        if(request.getHost() == null || request.getHost().isEmpty()){
            return RequestValidation.NullHost;
        }
        if(request.getUsername() == null || request.getUsername().isEmpty()){
            return RequestValidation.NullUsername;
        }
        if(request.getPassword() == null || request.getPassword().isEmpty()){
            return RequestValidation.NullPassword;
        }
        if(request.getFileName() == null || request.getFileName().isEmpty()){
            return RequestValidation.NullFileName;
        }
        if(request.getFileContent() == null || request.getFileContent().isEmpty()){
            return RequestValidation.NullFileContent;
        }
        if(request.getFilePath() == null || request.getFilePath().isEmpty()){
            return RequestValidation.NullFilePath;
        }

        // validation file name characters
        String tmp = request.getFileName().replaceAll("[:\\\\/*?|<>\"]", "_");
        if(!request.getFileName().equals(tmp)){
            return RequestValidation.InvalidFileNameCharacter;
        }
        //validation file path characters
        tmp = request.getFilePath().replaceAll("[:\\\\*?|<>\"]", "_");
        if(!request.getFilePath().equals(tmp)){
            return RequestValidation.InvalidFilePathCharacter;
        }
        return RequestValidation.Validate;
    }
}
