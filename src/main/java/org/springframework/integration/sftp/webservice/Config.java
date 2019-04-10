package org.springframework.integration.sftp.webservice;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * @author  Mehrdad Peykari
 * @version 1.0
 * @since   2019-02-01
 */
@EnableWs
@Configuration
public class Config extends WsConfigurerAdapter
{

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext)
    {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/service/*");// path of webservice URL
    }

    @Bean(name = "sftp") // name of WSDL after. Here webservice URL is: localhost:8090/service/sftp.wsdl
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema schema)
    {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("sftp");
        wsdl11Definition.setLocationUri("/service/sftp");
        wsdl11Definition.setTargetNamespace("http://www.spingframework.org/integration/sftp");
        wsdl11Definition.setSchema(schema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema schema()
    {
        return new SimpleXsdSchema(new ClassPathResource("sftpTransfer.xsd"));
    }

}
