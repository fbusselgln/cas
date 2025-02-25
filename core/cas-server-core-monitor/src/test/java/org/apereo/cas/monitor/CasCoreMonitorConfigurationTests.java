package org.apereo.cas.monitor;

import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreMonitorConfiguration;
import org.apereo.cas.config.CasCoreNotificationsConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketIdGeneratorsConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreTicketsSerializationConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import lombok.val;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationAutoConfiguration;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Import;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link CasCoreMonitorConfigurationTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@SpringBootTest(
    classes = CasCoreMonitorConfigurationTests.SharedTestConfiguration.class,
    properties = {
        "management.metrics.export.simple.enabled=true",

        "management.endpoint.metrics.enabled=true",
        "management.endpoints.web.exposure.include=*",
        "management.endpoint.health.enabled=true",

        "management.health.systemHealthIndicator.enabled=true",
        "management.health.memoryHealthIndicator.enabled=true",
        "management.health.sessionHealthIndicator.enabled=true"
    })
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Tag("Metrics")
@AutoConfigureObservability
class CasCoreMonitorConfigurationTests {
    @Autowired
    @Qualifier("defaultExecutableObserver")
    private ExecutableObserver defaultExecutableObserver;

    @Autowired
    @Qualifier("memoryHealthIndicator")
    private HealthIndicator memoryHealthIndicator;

    @Autowired
    @Qualifier("sessionHealthIndicator")
    private HealthIndicator sessionHealthIndicator;

    @Autowired
    @Qualifier("systemHealthIndicator")
    private HealthIndicator systemHealthIndicator;

    @Test
    void verifyOperation() throws Throwable {
        assertNotNull(memoryHealthIndicator);
        assertNotNull(sessionHealthIndicator);
        assertNotNull(systemHealthIndicator);
    }

    @Test
    void verifyObserabilitySupplier() throws Throwable {
        val result = defaultExecutableObserver.supply(new MonitorableTask("verifyObserabilitySupplier"), () -> "CAS");
        assertEquals("CAS", result);
    }

    @Test
    void verifyObserabilityRunner() throws Throwable {
        val result = new AtomicBoolean(false);
        defaultExecutableObserver.run(new MonitorableTask("verifyObserabilityRunner"), () -> result.set(true));
        assertTrue(result.get());
    }

    @ImportAutoConfiguration({
        MetricsAutoConfiguration.class,
        ObservationAutoConfiguration.class,
        SimpleMetricsExportAutoConfiguration.class,
        MetricsEndpointAutoConfiguration.class,
        RefreshAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        AopAutoConfiguration.class
    })
    @SpringBootConfiguration
    @Import({
        CasCoreTicketCatalogConfiguration.class,
        CasCoreTicketsSerializationConfiguration.class,
        CasCoreTicketsConfiguration.class,
        CasCoreTicketIdGeneratorsConfiguration.class,
        CasCoreMonitorConfiguration.class,
        CasCoreHttpConfiguration.class,
        CasCoreServicesConfiguration.class,
        CasCoreUtilConfiguration.class,
        CasCoreNotificationsConfiguration.class,
        CasCoreWebConfiguration.class,
        CasWebApplicationServiceFactoryConfiguration.class
    })
    public static class SharedTestConfiguration {
    }
}
