package utility;

import org.springframework.integration.sftp.enums.RequestValidation;

/**
 * @author  Mehrdad Peykari
 * @version 1.0
 * @since   2019-02-01
 */
public class ApplicationException extends RuntimeException {

    private RequestValidation validation;

    public RequestValidation getValidation() {
        return validation;
    }

    public void setValidation(RequestValidation validation) {
        this.validation = validation;
    }

    public ApplicationException(RequestValidation validation) {
        super(validation.getMessage());
        this.validation=validation;
    }
}
