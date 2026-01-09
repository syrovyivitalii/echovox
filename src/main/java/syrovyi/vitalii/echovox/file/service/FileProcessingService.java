package syrovyi.vitalii.echovox.file.service;

import org.springframework.web.multipart.MultipartFile;
import syrovyi.vitalii.echovox.file.controller.request.CustomerJsonDTO;
import syrovyi.vitalii.echovox.file.controller.response.FileResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface FileProcessingService {
    void uploadFile(MultipartFile file);

    void replaceFile(MultipartFile file);

    void deleteFile(String filename);

    List<FileResponseDTO> getFilesByDate(LocalDate date);

    CustomerJsonDTO getFileContent(String filename);
}
