package syrovyi.vitalii.echovox.file.service;

import org.springframework.web.multipart.MultipartFile;
import syrovyi.vitalii.echovox.file.controller.request.CustomerJsonDTO;

public interface FileProcessingService {
    void uploadFile(MultipartFile file);

    void replaceFile(MultipartFile file);

    void deleteFile(String filename);

    CustomerJsonDTO getFileContent(String filename);
}
