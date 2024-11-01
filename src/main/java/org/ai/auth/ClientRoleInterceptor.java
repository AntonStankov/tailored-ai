package org.ai.auth;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.ai.config.ApplicationConfig;

@ClientRoleAllowed
@Interceptor
public class ClientRoleInterceptor {

    @Inject
    ApplicationConfig applicationConfig;

    @Inject
    SecurityIdentity securityIdentity;

    @AroundInvoke
    public Object checkClientRole(InvocationContext context) throws Exception {
        boolean hasClientRole = securityIdentity.getRoles().stream()
                .anyMatch(role -> role.startsWith("client_") || role.equals(applicationConfig.adminUsername()));

        if (!hasClientRole) {
            throw new ForbiddenException("User does not have a required client role.");
        }

        return context.proceed();
    }
}
