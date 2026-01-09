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
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingServiceImpl implements FileProcessingService {
    private final FileSystemRepository fileSystemRepository;
    private final FileDataMapper fileDataMapper;

    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectMapper objectMapper;

    private static final String FILE_NAME_REGEX = "^([a-zA-Z0-9]+)_([a-zA-Z0-9]+)_(\\d{4}-\\d{2}-\\d{2})\\.xml$";
    private static final Pattern PATTERN = Pattern.compile(FILE_NAME_REGEX);

    @Override
    public void uploadFile(MultipartFile file) {
        processFile(file, false);
    }

    @Override
    public void replaceFile(MultipartFile file) {
        processFile(file, true);
    }

    @Override
    public void deleteFile(String filename) {
        validateFileName(filename);

        String storedFilename = filename.replace(".xml", ".json");

        if (BooleanUtils.isFalse(fileSystemRepository.exists(storedFilename))) {
            throw new ClientBackendException(ErrorCode.NOT_FOUND, "File not found: " + filename);
        }

        fileSystemRepository.delete(storedFilename);
    }

    public List<FileResponseDTO> getFilesByDate(LocalDate date) {
        return searchFiles(filename -> filename.endsWith("_" + date.toString() + ".xml"));
    }

    @Override
    public List<FileResponseDTO> getFilesByCustomer(String customerName) {
        return searchFiles(filename -> {
            Matcher matcher = PATTERN.matcher(filename);

            return matcher.matches() && matcher.group(1).equals(customerName);
        });
    }

    @Override
    public List<FileResponseDTO> getFilesByType(String type) {
        return searchFiles(filename -> {
            Matcher matcher = PATTERN.matcher(filename);

            return matcher.matches() && matcher.group(2).equals(type);
        });
    }

    private List<FileResponseDTO> searchFiles(Predicate<String> filenameFilter) {
        try (Stream<Path> stream = fileSystemRepository.loadAll()) {
            return stream
                    .filter(path -> {
                        String originalXmlName = path.getFileName().toString().replace(".json", ".xml");

                        return filenameFilter.test(originalXmlName);
                    })
                    .map(path -> {
                        String jsonFilename = path.getFileName().toString();
                        String xmlFilename = jsonFilename.replace(".json", ".xml");

                        try {
                            byte[] bytes = fileSystemRepository.readFile(jsonFilename);
                            CustomerJsonDTO content = objectMapper.readValue(bytes, CustomerJsonDTO.class);

                            return fileDataMapper.mapToFileResponseDTO(xmlFilename, content);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Error searching files", e);
        }
    }

    @Override
    public CustomerJsonDTO getFileContent(String filename) {
        validateFileName(filename);

        String storedFilename = filename.replace(".xml", ".json");

        if (BooleanUtils.isFalse(fileSystemRepository.exists(storedFilename))) {
            throw new ClientBackendException(ErrorCode.NOT_FOUND, "File not found: " + filename);
        }

        try {
            byte[] fileContent = fileSystemRepository.readFile(storedFilename);

            return objectMapper.readValue(fileContent, CustomerJsonDTO.class);

        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Error reading file content", e);
        }
    }

    public void processFile(MultipartFile file, boolean allowOverwrite) {
        String originalFilename = file.getOriginalFilename();

        if (Objects.isNull(originalFilename) || originalFilename.isEmpty()) {
            throw new ClientBackendException(ErrorCode.VALIDATION_ERROR, "Filename cannot be null or empty");
        }

        validateFileName(originalFilename);
        String filenameToSave = originalFilename.replace(".xml", ".json");

        if (BooleanUtils.isFalse(allowOverwrite) && fileSystemRepository.exists(filenameToSave)) {
            throw new ClientBackendException(ErrorCode.ALREADY_EXISTS,
                    "File with name " + filenameToSave + " already exists");
        }

        try {
            CustomerXmlDTO xmlDto = xmlMapper.readValue(file.getInputStream(), CustomerXmlDTO.class);
            CustomerJsonDTO jsonDto = fileDataMapper.toJsonDto(xmlDto);
            byte[] jsonBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(jsonDto);

            fileSystemRepository.save(filenameToSave, jsonBytes);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.INVALID_FORMAT, "Error parsing XML or writing file", e);
        }
    }

    private void validateFileName(String filename) {
        if (Objects.isNull(filename)) {
            throw new ClientBackendException(ErrorCode.VALIDATION_ERROR, "Filename cannot be null");
        }

        Matcher matcher = PATTERN.matcher(filename);

        if (BooleanUtils.isFalse(matcher.matches())) {
            throw new ClientBackendException(ErrorCode.VALIDATION_ERROR,
                    "Filename " + filename + " does not match pattern: customer_type_date.xml");
        }
    }
}
