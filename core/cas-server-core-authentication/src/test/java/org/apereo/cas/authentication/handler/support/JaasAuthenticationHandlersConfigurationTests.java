package org.apereo.cas.authentication.handler.support;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.AuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.DefaultAuthenticationServiceSelectionPlan;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreTicketsSerializationConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.spring.beans.BeanContainer;

import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apereo.services.persondir.IPersonAttributeDao;
import org.apereo.services.persondir.support.StubPersonAttributeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link JaasAuthenticationHandlersConfigurationTests}.
 *
 * @author Misagh Moayyed
 * @since 6.3.0
 */
@Tag("AuthenticationHandler")
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    JaasAuthenticationHandlersConfigurationTests.JaasAuthenticationHandlersConfigurationTestConfiguration.class,
    CasCoreWebConfiguration.class,
    CasCoreHttpConfiguration.class,
    CasCoreUtilConfiguration.class,
    CasCoreTicketCatalogConfiguration.class,
    CasCoreTicketsSerializationConfiguration.class,
    CasCoreTicketsConfiguration.class,
    CasWebApplicationServiceFactoryConfiguration.class,
    CasCoreAuthenticationPrincipalConfiguration.class,
    CasCoreAuthenticationHandlersConfiguration.class,
    CasCoreAuthenticationPolicyConfiguration.class,
    CasCoreAuthenticationSupportConfiguration.class,
    CasCoreAuthenticationConfiguration.class
}, properties = {
    "cas.authn.accept.users=casuser::Mellon,casuser2::Mellon",
    "cas.authn.jaas[0].realm=CAS",
    "cas.authn.jaas[0].password-policy.enabled=true",
    "cas.authn.jaas[0].password-policy.account-state-handling-enabled=true",
    "cas.authn.jaas[0].login-config-type=JavaLoginConfig"
})
class JaasAuthenticationHandlersConfigurationTests {
    @Autowired
    @Qualifier("jaasAuthenticationHandlers")
    private BeanContainer<AuthenticationHandler> jaasAuthenticationHandlers;

    @Autowired
    @Qualifier("jaasPersonDirectoryPrincipalResolvers")
    private BeanContainer<PrincipalResolver> jaasPersonDirectoryPrincipalResolvers;

    @Test
    void verifyOperation() throws Throwable {
        assertFalse(jaasAuthenticationHandlers.isEmpty());
        assertFalse(jaasPersonDirectoryPrincipalResolvers.isEmpty());
    }

    @BeforeEach
    public void initialize() throws Exception {
        val resource = new ClassPathResource("jaas-system.conf");
        val fileName = new File(FileUtils.getTempDirectory(), "jaas-authn.conf");
        try (val writer = Files.newBufferedWriter(fileName.toPath(), StandardCharsets.UTF_8)) {
            IOUtils.copy(resource.getInputStream(), writer, Charset.defaultCharset());
            writer.flush();
        }
        if (fileName.exists()) {
            System.setProperty("java.security.auth.login.config", '=' + fileName.getCanonicalPath());
        }
    }

    @TestConfiguration(value = "JaasAuthenticationHandlersConfigurationTestConfiguration", proxyBeanMethods = false)
    static class JaasAuthenticationHandlersConfigurationTestConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = PrincipalResolver.BEAN_NAME_ATTRIBUTE_REPOSITORY)
        public IPersonAttributeDao attributeRepository() {
            return new StubPersonAttributeDao();
        }

        @Bean
        @ConditionalOnMissingBean(name = AuthenticationServiceSelectionPlan.BEAN_NAME)
        public AuthenticationServiceSelectionPlan authenticationServiceSelectionPlan() {
            return new DefaultAuthenticationServiceSelectionPlan();
        }

        @Bean
        @ConditionalOnMissingBean(name = ServicesManager.BEAN_NAME)
        public ServicesManager servicesManager() {
            return mock(ServicesManager.class);
        }
    }
}
