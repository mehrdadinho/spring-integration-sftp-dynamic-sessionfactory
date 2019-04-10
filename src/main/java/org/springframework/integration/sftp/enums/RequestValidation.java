package org.springframework.integration.sftp.enums;

/**
 * @author  Mehrdad Peykari
 * @version 1.0
 * @since   2019-02-01
 */
public enum RequestValidation implements BaseEnum {
    Validate(0,"Validate Request informations"),
    NullRequest(-201,"Request is null"),
    NullHost(-202,"Host is null"),
    NullUsername(-203,"Username is null"),
    NullPassword(-204,"Password is null"),
    NullFileName(-205,"File Name is null"),
    NullFileContent(-206,"File Content is null"),
    NullFilePath(-207,"File Path is null"),
    InvalidFileNameCharacter(-220,"File Name Contain invalid character(s)"),
    InvalidFilePathCharacter(-221,"File Path Contain invalid character(s)");

    private int resCode;
    private String message;


    RequestValidation(int resCode, String message) {
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
