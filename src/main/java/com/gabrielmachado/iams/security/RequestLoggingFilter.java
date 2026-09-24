package com.gabrielmachado.iams.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RequestLoggingFilter(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    record RequestEvent(String metodo, String rota, int status, String usuario, String ip, String timestamp) {}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(request, response);

        String usuario = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonimo";

        logger.info("{} {} -> {} | usuario={} | ip={}",
                request.getMethod(), request.getRequestURI(), response.getStatus(), usuario, request.getRemoteAddr());

        try {
            RequestEvent event = new RequestEvent(
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    usuario,
                    request.getRemoteAddr(),
                    Instant.now().toString()
            );
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("requisicoes", json);
        } catch (Exception e) {
            logger.error("Falha ao publicar evento no Kafka: {}", e.getMessage());
        }
    }
}