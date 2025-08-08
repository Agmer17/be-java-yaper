package app.middleware;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import app.model.auth.ApiResponse;
import app.utils.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final JWTUtil jwtUtil;
    private final ObjectMapper mapper;

    public AuthenticationInterceptor(JWTUtil jwtUtil, ObjectMapper mapper) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ResponseEntity<ApiResponse> apiResp = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ApiResponse.builder().status("UNAUTHORIZED").message("Token tidak ada!").data(null).build());
            String jsonApiResp = mapper.writeValueAsString(apiResp);
            response.setStatus(401);
            response.getWriter().write(jsonApiResp);
            return false;
        }

        try {
            String claimsToken = token.substring(7);
            Claims claims = jwtUtil.parseToken(claimsToken);
            request.setAttribute("claims", claims);
            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "token sudah kadaluarsa. silahkan login ulang",
                    e.getCause());
        } catch (JwtException e) {
            throw new JwtException("Token yang kamu punya tidak valid! harap login ulang");
        }

    }
}
