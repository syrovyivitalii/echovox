package syrovyi.vitalii.echovox.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import syrovyi.vitalii.echovox.file.controller.dto.request.CustomerJsonDTO;
import syrovyi.vitalii.echovox.file.controller.dto.response.FileResponseDTO;
import syrovyi.vitalii.echovox.file.service.FileProcessingService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File Management", description = "Operations for uploading, searching, and managing XML/JSON files")
public class FileController {
    private final FileProcessingService fileProcessingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload XML file", description = "Validates filename, converts XML content to JSON, and saves to storage.")
    public ResponseEntity<Void> uploadFile(@RequestParam("file") MultipartFile file) {
        fileProcessingService.uploadFile(file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Replace existing file", description = "Overwrites an existing file if the name matches.")
    public ResponseEntity<Void> replaceFile(@RequestParam("file") MultipartFile file) {
        fileProcessingService.replaceFile(file);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{filename}")
    @Operation(summary = "Delete file", description = "Permanently removes the file from storage by filename.")
    public ResponseEntity<Void> deleteFile(@PathVariable String filename) {
        fileProcessingService.deleteFile(filename);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/{filename}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get file content", description = "Retrieves the parsed JSON content of a specific file.")
    public ResponseEntity<CustomerJsonDTO> getFileContent(@PathVariable String filename) {
        CustomerJsonDTO content = fileProcessingService.getFileContent(filename);

        return ResponseEntity.ok(content);
    }

    @GetMapping(value = "/by/date", params = "date")
    @Operation(summary = "Search files by date", description = "Returns a list of files matching the specific date extracted from the filename.")
    public ResponseEntity<List<FileResponseDTO>> getFilesByDate(@RequestParam("date") LocalDate date) {
        List<FileResponseDTO> files = fileProcessingService.getFilesByDate(date);

        return ResponseEntity.ok(files);
    }

    @GetMapping(value = "/by/customer", params = "customer")
    @Operation(summary = "Search files by customer", description = "Returns a list of files matching the customer name extracted from the filename.")
    public ResponseEntity<List<FileResponseDTO>> getFilesByCustomer(@RequestParam("customer") String customer) {
        List<FileResponseDTO> files = fileProcessingService.getFilesByCustomer(customer);
        return ResponseEntity.ok(files);
    }

    @GetMapping(value = "/by/type", params = "type")
    @Operation(summary = "Search files by type", description = "Returns a list of files matching the document type extracted from the filename.")
    public ResponseEntity<List<FileResponseDTO>> getFilesByType(@RequestParam("type") String type) {
        List<FileResponseDTO> files = fileProcessingService.getFilesByType(type);
        return ResponseEntity.ok(files);
    }
}