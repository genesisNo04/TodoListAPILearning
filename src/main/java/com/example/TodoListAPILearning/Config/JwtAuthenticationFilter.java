package com.example.TodoListAPILearning.Config;

import com.example.TodoListAPILearning.Model.AuthUser;
import com.example.TodoListAPILearning.Service.AuthUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
//extend OncePerRequestFilter to make sure this run once per request
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthUserService authUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Read the authorization header
        //ex: authorization: Bearer <jwt-token>
        final String header = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        //Extract the jwt token from the header
        //and use extractUsername from utils class to get the username
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        //Only try to authenticate if the token has username and there is no existing authentication in the SecurityContext
        //SecurityContext is like a thread-local storage for authentication info
        //Spring Security keeps the currently authenticated user in the SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AuthUser user = authUserService.findByUsername(username);

            if (jwtUtil.validateToken(jwt, username)) {

                //Create an authentication object
                //param1: the principle (user)
                //param2 : credentials, not needed because JWT is already a proof
                //authorities: could be populated with role if needed
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, null);

                //Set authentication in SecurityContext
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
