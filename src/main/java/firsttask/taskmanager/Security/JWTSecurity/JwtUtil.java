package firsttask.taskmanager.Security.JWTSecurity;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class JwtUtil {

    private final String SECRET_KEY = "secret";// this key is used to encrypt the jwt it should be stored in a save file and encrypted and decrypted when needed
    private static List<String> blackListTokens= new ArrayList<>();
    private static List<String> loggedInUserTokens= new ArrayList<>();

    public void addTokenToBlackList(String Token){
        blackListTokens.add(Token);

    }
    public void addTokenToLoggedInUserTokens(String Token){
        loggedInUserTokens.add(Token);
    }
    public void moveAllTokensToBlackList(){
        blackListTokens.addAll(loggedInUserTokens);
    }

   public boolean isTokenInBlackList(String Token){
        return blackListTokens.contains(Token);
   }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();// the claim as set to null for the this....we'll set them later
        return createToken(claims, userDetails.getUsername());
    }


    // creating the token using the username of the user
    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !isTokenInBlackList(token));
    }
}