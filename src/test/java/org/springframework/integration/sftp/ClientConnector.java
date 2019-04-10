package org.springframework.integration.sftp;

import org.springframework.integration.sftp.pojo.SftpTransferRequest;
import org.springframework.integration.sftp.pojo.SftpTransferResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class ClientConnector extends WebServiceGatewaySupport {

    public SftpTransferResponse transferFile(SftpTransferRequest request){
        return (SftpTransferResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    }
}
