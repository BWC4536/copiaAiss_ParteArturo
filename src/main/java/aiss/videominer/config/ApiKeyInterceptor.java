package aiss.videominer.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${videominer.api.key}")
    private String validApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Vamos a proteger solamente las peticiones que modifican datos (POST, PUT, DELETE)
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            return true; // Dejamos pasar las peticiones GET sin pedir clave
        }

        String authHeader = request.getHeader("Authorization");

        // Comprobamos que el header exista y empiece por Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (token.equals(validApiKey)) {
                return true; // El token es correcto
            }
        }

        // Si no hay token o es incorrecto, devolvemos error 401
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Unauthorized: Falta la API Key o es incorrecta. Usa el header 'Authorization: Bearer <API-KEY>'");
        return false;
    }
}
