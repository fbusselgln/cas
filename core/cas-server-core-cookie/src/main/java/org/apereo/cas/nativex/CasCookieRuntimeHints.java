package org.apereo.cas.nativex;

import org.apereo.cas.util.nativex.CasRuntimeHintsRegistrar;
import org.apereo.cas.web.cookie.CasCookieBuilder;
import org.springframework.aot.hint.RuntimeHints;

/**
 * This is {@link CasCookieRuntimeHints}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
public class CasCookieRuntimeHints implements CasRuntimeHintsRegistrar {
    @Override
    public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
        hints.proxies().registerJdkProxy(CasCookieBuilder.class);
    }
}
