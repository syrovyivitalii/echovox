package syrovyi.vitalii.echovox.common.exception.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 Bad Request
    INVALID_FORMAT("400-002", "Invalid data format", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("400-003", "Validation failed", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("400-004", "Required parameter is missing", HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_TYPE_MISMATCH("400-005", "Method argument type mismatch", HttpStatus.BAD_REQUEST),
    IO_ERROR("400-006", "I/O Error", HttpStatus.INTERNAL_SERVER_ERROR),

    // 404 Not Found
    NOT_FOUND("404-000", "Resource not found", HttpStatus.NOT_FOUND),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED("405-000", "HTTP method not allowed for this resource", HttpStatus.METHOD_NOT_ALLOWED),

    // 409 Conflict
    ALREADY_EXISTS("409-001", "Resource already exists", HttpStatus.CONFLICT),

    // 5xx Server Errors
    INTERNAL_SERVER_ERROR("500-000", "An unexpected internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String defaultDescription;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String defaultDescription, HttpStatus httpStatus) {
        this.code = code;
        this.defaultDescription = defaultDescription;
        this.httpStatus = httpStatus;
    }
}
