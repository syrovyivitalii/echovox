package syrovyi.vitalii.echovox.common.config.jackson;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.dataformat.xml.XmlMapper;

@Configuration
public class JacksonConfig {
    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }
}
