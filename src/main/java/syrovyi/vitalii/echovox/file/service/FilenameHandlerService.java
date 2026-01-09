package syrovyi.vitalii.echovox.file.service;

import java.time.LocalDate;

public interface FilenameHandlerService {
    void validate(String filename);

    String toStoredFilename(String originalXmlName);

    String toOriginalFilename(String storedJsonName);

    boolean matchesCustomer(String filename, String customer);

    boolean matchesType(String filename, String type);

    boolean matchesDate(String filename, LocalDate date);

    String generateDateGlob(LocalDate date);

    String generateCustomerGlob(String customerName);

    String generateTypeGlob(String type);
}
