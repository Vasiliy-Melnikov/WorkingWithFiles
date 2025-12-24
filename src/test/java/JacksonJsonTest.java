import com.fasterxml.jackson.databind.ObjectMapper;
import model.Device;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class JacksonJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void parseJsonWithArray() throws Exception {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("json/device.json")) {

            assertThat(is).isNotNull();

            Device device = mapper.readValue(is, Device.class);

            assertThat(device.model).contains("ROG Ally");
            assertThat(device.ramGb).isEqualTo(16);

            assertThat(device.benchmarks)
                    .isNotNull()
                    .hasSize(2);

            assertThat(device.benchmarks)
                    .anyMatch(b -> b.game.equals("Hogwarts Legacy") && b.fps >= 30);
        }
    }
}