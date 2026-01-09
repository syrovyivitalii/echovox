package syrovyi.vitalii.echovox.common.exception;

import syrovyi.vitalii.echovox.common.exception.dto.Error;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import syrovyi.vitalii.echovox.common.exception.dto.ErrorResponse;
import syrovyi.vitalii.echovox.common.exception.dto.FormValidationError;
import syrovyi.vitalii.echovox.common.exception.enums.ErrorCode;
import syrovyi.vitalii.echovox.common.exception.exception.ClientBackendException;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ClientBackendException.class)
    public ResponseEntity<Object> handleClientBackendException(ClientBackendException ex, WebRequest request) {
        logException(ex);
        return buildErrorResponse(ex, ex.getErrorCode(), ex.getOverrideMessage(), request);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());
        List<Error> validationErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> FormValidationError.builder()
                        .code(ErrorCode.VALIDATION_ERROR.getCode())
                        // .label(ErrorCode.VALIDATION_ERROR.name())
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .rejectedValue(fieldError.getRejectedValue())
                        .build())
                .collect(Collectors.toList());

        return buildErrorResponse(ex, ErrorCode.VALIDATION_ERROR, "Validation failed", validationErrors, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.warn("Method argument type mismatch: Parameter '{}' requires type '{}' but value was '{}'",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "N/A", ex.getValue());
        String message = String.format("Parameter '%s' should be of type '%s'",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "N/A");
        return buildErrorResponse(ex, ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH, message, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("HTTP message not readable: {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_FORMAT;
        String message = "Malformed JSON request or invalid format.";

        if (ex.getCause() instanceof InvalidFormatException ifx) {
            message = String.format("Invalid format for value '%s' at field '%s'. Expected type: %s.",
                    ifx.getValue(),
                    ifx.getPath().stream().map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining(".")),
                    ifx.getTargetType().getSimpleName());
            log.warn("Invalid format exception details: {}", message);
        } else if (ex.getCause() instanceof DateTimeParseException dtpex) {
            message = String.format("Invalid date/time format: '%s'. Problem: %s", dtpex.getParsedString(), dtpex.getMessage());
            log.warn("DateTimeParseException details: {}", message);
        }
        return buildErrorResponse(ex, errorCode, message, request);
    }


    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("HTTP method not supported: {}", ex.getMessage());
        return buildErrorResponse(ex, ErrorCode.METHOD_NOT_ALLOWED, ex.getMessage(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.warn("Missing request parameter: {}", ex.getMessage());
        String message = String.format("Required parameter '%s' of type %s is missing", ex.getParameterName(), ex.getParameterType());
        return buildErrorResponse(ex, ErrorCode.MISSING_PARAMETER, message, request);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(ex, ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getDefaultDescription(), request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Throwable throwable, ErrorCode errorCode, String message, WebRequest request) {
        return buildErrorResponse(throwable, errorCode, message, null, request);
    }

    private ResponseEntity<Object> buildErrorResponse(
            Throwable throwable, ErrorCode errorCode, String message, List<Error> errors, WebRequest request) {

        HttpStatus status = errorCode.getHttpStatus();
        List<Error> errorList = errors;

        if (errorList == null || errorList.isEmpty()) {
            errorList = Collections.singletonList(
                    Error.builder()
                            .code(errorCode.getCode())
                            // .label(errorCode.name())
                            .message(message != null ? message : errorCode.getDefaultDescription())
                            .build()
            );
        }

        ErrorResponse responseBody = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                errorList,
                getPath(request)
        );

        return new ResponseEntity<>(responseBody, status);
    }

    private void logException(Exception ex) {
        if (ex instanceof ClientBackendException cbEx) {
            HttpStatus status = cbEx.getErrorCode().getHttpStatus();
            if (status.is5xxServerError()) {
                log.error("ClientBackendException [{}]: {}", cbEx.getErrorCode(), cbEx.getMessage(), ex);
            } else {
                log.warn("ClientBackendException [{}]: {}", cbEx.getErrorCode(), cbEx.getMessage());
            }
        } else {
            log.error("Unhandled exception type [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        }
    }

    private String getPath(WebRequest request) {
        try {
            if (request instanceof ServletWebRequest swr) {
                return swr.getRequest().getRequestURI();
            }
        } catch (Exception e) {
            log.trace("Could not extract request path.", e);
        }
        return "N/A";
    }
}
