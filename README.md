<div align="center">

<img src="https://raw.githubusercontent.com/democraftmc/rcm-smart/refs/heads/main/blob/images/banner.png" alt="Rusty Smart Banner" />

# 🐳 Rusty Smart – Docker Engine Provider

**Use Docker Engine to scale your Minecraft network with one container per server!**

Requires RustyConnector & Smart

<a href="https://modrinth.com/plugin/rustyconnector"><img src="https://cdn.modrinth.com/data/cached_images/2aa01497a14b7a742a356d7a686118470e0af1bb.png" alt="Get RustyConnector on Modrinth" /></a>
<a href="https://modrinth.com/plugin/rcm-smart"><img src="https://cdn.modrinth.com/data/cached_images/f61969842f4cda7476b6216876acf5fe634c4558.png" alt="Get Rusty Smart on Modrinth" /></a>

[📖 Documentation](https://smart.democraft.fr/docs/providers/official/docker-http/) · [🧠 Smart Module](https://modrinth.com/plugin/rcm-smart) · [💬 Support](https://dsc.gg/drmcld)

</div>

---

## 📌 Overview

In Rusty Smart, a **provider** connects Smart's scaling logic to an infrastructure platform so it can provision and remove Minecraft servers as demand changes.

The **Docker Engine Provider** creates and starts a container for each Minecraft server through the Docker Engine API. Smart removes the container and its anonymous volumes when it scales down.

## ⚙️ Compatibility

| Requirement | Details |
| --- | --- |
| **Docker Engine** | An Engine that supports HTTP API `v1.45` |
| **API access** | Permission to create, start, and remove containers. `curl` is required for Unix sockets or PEM-based TLS. |
| **Server image** | A container image already present on the Docker Engine host |
| **Proxy modules** | RustyConnector, [Rusty Smart 2.0.0](https://modrinth.com/plugin/rcm-smart), and a configured Haze driver |

## 🧱 Installation

1. Place the provider JAR in the `rc-modules` folder of your Minecraft proxy.
2. Set `RC_DOCKER_ENDPOINT` in the proxy environment, for example `unix:///var/run/docker.sock` for a local Engine. See the guide for remote TLS setup.
3. Create `rustyconnector/smart/providers/docker.yml` with `providerId: docker` and include the families you want Smart to scale.
4. Set `image` under `metadata.smart.docker` in each family's YAML. Set `network` there if you use a specific Docker network.
5. Make sure the proxy can reach the containers on TCP 25565, then restart the proxy.

Use a RustyConnector-enabled server image or bootstrap so each new server can join its configured family.

## 🔨 Build from source

Run `gradle clean build` with Java 21 and Gradle. Gradle resolves Smart 2.0.0 from the Funa releases repository and writes the provider JAR to `build/libs/`.

## 💬 Need help?

- 📖 Read the [Docker Engine Provider Documentation](https://smart.democraft.fr/docs/providers/official/docker-http/) for configuration examples and the metadata reference.
- 💬 Join the [Rusty Smart Discord](https://dsc.gg/drmcld) for community support.
