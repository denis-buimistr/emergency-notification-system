    package com.example.ens.security;

    import io.github.cdimascio.dotenv.Dotenv;
    import io.jsonwebtoken.*;
    import io.jsonwebtoken.security.Keys;
    import org.springframework.stereotype.Service;

    import java.security.Key;
    import java.util.Date;
    import java.util.Map;
    import java.util.function.Function;

    @Service
    public class JwtService {

        private static final Dotenv dotenv = Dotenv.load();
        private static final String SECRET_KEY = dotenv.get("JWT_SECRET");

        private Key getSigningKey() {
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        }

        public String generateToken(String email, Map<String, Object> extraClaims) {
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(email)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        public String extractEmail(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public boolean isTokenValid(String token, String email) {
            return (extractEmail(token).equals(email)) && !isTokenExpired(token);
        }

        private boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        private Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        }
    }
