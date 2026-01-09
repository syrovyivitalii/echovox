package syrovyi.vitalii.echovox.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import syrovyi.vitalii.echovox.file.controller.request.CustomerJsonDTO;
import syrovyi.vitalii.echovox.file.service.FileProcessingService;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileProcessingService fileProcessingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file) {
        fileProcessingService.uploadFile(file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> replaceFile(@RequestParam("file") MultipartFile file) {
        fileProcessingService.replaceFile(file);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteFile(@PathVariable String filename) {
        fileProcessingService.deleteFile(filename);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/{filename}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerJsonDTO> getFileContent(@PathVariable String filename) {
        CustomerJsonDTO content = fileProcessingService.getFileContent(filename);

        return ResponseEntity.ok(content);
    }
}