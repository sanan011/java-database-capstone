package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey signingKey;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /** * FIXED: Added back the single-string argument version 
     * Required by SharedService and Patient login
     */
    public String generateToken(String identifier) {
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Overloaded method for DoctorService (using ID and Role) */
    public String generateToken(Long id, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Alias for PatientService compatibility */
    public String extractEmail(String token) {
        return extractIdentifier(token);
    }

    /** * FIXED: Changed parserBuilder() to parser() 
     * This resolves the 'cannot find symbol' error for newer JJWT versions
     */
    public String extractIdentifier(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            return null; 
        }
    }

    /**
     * NEW METHOD: Extract ID from token as Long
     * Used for doctor and patient IDs stored in JWT subject
     */
    public Long extractId(String token) {
        try {
            String identifier = extractIdentifier(token);
            if (identifier == null) return null;
            return Long.parseLong(identifier);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);
            if (identifier == null) return false;

            switch (userType.toLowerCase()) {
                case "admin":
                    return adminRepository.findByUsername(identifier) != null;
                case "doctor":
                    return doctorRepository.findByEmail(identifier) != null;
                case "patient":
                    return patientRepository.findByEmail(identifier) != null;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public SecretKey getSigningKey() {
        return signingKey;
    }
}