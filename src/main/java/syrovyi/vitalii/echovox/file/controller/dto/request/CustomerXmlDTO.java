package syrovyi.vitalii.echovox.file.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

@Data
@JsonRootName(value = "data")
public class CustomerXmlDTO {
    private String id;
    private String name;
    private String content;
}
