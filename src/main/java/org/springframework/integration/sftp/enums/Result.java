package org.springframework.integration.sftp.enums;

/**
 * @author  Mehrdad Peykari
 * @version 1.0
 * @since   2019-02-01
 */
public enum Result implements BaseEnum {
    Success(1,"file moved successfully"),
    IOException(-100,"Could Not write file"),
    InterruptedException(-110,"InterruptedException"),
    ConnectionException(-120,"Connection Refused on host: "),
    InvalidUsernameOrPassword(-130,"Invalid Username or Password on host: "),
    UnknownException(-999,"Unknown Exception");

    private int resCode;
    private String message;


    Result(int resCode, String message) {
        this.resCode=resCode;
        this.message=message;
    }

    public int getResCode() {
        return resCode;
    }
    public String getMessage() {
        return message;
    }

}
