package syrovyi.vitalii.echovox.file.controller.dto.response;

import lombok.Builder;
import lombok.Data;
import syrovyi.vitalii.echovox.file.controller.dto.request.CustomerJsonDTO;

@Data
@Builder
public class FileResponseDTO {
    private String fileName;
    private CustomerJsonDTO content;
}
