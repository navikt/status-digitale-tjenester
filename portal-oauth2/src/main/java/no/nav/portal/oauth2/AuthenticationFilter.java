package no.nav.portal.oauth2;


import no.nav.portal.infrastructure.OpenIdConnectAuthentication;
import no.nav.portal.infrastructure.PortalRestPrincipal;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Request;
import org.jsonbuddy.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


import javax.security.auth.Subject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Optional;


public class AuthenticationFilter implements Filter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final Authentication authentication;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("IN AUTHENTICATION FILTER");

        MDC.clear();
        ((Request)request).setAuthentication(authentication);

        String pathInfo = ((Request) request).getPathInfo();
        logger.info("Path: "+ pathInfo);
        getUserv2(request);

        if (pathInfo.startsWith("/login") || pathInfo.startsWith("/callback")) {
            ((HttpServletRequest)request).authenticate((HttpServletResponse)response);
            return;
        }
        //Legg til principal her? for alle some ikke er login eller callback
        // se linje 120 i OpenIdConnectAuthentication
        logger.info("Trying to create user for request");
        JsonObject jsonObject = getJsonBody(request);
        PortalRestPrincipal principal = createPrincipalv2(jsonObject);
        Authentication authenticationForUser = new UserAuthentication("user", createUserIdentity(principal));
    //    ((Request) request).setAuthentication(authenticationForUser);
        chain.doFilter(request, response);
    }

    public PortalRestPrincipal createPrincipalv2(JsonObject payloadJson){
        System.out.println("createPrincipal ---------------------------");
        //payloadJson =
        return new PortalRestPrincipal(payloadJson.requiredString("name"), payloadJson.stringValue("NAVident").orElse(null));
    }

    private DefaultUserIdentity createUserIdentity(Principal principal) {
        System.out.println("createUserIdentity ---------------------------");
        Subject subject = new Subject();
        subject.getPrincipals().add(principal);
        return new DefaultUserIdentity(subject, principal, new String[0]);
    }
    private void getUserv2(ServletRequest servletRequest) {
        logger.info("IN AUTHENTICATION FILTER, /authenticate");
        try {

            String encodedAuthentication = ((HttpServletRequest) servletRequest).getHeader(AUTHORIZATION_HEADER);

            if (encodedAuthentication == null || encodedAuthentication.isEmpty()) {
                return;
            }
            logger.info("Encoded: "+ encodedAuthentication);
            String[] splited = encodedAuthentication.split("[.]");

            String encodedHeader = splited[0];
            String encodedPayload = splited[1];
            //String encodedSignature = splited[2];

            String decodedHeader = new String(Base64.getDecoder().decode(encodedHeader));
            String decodedPayload = new String(Base64.getDecoder().decode(encodedPayload));

            JsonObject headerJson = JsonObject.parse(decodedHeader);
            JsonObject payloadJson = JsonObject.parse(decodedPayload);

            logger.info("Useridentity: " + headerJson + payloadJson);
        }
        catch (Exception e){
            logger.info("Could not read authorization token");
            logger.info(e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> logger.info(stackTraceElement.toString()));
        }

    }

    private JsonObject getJsonBody(ServletRequest request) {
        String encodedAuthentication = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6ImpTMVhvMU9XRGpfNTJ2YndHTmd2UU8yVnpNYyJ9.eyJhdWQiOiI5ZWUwODAxOC1jNjA4LTRjNjAtYWEzYy00MDNmNmZjNmU5MTQiLCJpc3MiOiJodHRwczovL2xvZ2luLm1pY3Jvc29mdG9ubGluZS5jb20vNjIzNjY1MzQtMWVjMy00OTYyLTg4NjktOWI1NTM1Mjc5ZDBiL3YyLjAiLCJpYXQiOjE2NTE0ODAwMjQsIm5iZiI6MTY1MTQ4MDAyNCwiZXhwIjoxNjUxNDg0NTgwLCJhaW8iOiJBVlFBcS84VEFBQUFYdmxVdGVLaTZpeWZXczl2c1ZtbVJXY0NDV2NpdCtld1dyK2dNQkQ2cW5iZnMvRmVJWGhFYytPZDJmSTIvNmhPOWpyNWFhNTF1bjJFYWgrVDNld0R4V0xmS282WnR5UTlvVm8xQ0pZVWJyUT0iLCJhenAiOiI5ZWUwODAxOC1jNjA4LTRjNjAtYWEzYy00MDNmNmZjNmU5MTQiLCJhenBhY3IiOiIyIiwiZ3JvdXBzIjpbIjJkN2YxYzBkLTU3ODQtNGY4MS04YmIyLThmM2E3OWY4Zjk0OSJdLCJuYW1lIjoiTG90c2JlcmcsIExhcnMgQXVndXN0Iiwib2lkIjoiMTAzZjlhZDItMGY2Yi00ODljLWFkNDQtYjNjZWJlMjUxMWRiIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiTGFycy5BdWd1c3QuTG90c2JlcmdAbmF2Lm5vIiwicmgiOiIwLkFTQUFOR1UyWXNNZVlrbUlhWnRWTlNlZEN4aUE0SjRJeG1CTXFqeEFQMl9HNlJRZ0FEay4iLCJzY3AiOiJkZWZhdWx0YWNjZXNzIiwic3ViIjoiRldHc0xGUGxsdWwyZkMzQVNNZGdiZXlPZDhreE9XM0Y5S2lIdGZFa19oUSIsInRpZCI6IjYyMzY2NTM0LTFlYzMtNDk2Mi04ODY5LTliNTUzNTI3OWQwYiIsInV0aSI6ImJReHJJbWJubGt5bG9PS0c5d0lyQUEiLCJ2ZXIiOiIyLjAiLCJOQVZpZGVudCI6IkwxNTI0MjMiLCJhenBfbmFtZSI6ImRldi1nY3A6bmF2ZGlnOnBvcnRhbHNlcnZlciJ9.r9c2p0ukf2xcXPTHkHmsFtG3ScbCRnHxMMYr-STGvmqjbzYNkthBBjmj6bC7iN6syfJ-jK8CzfMOq9wt2Hv7HwfCFu5KcnAIfVJbsaUU9qRz2DYr7OWbjYpbKZ2qg_Ko9IcI3ct931UpOfb5Z2kRQbwJHg6NRGw-Iq7xLGEXe3vKXnc-fTeFVtbRdGuxv8TTfGhHyLP9UVEVPfz_C_LH62iAgQLdDRGA4BU-mOwTRO9Zgndg74ojOIne9SXRQBpxrpNBtTpGOOsefE4GXK1T9zvGo2Rdh5Zh9fuUoQ7h0IV6BwndxiKBegPCVvrqQqIbdzJI7nXcJQJ2PsTXrxaQgQ";
        String[] splited = encodedAuthentication.split("[.]");
        String encodedHeader = splited[0];
        String encodedPayload = splited[1];
        String encodedSignature = splited[2];
        //byte[] decodedBytes = Base64.getDecoder().decode(encodedAuthentication);
        String decodedHeader  = new String(Base64.getDecoder().decode(encodedHeader));
        String decodedPayload  = new String(Base64.getDecoder().decode(encodedPayload));
        JsonObject headerJson =  JsonObject.parse(decodedHeader);
        JsonObject payloadJson = JsonObject.parse(decodedPayload);
        String hallo = "hallo";
        return payloadJson;
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }
}
