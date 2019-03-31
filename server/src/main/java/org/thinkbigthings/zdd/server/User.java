package org.thinkbigthings.zdd.server;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User {

    public enum UserType {
        REGISTERED, GUEST
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, insertable = false, nullable = false)
    private Long id;

    @Column(unique=true)
    @NotNull
    @Size(min = 3, message = "must be at least three characters")
    private String username = "";

    @NotNull
    @Column(unique=true)
    @Size(min = 3, message = "must be at least three characters")
    private String email = "";

    @NotNull
    @Size(min = 3, message = "must be at least three characters")
    @Column(name="display_name")
    private String displayName = "";

    @Basic
    private boolean enabled = false;

    @Basic
    @NotNull
    private String favoriteColor = "NONE";

    @Basic
    @NotNull
    private Instant registration = Instant.now();

    @Basic
    @NotNull
    private String height = "0";

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserType typeName = UserType.REGISTERED;

    protected User() {

    }

    public User(String name) {
        this(name, name);
    }

    public User(String name, String display) {
        username = name;
        displayName = display;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getRegistration() {
        return registration;
    }

    public void setRegistration(Instant registration) {
        this.registration = registration;
    }

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String favoriteColor) {
        this.favoriteColor = favoriteColor;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public UserType getTypeName() {
        return typeName;
    }

    public void setTypeName(UserType typeName) {
        this.typeName = typeName;
    }
}
