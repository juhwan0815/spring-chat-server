package spring.study.websocket.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private long tokenValidMilliSecond = 1000L * 60 * 60; // 1시간만 토큰 유효

    /**
     * 이름으로 Jwt Token을 생성
     */
    public String generateToken(String name){
        Date now = new Date();
        return Jwts.builder()
                .setId(name)
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + tokenValidMilliSecond))
                .signWith(SignatureAlgorithm.HS256,secretKey) // 암호화 알고리즘
                .compact();
    }

    /**
     * Jwt Token을 복호화 하여 이름을 얻는다.
     */
    public String getUsernameFromJwt(String jwt){
        return getClaims(jwt).getBody().getId();
    }

    /**
     * Jwt Token의 유효성을 체크한다.
     */
    public boolean validateToken(String jwt){
        return this.getClaims(jwt) != null;
    }

    private Jws<Claims> getClaims(String jwt){
        try{
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        }catch (SignatureException e){
            log.error("Invalid JWT signature");
            throw e;
        }catch (MalformedJwtException e){
            log.error("Invalid JWT token");
            throw e;
        }catch (ExpiredJwtException e){
            log.error("Expired JWT token");
            throw e;
        }catch (UnsupportedJwtException e){
            log.error("Unsupported JWT token");
            throw e;
        }catch (IllegalArgumentException e){
            log.error("JWT claims String is empty");
            throw e;
        }
    }

}
