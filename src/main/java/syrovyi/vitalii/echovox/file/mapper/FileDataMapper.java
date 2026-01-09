package syrovyi.vitalii.echovox.file.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import syrovyi.vitalii.echovox.file.controller.request.CustomerJsonDTO;
import syrovyi.vitalii.echovox.file.controller.request.CustomerXmlDTO;
import syrovyi.vitalii.echovox.file.controller.response.FileResponseDTO;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface FileDataMapper {

    @Mapping(source = "id", target = "customerId")
    @Mapping(source = "name", target = "customerName")
    @Mapping(source = "content", target = "payload")
    CustomerJsonDTO toJsonDto(CustomerXmlDTO xmlDto);

    default FileResponseDTO mapToFileResponseDTO(String fileName, CustomerJsonDTO content) {
        if (Objects.isNull(content) && Objects.isNull(fileName)) {
            return null;
        }

        return FileResponseDTO.builder()
                .fileName(fileName)
                .content(content)
                .build();
    }
}
