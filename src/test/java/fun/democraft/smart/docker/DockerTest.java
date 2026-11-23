package fun.democraft.smart.docker;

import fun.democraft.smart.api.SmartProvider;
import fun.democraft.smart.testing.ProviderFixture;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DockerTest extends ProviderFixture {
    private SmartProvider provider() {
        return track(new DockerProvider(config("docker-eu"), connection("docker-eu")));
    }

    @Test void createsStartsAndRemovesUsingTheEngineId() throws Exception {
        var family = family(Map.of("cpu", 2, "memory", 768, "docker", Map.of("image", "other"),
                "docker-eu", Map.of("image", "paper:21", "cpu", 1, "network", "servers", "env", Map.of("EULA", "FALSE"))));
        String engineId = "cd".repeat(32);
        reply(201, "{\"Id\":\"" + engineId + "\"}");
        reply(204, "");
        reply(204, "");
        reply(404, "{}");

        String id = provider().provision(family);
        assertEquals(engineId, id);
        var restored = provider();
        restored.deprovision(id);
        restored.deprovision(id);

        String container = "/v1.45/containers/" + id;
        assertCalls(createCall(), "POST " + container + "/start",
                "DELETE " + container + "?force=true&v=true", "DELETE " + container + "?force=true&v=true");
        var body = requests.getFirst().body();
        assertEquals("paper:21", body.get("Image").getAsString());
        assertEquals("EULA=TRUE", body.getAsJsonArray("Env").get(0).getAsString());
        var host = body.getAsJsonObject("HostConfig");
        assertEquals(768_000_000L, host.get("Memory").getAsLong());
        assertEquals(500_000_000L, host.get("NanoCpus").getAsLong());
        assertEquals("servers", host.get("NetworkMode").getAsString());
    }

    @Test void deletesAfterStartFailsWithoutRepeatingCreate() throws Exception {
        reply(201, "{\"Id\":\"a123\"}");
        reply(500, "{\"message\":\"secret-from-engine\"}");
        reply(204, "");
        var family = family(Map.of("docker-eu", Map.of("image", "paper:21")));
        var error = assertThrows(IOException.class, () -> provider().provision(family));
        assertTrue(error.getMessage().contains("500"));
        assertFalse(error.getMessage().contains("secret"));
        assertCalls(createCall(), "POST /v1.45/containers/a123/start",
                "DELETE /v1.45/containers/a123?force=true&v=true");
    }

    @Test void reportsTheContainerNameAndDoesNotRepeatAnUncertainCreate() throws Exception {
        reply(502, "{}");
        var family = family(Map.of("docker-eu", Map.of("image", "paper:21")));
        var error = assertThrows(IOException.class, () -> provider().provision(family));
        String create = createCall();
        assertTrue(error.getMessage().contains(create.split("name=")[1]));
        assertCalls(create);
    }

    private String createCall() {
        String path = requests.getFirst().path();
        assertTrue(path.matches("/v1\\.45/containers/create\\?name=smart-[a-f0-9-]{36}"));
        return "POST " + path;
    }
}
