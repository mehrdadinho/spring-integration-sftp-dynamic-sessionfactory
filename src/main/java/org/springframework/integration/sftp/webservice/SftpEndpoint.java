package org.springframework.integration.sftp.webservice;

import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.sftp.enums.RequestValidation;
import org.springframework.integration.sftp.enums.Result;
import org.springframework.integration.sftp.pojo.SftpTransferRequest;
import org.springframework.integration.sftp.pojo.SftpTransferResponse;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import utility.ApplicationException;

import java.io.File;
import java.io.IOException;

/**
 * The Basic EndPoint of web service that every time that web service was called from outside,
 * this endpoint listening to every request that come to it and response to them.
 *
 * @author  Mehrdad Peykari
 * @version 1.0
 * @since   2019-02-01
 */
@Endpoint
public class SftpEndpoint
{
    private static final String NAMESPACE_URI = "http://www.springframework.org/integration/sftp/org.springframework.pojo";
    private static final Logger logger = LoggerFactory.getLogger(SftpEndpoint.class);

    @Autowired
    SftpUtil sftpUtil;

    public SftpEndpoint() {
    }

    /**
     * this is a only method of the webservice. This method get a string and save it to SFTP
     * server depend on income parameters.
     *
     * @param request the request object that receive from outside.
     * request includes:
     *            <ul>
     *                <li> host: host name of SFTP Server </li>
     *                <li> username </li>
     *                <li> password </li>
     *                <li> fileName: input string saved in a file with this name </li>
     *                <li> fileContent: the goal string </li>
     *                <li> filePath: SFTP server path to save file on it </li>
     *            </ul>
     * @return the result of process contain resCode and message that will be:
     *            <ul>
     *                <li>  1 : successful</li>
     *                <li> -100 : IOException</li>
     *                <li> -110 : InterruptedException</li>
     *                <li> -120 : Connection Refused on host: </li>
     *                <li> -130 : Invalid Username or Password on host: </li>
     *                <li> -999 : Unknown Exception</li>
     *            </ul>
     *
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SftpTransferRequest")
    @ResponsePayload
    public SftpTransferResponse transferFile(@RequestPayload SftpTransferRequest request) {
        logger.trace("host:"+request.getHost()+",username:"+request.getUsername()+",password:"+request.getPassword()+
                ",fileName:"+request.getFileName()+",fileContent:"+request.getFileContent(),",filePath:"+request.getFilePath());
        SftpTransferResponse response = new SftpTransferResponse();
        File file = null;
        try {
            RequestValidation validateResult = sftpUtil.validateRequestParameters(request,response);
            if (!validateResult.equals(RequestValidation.Validate)){
                throw new ApplicationException(validateResult);
            }

            file = sftpUtil.createFile(request.getFileContent(),System.getProperty("java.io.tmpdir"),request.getFileName());
            logger.debug("new file created:"+file.getPath());
            String result;
            logger.info("start checking encryption");

            result = sftpUtil.sendDataToHost(file,request.getFileName(),request.getHost(),request.getFilePath(),request.getUsername(),request.getPassword());
            logger.info("file transferred with result:"+result);
            response = sftpUtil.setResultToResponse(response, Result.Success);
            logger.trace("return response result:"+response.getResCode());
            return response;
        } catch (ApplicationException e) {
            response = sftpUtil.setResultToResponse(response,e.getValidation());
            logger.error("exception occur:"+e.getMessage());
            logger.trace("return response result:"+response.getResCode());
            e.printStackTrace();
            return response;
        } catch (IOException e) {
            response = sftpUtil.setResultToResponse(response,Result.IOException);
            logger.error("exception occur:"+e.getMessage());
            logger.trace("return response result:"+response.getResCode());
            e.printStackTrace();
            return response;
        } catch (InterruptedException e) {
            response = sftpUtil.setResultToResponse(response,Result.InterruptedException);
            logger.error("exception occur:"+e.getMessage());
            logger.trace("return response result:"+response.getResCode());
            e.printStackTrace();
            return response;
        } catch (MessageHandlingException e) {
            if(e.getRootCause() instanceof JSchException){
                if(e.getRootCause().getMessage().contains("timeout")){
                    response = sftpUtil.setResultToResponse(response,Result.ConnectionException,request.getHost());
                    logger.error("exception occur:"+e.getRootCause().getMessage());
                    logger.trace("return response result:"+response.getResCode());
                }else if(e.getRootCause().getMessage().contains("authentication")){
                    response = sftpUtil.setResultToResponse(response,Result.InvalidUsernameOrPassword,request.getHost());
                    logger.error("exception occur:"+e.getMessage());
                    logger.trace("return response result:"+response.getResCode());
                }else{
                    response = sftpUtil.setResultToResponse(response,Result.ConnectionException,request.getHost());
                    logger.error("exception occur:"+e.getMessage());
                    logger.trace("return response result:"+response.getResCode());
                }
            }
            e.printStackTrace();
            return response;
        } catch (Exception e) {
            response = sftpUtil.setResultToResponse(response,Result.UnknownException);
            logger.error("exception occur:"+e.getMessage());
            logger.error("UNKNOWN EXCEPTION IS:"+e.getClass().getName());
            logger.trace("return response result:"+response.getResCode());
            e.printStackTrace();
            return response;
        }finally {
            if (file != null){
                sftpUtil.removeFile(file);
            }
        }
    }
}
