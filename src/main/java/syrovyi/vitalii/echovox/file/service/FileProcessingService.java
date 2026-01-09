package syrovyi.vitalii.echovox.file.service;

import org.springframework.web.multipart.MultipartFile;
import syrovyi.vitalii.echovox.file.controller.dto.request.CustomerJsonDTO;
import syrovyi.vitalii.echovox.file.controller.dto.response.FileResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface FileProcessingService {
    void uploadFile(MultipartFile file);

    void replaceFile(MultipartFile file);

    void deleteFile(String filename);

    List<FileResponseDTO> getFilesByDate(LocalDate date);

    List<FileResponseDTO> getFilesByCustomer(String customerName);

    List<FileResponseDTO> getFilesByType(String type);

    CustomerJsonDTO getFileContent(String filename);
}
