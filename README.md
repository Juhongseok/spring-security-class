## AnonymousAuthenticationFilter
```java
class AnonymousAuthenticationFilter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        Supplier<SecurityContext> deferredContext = this.securityContextHolderStrategy.getDeferredContext();
        this.securityContextHolderStrategy
                .setDeferredContext(defaultWithAnonymous((HttpServletRequest) req, deferredContext));
        chain.doFilter(req, res);
    }

    //익명 사용자 생성 후 SecurityContext 등록
    private Supplier<SecurityContext> defaultWithAnonymous(HttpServletRequest request,
                                                           Supplier<SecurityContext> currentDeferredContext) {
        return SingletonSupplier.of(() -> {
            SecurityContext currentContext = currentDeferredContext.get();
            return defaultWithAnonymous(request, currentContext);
        });
    }

    //익명 사용자 생성 후 SecurityContext 등록
    private SecurityContext defaultWithAnonymous(HttpServletRequest request, SecurityContext currentContext) {
        Authentication currentAuthentication = currentContext.getAuthentication();
        //익명 사용자일 경우
        if (currentAuthentication == null) {
            Authentication anonymous = createAuthentication(request);
            SecurityContext anonymousContext = this.securityContextHolderStrategy.createEmptyContext();
            anonymousContext.setAuthentication(anonymous);
            return anonymousContext;
        } else {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(LogMessage.of(() -> "Did not set SecurityContextHolder since already authenticated "
                        + currentAuthentication));
            }
        }
        //로그인 했을 경우
        return currentContext;
    }

    //익명 사용자 생성
    protected Authentication createAuthentication(HttpServletRequest request) {
        AnonymousAuthenticationToken token = new AnonymousAuthenticationToken(this.key, this.principal,
                this.authorities);
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));
        return token;
    }
}

```

## ExceptionTranslationFilter
```java
class ExceptionTranslationFilter {
    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(ex);
            RuntimeException securityException = (AuthenticationException) this.throwableAnalyzer
                    .getFirstThrowableOfType(AuthenticationException.class, causeChain);
            if (securityException == null) {
                securityException = (AccessDeniedException) this.throwableAnalyzer
                        .getFirstThrowableOfType(AccessDeniedException.class, causeChain);
            }
            if (securityException == null) {
                throw new RuntimeException(ex);
            }
            if (response.isCommitted()) {
                throw new ServletException("Unable to handle the Spring Security Exception "
                        + "because the response is already committed.", ex);
            }
            handleSpringSecurityException(request, response, chain, securityException);
        }
    }

    private void handleSpringSecurityException(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain chain, RuntimeException exception) throws IOException, ServletException {
        if (exception instanceof AuthenticationException) {
            handleAuthenticationException(request, response, chain, (AuthenticationException) exception);
        }
        else if (exception instanceof AccessDeniedException) {
            handleAccessDeniedException(request, response, chain, (AccessDeniedException) exception);
        }
    }

    private void handleAuthenticationException(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain chain, AuthenticationException exception) throws ServletException, IOException {
        this.logger.trace("Sending to authentication entry point since authentication failed", exception);
        sendStartAuthentication(request, response, chain, exception);
    }

    private void handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response,
                                             FilterChain chain, AccessDeniedException exception) throws ServletException, IOException {
        Authentication authentication = this.securityContextHolderStrategy.getContext().getAuthentication();
        boolean isAnonymous = this.authenticationTrustResolver.isAnonymous(authentication);
        if (isAnonymous || this.authenticationTrustResolver.isRememberMe(authentication)) {
            if (logger.isTraceEnabled()) {
                logger.trace(LogMessage.format("Sending %s to authentication entry point since access is denied",
                        authentication), exception);
            }
            sendStartAuthentication(request, response, chain,
                    new InsufficientAuthenticationException(
                            this.messages.getMessage("ExceptionTranslationFilter.insufficientAuthentication",
                                    "Full authentication is required to access this resource")));
        }
        else {
            if (logger.isTraceEnabled()) {
                logger.trace(
                        LogMessage.format("Sending %s to access denied handler since access is denied", authentication),
                        exception);
            }
            this.accessDeniedHandler.handle(request, response, exception);
        }
    }
}
```

## 대칭 키 암호화

## RSA 암호화
# spring-security-class
