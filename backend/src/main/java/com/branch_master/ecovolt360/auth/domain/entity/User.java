package com.branch_master.ecovolt360.auth.domain.entity;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.*;


@Entity(name = "AuthUser")
@Table(name = "users")
public class  User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    @Column(name = "address_id")
    private UUID addressId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "token_version", nullable = false)
    private int tokenVersion = 0;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public Role getRole() {
        return role;
    }
    public int getTokenVersion() {
        return tokenVersion;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public void incrementTokenVersion() {
        this.tokenVersion++;
    }

    /**
     * A sessão está inativa quando não há atividade registrada ou quando o limite de
     * inatividade foi atingido (exatamente no limite já conta como expirada).
     */
    public boolean isIdleFor(Duration limit, Instant now) {
        return lastActivityAt == null || !now.isBefore(lastActivityAt.plus(limit));
    }

    /**
     * Inicia uma nova sessão no login. Se havia uma sessão inativa, invalida os tokens
     * antigos para que não voltem a valer quando a atividade for reiniciada.
     */
    public void startSession(Duration limit, Instant now) {
        if (isIdleFor(limit, now)) {
            incrementTokenVersion();
        }
        this.lastActivityAt = now;
    }

    protected  User() {

    }
}