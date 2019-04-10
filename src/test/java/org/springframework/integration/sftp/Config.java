package org.springframework.integration.sftp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class Config {

    @Bean
    public Jaxb2Marshaller getMarshaller(){
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("org.springframework.integration.sftp.pojo");
        return marshaller;
    }

    @Bean
    public ClientConnector clientConnector(){
        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setMarshaller(getMarshaller());
        clientConnector.setUnmarshaller(getMarshaller());
        clientConnector.setDefaultUri("http://localhost:8090/service/sftp.wsdl");
        return clientConnector;
    }

}
