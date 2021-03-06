package com.iyzico.challenge.integrator.session;

import com.iyzico.challenge.integrator.exception.auth.AuthorizationException;
import com.iyzico.challenge.integrator.session.model.ApiSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityInterceptor extends HandlerInterceptorAdapter {
    public final static String SESSION = "INTEGRATION_SESSION";
    private final static Logger log = LoggerFactory.getLogger(SecurityInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SecurityConfig security = getSecurityConfigWrapper(handler);

        if (security == null) {
            return true;
        }

        ApiSession session = (ApiSession) request.getSession(false);

        if (session == null) {
            if (!security.isAllowAnonymous()) {
                log.trace("Endpoint is secured and not allows the anonymous users");
                throw new AuthorizationException("Session not found");
            }
        } else {
            if (security.requireAdminPermission()) {
                if (!session.getUser().isAdmin()) {
                    log.trace("Endpoint requires admin privilages and User {} is not admin", session.getUser().getId());
                    throw new AuthorizationException("Not authorized");
                }
            }

            request.setAttribute(SecurityInterceptor.SESSION, session);
        }

        return true;
    }

    private SecurityConfig getSecurityConfigWrapper(Object handler) {
        if (isHandlerMethod(handler)) {
            try {
                return getSecurityConfig((HandlerMethod) handler);
            } catch (Exception e) {
                // ignored
            }
        }

        return null;
    }

    private boolean isHandlerMethod(Object handler) {
        return handler instanceof HandlerMethod;
    }

    private SecurityConfig getSecurityConfig(HandlerMethod handler) {
        return new SecurityConfig(handler);
    }

    protected static class SecurityConfig {
        private final SecuredEndpoint secured;

        SecurityConfig(HandlerMethod method) {
            SecuredEndpoint secured = method.getMethodAnnotation(SecuredEndpoint.class);
            if (secured == null) {
                secured = method.getBeanType().getAnnotation(SecuredEndpoint.class);
                if (secured == null) {
                    throw new NullPointerException();
                }
            }

            this.secured = secured;

        }

        private boolean isAllowAnonymous() {
            return secured.isAllowAnonymous();
        }

        private boolean requireAdminPermission() {
            return secured.requireAdminPermission();
        }
    }
}
