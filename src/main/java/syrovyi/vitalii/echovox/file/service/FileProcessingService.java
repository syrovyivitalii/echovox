package syrovyi.vitalii.echovox.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileProcessingService {
    void uploadFile(MultipartFile file);

    void replaceFile(MultipartFile file);
}
