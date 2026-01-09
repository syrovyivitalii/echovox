package syrovyi.vitalii.echovox.common.exception.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 Bad Request
    BAD_REQUEST("400-000", "Bad Request", HttpStatus.BAD_REQUEST),
    ILLEGAL_ARGUMENT("400-001", "Illegal argument provided", HttpStatus.BAD_REQUEST),
    INVALID_FORMAT("400-002", "Invalid data format", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("400-003", "Validation failed", HttpStatus.BAD_REQUEST),
    MISSING_PARAMETER("400-004", "Required parameter is missing", HttpStatus.BAD_REQUEST),
    METHOD_ARGUMENT_TYPE_MISMATCH("400-005", "Method argument type mismatch", HttpStatus.BAD_REQUEST),
    IO_ERROR("400-006", "I/O Error", HttpStatus.INTERNAL_SERVER_ERROR),

    // 403 Forbidden
    FORBIDDEN("403-000", "Access Denied", HttpStatus.FORBIDDEN),

    // 404 Not Found
    NOT_FOUND("404-000", "Resource not found", HttpStatus.NOT_FOUND),

    // 405 Method Not Allowed
    METHOD_NOT_ALLOWED("405-000", "HTTP method not allowed for this resource", HttpStatus.METHOD_NOT_ALLOWED),

    // 409 Conflict
    RESOURCE_CONFLICT("409-000", "Resource conflict", HttpStatus.CONFLICT),
    ALREADY_EXISTS("409-001", "Resource already exists", HttpStatus.CONFLICT), // If 406 was meant to be Conflict

    // 5xx Server Errors
    INTERNAL_SERVER_ERROR("500-000", "An unexpected internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_GATEWAY("502-000", "Bad Gateway: Invalid response from upstream server", HttpStatus.BAD_GATEWAY),
    SERVICE_UNAVAILABLE("503-000", "Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    GATEWAY_TIMEOUT("504-000", "Gateway Timeout: Upstream server did not respond in time", HttpStatus.GATEWAY_TIMEOUT);


    private final String code;
    private final String defaultDescription;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String defaultDescription, HttpStatus httpStatus) {
        this.code = code;
        this.defaultDescription = defaultDescription;
        this.httpStatus = httpStatus;
    }
}
