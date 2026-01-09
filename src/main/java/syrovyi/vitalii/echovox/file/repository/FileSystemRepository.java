package syrovyi.vitalii.echovox.file.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import syrovyi.vitalii.echovox.common.exception.enums.ErrorCode;
import syrovyi.vitalii.echovox.common.exception.exception.ClientBackendException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class FileSystemRepository {
    @Value("${file.upload-dir}")
    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Could not initialize storage", e);
        }
    }

    public boolean exists(String filename) {
        return Files.exists(this.rootLocation.resolve(filename));
    }

    public void save(String filename, byte[] content) {
        try {
            Path destinationFile = this.rootLocation.resolve(filename);
            Files.write(destinationFile, content);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Failed to store file " + filename, e);
        }
    }

    public void delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Could not delete file: " + filename, e);
        }
    }

    public byte[] readFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Could not read file: " + filename, e);
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.list(this.rootLocation)
                    .filter(path -> BooleanUtils.isFalse(Files.isDirectory(path)));
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.IO_ERROR, "Failed to load files", e);
        }
    }
}
