package org.thinkbigthings.zdd.server;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
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
    private Instant registrationTime;

    @Basic
    @NotNull
    private String phoneNumber = "";

    @Basic
    @NotNull
    private int heightCm = 0;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserType typeName = UserType.REGISTERED;

    @OneToMany(fetch=FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval=true)
    private Set<Address> addresses = new HashSet<>();

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

    public Instant getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Instant registration) {
        this.registrationTime = registration;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(int height) {
        this.heightCm = height;
    }

    public UserType getTypeName() {
        return typeName;
    }

    public void setTypeName(UserType typeName) {
        this.typeName = typeName;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

}
