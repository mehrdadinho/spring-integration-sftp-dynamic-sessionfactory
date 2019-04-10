package org.springframework.integration.sftp;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.integration.sftp.pojo.SftpTransferRequest;
import org.springframework.integration.sftp.pojo.SftpTransferResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * @author  mehrdad.peykari
 * @apiNote run the application before starting test cases.
 * @since   2019-02-01
 **/
public class ApplicationTests {

	@Test
	public void sendDataToWebService() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		ClientConnector client = context.getBean(ClientConnector.class);
		SftpTransferRequest request = new SftpTransferRequest();
		request.setHost("192.168.10.10");
        request.setUsername("username");
        request.setPassword("password");
		request.setFileName("fileName");
		request.setFileContent("content of my file.");
		request.setFilePath("sendDataToWebService/");
        SftpTransferResponse response = client.transferFile(request);
		System.out.println("Response message:"+response.getMessage());
		assertEquals(1,response.getResCode());
	}

	@Test
	public void sendFileAsString() {
		File file = new File("D:/sftp/3MB.txt"); //set a file path HERE.
		StringBuilder generated = new StringBuilder((int)file.length());

		try (Scanner scanner = new Scanner(file)) {
			while(scanner.hasNextLine()) {
				generated.append(scanner.nextLine() + System.lineSeparator());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
		ClientConnector client = context.getBean(ClientConnector.class);
		SftpTransferRequest request = new SftpTransferRequest();
		request.setHost("192.168.10.10");
		request.setUsername("username");
		request.setPassword("password");
		request.setFilePath("sendFileAsString/");
		request.setFileName(file.getName());
		request.setFileContent(generated.toString());
		SftpTransferResponse response = client.transferFile(request);
		System.out.println("Response message:"+response.getMessage());
		assertEquals(1,response.getResCode());
	}

}

