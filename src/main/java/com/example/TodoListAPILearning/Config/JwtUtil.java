package com.example.TodoListAPILearning.Config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "";
    private static final long EXPIRATION_TIME = 1000 * 60;

    //SECRET_KEY.getBytes() change the key into array of bytes
    //Keys.hmacShaKeyFor this wrap the byte arrayy as a Secret key that can be used with HMAC-SHA(HS256)
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    //Jwts.builder() create new JWTBuilder objects
    //This is a fluent API where I can add claims, header values, and set how token is sign
    //builder is used a lot in Java
    /*
    * Builder pattern
    * creational desgin pattern used to construct complex objects step by steps.
    * Instead of calling a big constructor with a lot of parameter, we "build" object by chaining method calls.
    * At the end call .build() or .compact (this in in JJWT) to get final object.
    *
    */

    //.setSubject(username): subject claim to the payload
    // Standard JWT claim, usually used for the user identifier
    // A claim: a piece of information(a key-value pair) inside the JWT payload
    // claims are statements about a subject
    /*
    * Registered claims: predefined standard keys defined by the JWT spec
    * Public claims: custom keys you define, shared across systems.
    * Private claims: custom keys agreed upon between parties.
    * Sub claim: subject claim: identified who the token is about
    * Best practice is to have sub or userId claim
    * Other claims: iss(issuer: who create the token), aud(audience, who the token is intended for)
    * exp(expiration when the token should expire), ist issueAt(when the token is created), nbf (not before: when the token becomes valid)
    */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) //standard claim
                .setIssuedAt(new Date(System.currentTimeMillis())) //standard claim
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //standard claim
//                .claim("role", role) //custom claim
//                .claim("email", email) //custom claim
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //If I have multiple dynamic claims, it’s easier to use a Map
//    Map<String, Object> claims = new HashMap<>();
//    claims.put("role", "ADMIN");
//    claims.put("email", "alice@example.com");
//    claims.put("userId", 42);
//    public String generateToken(String username, Map<String, Object> extraClaims) {
//        return Jwts.builder()
//                .setClaims(extraClaims) // put all custom claims here
//                .setSubject(username)   // standard claim
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }

    //Jwts.parserBuilder() create a parserBuilder object
    //Used for parsing/validating JWT
    //.setSigningKey() this tell the parser which secret key to use to verify the JWT signature
    //.build() build the JWTParser instance using the configuration
    //parseClaimsJws(token): parse the token, JWS = JSON Web signature (signed JWT)
    /*
    * step inside:
    * 1. Split the token into header.payload.signature
    * 2. Base64URL-decode the header and payload
    * 3. Recompute the signature using secret key and algorithm
    * 4. Compare if it with the signature in the token. Token is authenticated if match
    * Returns a JWS<Claims> object containing the payload
     */
    //.getBody() return value of all the claim
    //.getSubject(): get the sub claim
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //check if the token is valid
    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    // this does the same thing with extractUsername but this get the expired time
    //.before() this check if the expiration date is in the past
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
