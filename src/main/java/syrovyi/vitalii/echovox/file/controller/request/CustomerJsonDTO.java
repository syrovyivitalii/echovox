package syrovyi.vitalii.echovox.file.controller.request;

import lombok.Data;

@Data
public class CustomerJsonDTO {
    private String customerId;
    private String customerName;
    private String payload;
}
