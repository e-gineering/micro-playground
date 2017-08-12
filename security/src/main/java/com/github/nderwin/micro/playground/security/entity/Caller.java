package com.github.nderwin.micro.playground.security.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(schema = "security", name = "caller")
@NamedQueries({
    @NamedQuery(name = "Caller.findByUsername", query = "SELECT c FROM Caller c WHERE LOWER(c.username) = LOWER(:username)")
})
public class Caller implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(schema = "security", name = "caller_seq", sequenceName = "caller_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "caller_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "username", nullable = false, length = 255, unique = true)
    private String username;

    @Basic(optional = false)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    protected Caller() {
    }

    public Caller(final String username, final String password) {
        if (null == username || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must be a non-null, non-blank value");
        }

        this.username = username.trim();
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.username);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Caller other = (Caller) obj;
        return Objects.equals(this.username, other.username);
    }

}
