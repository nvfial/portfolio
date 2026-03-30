package com.example.portfolio.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DebugFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String auth = httpRequest.getHeader("Authorization");

        System.out.println("=== DEBUG FILTER ===");
        System.out.println("  " + method + " " + uri);
        System.out.println("  Auth: " + (auth != null ? auth.substring(0, Math.min(20, auth.length())) + "..." : "null"));

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("=== EXCEPTION in Filter ===");
            System.out.println("  Type: " + e.getClass().getName());
            System.out.println("  Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        int status = httpResponse.getStatus();
        System.out.println("  Response Status: " + status);
        System.out.println("=====================");
    }
}