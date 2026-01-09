package syrovyi.vitalii.echovox.file.controller.response;

import lombok.Builder;
import lombok.Data;
import syrovyi.vitalii.echovox.file.controller.request.CustomerJsonDTO;

@Data
@Builder
public class FileResponseDTO {
    private String fileName;
    private CustomerJsonDTO content;
}
