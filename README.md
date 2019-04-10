# spring-integration-sftp-dynamic-sessionfactory

The main goal of this project is creating dynamic session factory for sftp connections using spring integration,
and build with Spring Boot, Maven, Spring Integration and Spring webService.

This is a webservice that get a String and saved it as a File into specific Host through Sftp connection.

When a new request come to webservice, it looking for existing session factory with 'username@Host:Password' key.
if session factory with this properties does not found, it create a new session factory with 'username@Host:Password' key
and add to `FactoryLocator` in `DelegationSessionFactory`.

# Objects:

**Request parameters:** 
`Host`:     host of destination. example: 192.168.10.10
`Username`: username of the Host 
`password`: password of the Host
`fileName`: name of the file that will saved on the Host
`filePath`: path of the file that will saved on the Host
`fileContent`: content of the file

**Response parameters:** 
`resCode`: result code of the operation
`message`: message of the result
