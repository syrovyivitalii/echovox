package syrovyi.vitalii.echovox.file.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import syrovyi.vitalii.echovox.common.exception.enums.ErrorCode;
import syrovyi.vitalii.echovox.common.exception.exception.ClientBackendException;
import syrovyi.vitalii.echovox.file.controller.dto.request.CustomerJsonDTO;
import syrovyi.vitalii.echovox.file.controller.dto.request.CustomerXmlDTO;
import syrovyi.vitalii.echovox.file.controller.dto.response.FileResponseDTO;
import syrovyi.vitalii.echovox.file.mapper.FileDataMapper;
import syrovyi.vitalii.echovox.file.repository.FileSystemRepository;
import syrovyi.vitalii.echovox.file.service.FileProcessingService;
import syrovyi.vitalii.echovox.file.service.FilenameHandlerService;
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingServiceImpl implements FileProcessingService {

    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;
    private final FileDataMapper fileDataMapper;
    private final FileSystemRepository fileSystemRepository;
    private final FilenameHandlerService filenameHandler;

    @Override
    public void uploadFile(MultipartFile file) {
        save(file, false);
    }

    @Override
    public void replaceFile(MultipartFile file) {
        save(file, true);
    }

    @Override
    public void deleteFile(String filename) {
        filenameHandler.validate(filename);
        String storedName = filenameHandler.toStoredFilename(filename);

        if (BooleanUtils.isFalse(fileSystemRepository.exists(storedName))) {
            throw new ClientBackendException(ErrorCode.NOT_FOUND, "File not found: " + filename);
        }

        fileSystemRepository.delete(storedName);
    }

    @Override
    public CustomerJsonDTO getFileContent(String filename) {
        filenameHandler.validate(filename);
        String storedName = filenameHandler.toStoredFilename(filename);

        if (BooleanUtils.isFalse(fileSystemRepository.exists(storedName))) {
            throw new ClientBackendException(ErrorCode.NOT_FOUND, "File not found: " + filename);
        }

        try {
            byte[] bytes = fileSystemRepository.readFile(storedName);

            return objectMapper.readValue(bytes, CustomerJsonDTO.class);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Failed to read file content", e);
        }
    }

    @Override
    public List<FileResponseDTO> getFilesByDate(LocalDate date) {
        String glob = filenameHandler.generateDateGlob(date);

        return searchFiles(glob, name -> filenameHandler.matchesDate(name, date));
    }

    @Override
    public List<FileResponseDTO> getFilesByCustomer(String customerName) {
        String glob = filenameHandler.generateCustomerGlob(customerName);

        return searchFiles(glob, name -> filenameHandler.matchesCustomer(name, customerName));
    }

    @Override
    public List<FileResponseDTO> getFilesByType(String type) {
        String glob = filenameHandler.generateTypeGlob(type);

        return searchFiles(glob, name -> filenameHandler.matchesType(name, type));
    }

    private void save(MultipartFile file, boolean allowOverwrite) {
        String originalFilename = file.getOriginalFilename();

        if (Objects.isNull(originalFilename) || originalFilename.isEmpty()) {
            throw new ClientBackendException(ErrorCode.VALIDATION_ERROR, "Filename cannot be empty");
        }
        filenameHandler.validate(originalFilename);

        String storedName = filenameHandler.toStoredFilename(originalFilename);

        if (BooleanUtils.isFalse(allowOverwrite) && fileSystemRepository.exists(storedName)) {
            throw new ClientBackendException(ErrorCode.ALREADY_EXISTS,
                    "File " + storedName + " already exists");
        }

        try {
            CustomerXmlDTO xmlDto = xmlMapper.readValue(file.getInputStream(), CustomerXmlDTO.class);
            CustomerJsonDTO jsonDto = fileDataMapper.toJsonDto(xmlDto);

            byte[] jsonBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(jsonDto);
            fileSystemRepository.save(storedName, jsonBytes);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.INVALID_FORMAT, "Error parsing XML or writing file", e);
        }
    }

    private List<FileResponseDTO> searchFiles(String globPattern, Predicate<String> strictFilter) {
        try (Stream<Path> stream = fileSystemRepository.findFiles(globPattern)) {
            return stream
                    .map(path -> path.getFileName().toString())
                    .map(filenameHandler::toOriginalFilename)
                    .filter(strictFilter)
                    .map(this::mapFileResponseDTO)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (Exception e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Error searching files", e);
        }
    }

    private Optional<FileResponseDTO> mapFileResponseDTO(String xmlFilename) {
        try {
            String jsonFilename = filenameHandler.toStoredFilename(xmlFilename);
            byte[] bytes = fileSystemRepository.readFile(jsonFilename);
            CustomerJsonDTO content = objectMapper.readValue(bytes, CustomerJsonDTO.class);

            return Optional.of(fileDataMapper.mapToFileResponseDTO(xmlFilename, content));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}