package syrovyi.vitalii.echovox.common.exception.exception;

import lombok.Getter;
import org.springframework.util.Assert;
import syrovyi.vitalii.echovox.common.exception.enums.ErrorCode;

@Getter
public class ClientBackendException extends RuntimeException {
  private final ErrorCode errorCode;
  private final String overrideMessage;

  public ClientBackendException(ErrorCode errorCode) {
    super(errorCode.getDefaultDescription());
    Assert.notNull(errorCode, "ErrorCode is required");
    this.errorCode = errorCode;
    this.overrideMessage = null;
  }

  public ClientBackendException(ErrorCode errorCode, String overrideMessage) {
    super(overrideMessage != null ? overrideMessage : errorCode.getDefaultDescription());
    Assert.notNull(errorCode, "ErrorCode is required");
    this.errorCode = errorCode;
    this.overrideMessage = overrideMessage;
  }

  public ClientBackendException(ErrorCode errorCode, String overrideMessage, Throwable cause) {
    super(overrideMessage != null ? overrideMessage : errorCode.getDefaultDescription(), cause);
    Assert.notNull(errorCode, "ErrorCode is required");
    this.errorCode = errorCode;
    this.overrideMessage = overrideMessage;
  }
}
