package syrovyi.vitalii.echovox.file.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import syrovyi.vitalii.echovox.common.exception.enums.ErrorCode;
import syrovyi.vitalii.echovox.common.exception.exception.ClientBackendException;
import syrovyi.vitalii.echovox.file.service.FilenameHandlerService;

import java.time.LocalDate;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FilenameHandlerServiceImpl implements FilenameHandlerService {
    private static final String EXT_XML = ".xml";
    private static final String EXT_JSON = ".json";

    private static final String G_CUSTOMER = "customer";
    private static final String G_TYPE = "type";
    private static final String G_DATE = "date";

    private static final Pattern FILENAME_PATTERN = Pattern.compile(
            String.format("^(?<%s>[a-zA-Z0-9]+)_(?<%s>[a-zA-Z0-9]+)_(?<%s>\\d{4}-\\d{2}-\\d{2})\\.xml$",
                    G_CUSTOMER, G_TYPE, G_DATE)
    );

    @Override
    public void validate(String filename) {
        if (Objects.isNull(filename)|| !FILENAME_PATTERN.matcher(filename).matches()) {
            throw new ClientBackendException(ErrorCode.VALIDATION_ERROR,
                    "Invalid filename format. Expected: customer_type_date.xml. Got: " + filename);
        }
    }

    @Override
    public String toStoredFilename(String originalXmlName) {
        return originalXmlName.replace(EXT_XML, EXT_JSON);
    }

    @Override
    public String toOriginalFilename(String storedJsonName) {
        return storedJsonName.replace(EXT_JSON, EXT_XML);
    }

    @Override
    public boolean matchesCustomer(String filename, String customer) {
        Matcher m = FILENAME_PATTERN.matcher(filename);

        return m.matches() && m.group(G_CUSTOMER).equals(customer);
    }

    @Override
    public boolean matchesType(String filename, String type) {
        Matcher m = FILENAME_PATTERN.matcher(filename);

        return m.matches() && m.group(G_TYPE).equals(type);
    }

    @Override
    public boolean matchesDate(String filename, LocalDate date) {
        Matcher m = FILENAME_PATTERN.matcher(filename);

        return m.matches() && m.group(G_DATE).equals(date.toString());
    }
}
