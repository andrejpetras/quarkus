package io.quarkus.spring.cloud.config.client.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

class SpringCloudConfigClientGatewayTest {

    private static final int MOCK_SERVER_PORT = 8089;
    private static final WireMockServer wireMockServer = new WireMockServer(MOCK_SERVER_PORT);

    private final SpringCloudConfigClientGateway sut = new DefaultSpringCloudConfigClientGateway(
            configForTesting());

    @BeforeAll
    static void start() {
        wireMockServer.start();
    }

    @AfterAll
    static void stop() {
        wireMockServer.stop();
    }

    @Test
    void testBasicExchange() throws IOException {
        final String applicationName = "foo";
        final String profile = "dev";
        wireMockServer.stubFor(WireMock.get(String.format("/%s/%s", applicationName, profile)).willReturn(WireMock
                .okJson(getJsonStringForApplicationAndProfile(applicationName, profile))));

        final Response response = sut.exchange(applicationName, profile);

        assertThat(response).isNotNull().satisfies(r -> {
            assertThat(r.getName()).isEqualTo("foo");
            assertThat(r.getProfiles()).containsExactly("dev");
            assertThat(r.getPropertySources()).hasSize(4);
            assertThat(r.getPropertySources().get(0)).satisfies(ps -> {
                assertThat(ps.getSource()).contains(entry("bar", "spam"), entry("foo", "from foo development"),
                        entry("democonfigclient.message", "hello from dev profile"));
            });
            assertThat(r.getPropertySources().get(1)).satisfies(ps -> {
                assertThat(ps.getSource()).contains(entry("my.prop", "from application-dev.yml"));
            });
            assertThat(r.getPropertySources().get(2)).satisfies(ps -> {
                assertThat(ps.getSource()).contains(entry("foo", "from foo props"),
                        entry("democonfigclient.message", "hello spring io"));
            });
            assertThat(r.getPropertySources().get(3)).satisfies(ps -> {
                assertThat(ps.getSource()).contains(entry("foo", "baz"));
            });
        });
    }

    private String getJsonStringForApplicationAndProfile(String applicationName, String profile) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(String.format("/%s-%s.json", applicationName, profile)),
                Charset.defaultCharset());
    }

    private static SpringCloudConfigClientConfig configForTesting() {
        SpringCloudConfigClientConfig springCloudConfigClientConfig = new SpringCloudConfigClientConfig();
        springCloudConfigClientConfig.uri = "http://localhost:" + MOCK_SERVER_PORT;
        springCloudConfigClientConfig.connectionTimeout = Duration.ZERO;
        springCloudConfigClientConfig.readTimeout = Duration.ZERO;
        springCloudConfigClientConfig.username = Optional.empty();
        springCloudConfigClientConfig.password = Optional.empty();
        return springCloudConfigClientConfig;
    }
}
