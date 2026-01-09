package syrovyi.vitalii.echovox.file.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import syrovyi.vitalii.echovox.common.exception.enums.ErrorCode;
import syrovyi.vitalii.echovox.common.exception.exception.ClientBackendException;
import syrovyi.vitalii.echovox.file.controller.request.CustomerJsonDTO;
import syrovyi.vitalii.echovox.file.controller.request.CustomerXmlDTO;
import syrovyi.vitalii.echovox.file.mapper.FileDataMapper;
import syrovyi.vitalii.echovox.file.repository.FileSystemRepository;
import syrovyi.vitalii.echovox.file.service.FileProcessingService;
import tools.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public void uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ClientBackendException(ErrorCode.VALIDATION_ERROR, "Filename cannot be null or empty");
        }

        validateFileName(originalFilename);

        String filenameToSave = originalFilename.replace(".xml", ".json");

        if (fileSystemRepository.exists(filenameToSave)) {
            throw new ClientBackendException(ErrorCode.ALREADY_EXISTS,
                    "File with name " + filenameToSave + " already exists");
        }

        try {
            CustomerXmlDTO xmlDto = xmlMapper.readValue(file.getInputStream(), CustomerXmlDTO.class);
            CustomerJsonDTO jsonDto = fileDataMapper.toJsonDto(xmlDto);
            byte[] jsonBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(jsonDto);

            fileSystemRepository.save(filenameToSave, jsonBytes);

        } catch (IOException e) {
            log.error("Error processing file", e);
            throw new ClientBackendException(ErrorCode.INVALID_FORMAT, "Error parsing XML or writing file", e);
        }
    }

    private void validateFileName(String filename) {
        if (Objects.isNull(filename)) {
            throw new ClientBackendException(ErrorCode.VALIDATION_ERROR, "Filename cannot be null");
        }
        Matcher matcher = PATTERN.matcher(filename);
        if (!matcher.matches()) {
            throw new ClientBackendException(ErrorCode.VALIDATION_ERROR,
                    "Filename " + filename + " does not match pattern: customer_type_date.xml");
        }
    }
}
