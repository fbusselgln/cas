package org.apereo.cas.nativex;

import org.apereo.cas.audit.AuditTrailExecutionPlan;
import org.apereo.cas.audit.AuditTrailExecutionPlanConfigurer;
import org.apereo.cas.audit.AuditTrailRecordResolutionPlanConfigurer;
import org.apereo.cas.util.nativex.CasRuntimeHintsRegistrar;
import org.apereo.inspektr.audit.AuditTrailManagementAspect;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.apereo.inspektr.audit.spi.AuditActionResolver;
import org.apereo.inspektr.audit.spi.AuditResourceResolver;
import org.apereo.inspektr.common.spi.PrincipalResolver;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import java.util.Collection;
import java.util.List;

/**
 * This is {@link CasCoreAuditRuntimeHints}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
public class CasCoreAuditRuntimeHints implements CasRuntimeHintsRegistrar {
    @Override
    public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
        hints.proxies().registerJdkProxy(AuditTrailExecutionPlanConfigurer.class);

        registerReflectionHints(hints, List.of(
            AuditTrailExecutionPlan.class,
            AuditTrailExecutionPlanConfigurer.class,
            AuditTrailRecordResolutionPlanConfigurer.class,
            AuditTrailManagementAspect.class
        ));

        registerProxyHints(hints, List.of(
            AuditTrailManager.class,
            PrincipalResolver.class,
            AuditActionResolver.class,
            AuditResourceResolver.class,
            AuditTrailExecutionPlanConfigurer.class,
            AuditTrailRecordResolutionPlanConfigurer.class,
            AuditEventRepository.class));
    }

    private static void registerProxyHints(final RuntimeHints hints, final Collection<Class> subclassesInPackage) {
        subclassesInPackage.forEach(clazz -> hints.proxies().registerJdkProxy(clazz));
    }

    private static void registerReflectionHints(final RuntimeHints hints, final Collection entries) {
        entries.forEach(el -> hints.reflection().registerType((Class) el,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
            MemberCategory.INVOKE_DECLARED_METHODS,
            MemberCategory.INVOKE_PUBLIC_METHODS,
            MemberCategory.DECLARED_FIELDS,
            MemberCategory.PUBLIC_FIELDS));
    }
}
