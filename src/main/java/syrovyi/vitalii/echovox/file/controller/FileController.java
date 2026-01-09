package syrovyi.vitalii.echovox.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import syrovyi.vitalii.echovox.file.controller.request.CustomerJsonDTO;
import syrovyi.vitalii.echovox.file.controller.response.FileResponseDTO;
import syrovyi.vitalii.echovox.file.service.FileProcessingService;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping(value = "/by/date", params = "date")
    public ResponseEntity<List<FileResponseDTO>> getFilesByDate(@RequestParam("date") LocalDate date) {
        List<FileResponseDTO> files = fileProcessingService.getFilesByDate(date);

        return ResponseEntity.ok(files);
    }

    @GetMapping(value = "/by/customer", params = "customer")
    public ResponseEntity<List<FileResponseDTO>> getFilesByCustomer(@RequestParam("customer") String customer) {
        List<FileResponseDTO> files = fileProcessingService.getFilesByCustomer(customer);
        return ResponseEntity.ok(files);
    }

    @GetMapping(value = "/by/type", params = "type")
    public ResponseEntity<List<FileResponseDTO>> getFilesByType(@RequestParam("type") String type) {
        List<FileResponseDTO> files = fileProcessingService.getFilesByType(type);
        return ResponseEntity.ok(files);
    }
}