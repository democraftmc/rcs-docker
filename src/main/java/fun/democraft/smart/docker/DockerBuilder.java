package fun.democraft.smart.docker;

import group.aelysium.rustyconnector.common.modules.ExternalModuleBuilder;
import group.aelysium.rustyconnector.common.modules.Module;
import fun.democraft.smart.api.workloads.ProviderBuilder;
import group.aelysium.rustyconnector.proxy.ProxyKernel;

public final class DockerBuilder extends ExternalModuleBuilder<Module> {
    @Override
    public Module onStart(Context context) {
        return ProviderBuilder.start("docker", DockerProvider::new);
    }

    @Override
    public void bind(ProxyKernel kernel, Module module) {
        ProviderBuilder.bind(kernel, module);
    }
}
