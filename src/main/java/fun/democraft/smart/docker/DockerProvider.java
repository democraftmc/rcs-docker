package fun.democraft.smart.docker;

import fun.democraft.smart.ProviderConfig;
import fun.democraft.smart.api.workloads.Api;
import fun.democraft.smart.api.workloads.Provider;
import fun.democraft.smart.api.workloads.Settings;
import group.aelysium.rustyconnector.proxy.family.Family;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class DockerProvider extends Provider {
    public DockerProvider(ProviderConfig config) {
        this(config, System.getenv());
    }

    DockerProvider(ProviderConfig config, Map<String, String> connection) {
        super(config, Api.configured(config.providerId, true, null, connection));
    }

    private static final String CONTAINERS = "/v1.45/containers/";

    @Override
    public String provision(Family family) throws Exception {
        var s = Settings.read(family, id(), Set.of("image", "network"));
        long memory = s.memoryBytes();
        if (memory < 6 * Settings.BYTES_PER_MIB) {
            throw new IllegalArgumentException(s.resourcePath("memory") + " must be at least 7 MB for Docker Engine");
        }
        var host = new HashMap<String, Object>();
        host.put("Memory", memory);
        host.put("NanoCpus", s.cpu(1_000_000_000, 1_000_000_000, 10_000_000, Long.MAX_VALUE));
        if (s.value("network").isPresent()) host.put("NetworkMode", s.string("network"));
        var env = s.env().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toList();
        var body = Map.of("Image", s.string("image"), "Env", env, "HostConfig", host);
        String name = "smart-" + UUID.randomUUID();
        String id = create(CONTAINERS + "create?name=" + name, body, name, "Id");
        return finish(id, () -> api.call("POST", CONTAINERS + id + "/start", null, false));
    }

    @Override
    public void deprovision(String id) throws Exception {
        api.call("DELETE", CONTAINERS + Api.identifier(id) + "?force=true&v=true", null, true);
    }
}
