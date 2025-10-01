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

        //Only try to authenticate if the token has username and there is no existing authentication in the SecurityContext (this is to prevent multiple filter running at the same time
        // and it may overwrite the authentication)
        //SecurityContext is like a thread-local storage for authentication info, this will save the authentication during the duration of the request getting executed
        //Spring Security keeps the currently authenticated user in the SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AuthUser user = authUserService.findByUsername(username);

            //This condition make sure the token belong the current user and check if the token is still valid
            if (jwtUtil.validateToken(jwt, username)) {

                //Create an authentication object
                //param1: the principle (user)
                //param2 : credentials, not needed because JWT is already a proof
                //authorities: could be populated with role if needed

                /*
                Authentication object will look like this
                {
                    "principal": user,            // The identity (your AuthUser object)
                    "credentials": null,          // Password or proof (not needed for JWT)
                    "authorities": null           // Roles/permissions (you could pass in here)
                    "authenticated": true         // By default, when created like this
                }
                */
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, null);

                //This attach more info for the authentication object
                //WebAuthenticationDetailsSource look at the current address and create the new object details with remote IP address and session ID (if any) and attach it to the authentication object for more detail
                //Useful for auditing/logging
                //Might use IP address to restrict access
                //Consistency: many part of Spring security expect details to be set this way, so this is kind of boilerplate.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //This let spring know this filter is done, spring can proceed to the next filter
        //If there is no filter left it will go to the controller endpoint
        filterChain.doFilter(request, response);
    }
}
