package com.e_gineering.micro.playground.security.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(schema = "security", name = "invalid_token")
@NamedQueries({
    @NamedQuery(name = "InvalidToken.deleteExpiredTokens", query = "DELETE FROM InvalidToken it WHERE it.expirationDate < CURRENT_TIMESTAMP")
})
public class InvalidToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "token", length = 8192)
    private String token;

    @Basic(optional = false)
    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    protected InvalidToken() {
    }

    public InvalidToken(final String token, final Date expirationDate) {
        this.token = token;
        this.expirationDate = expirationDate;
    }
    
    public String getToken() {
        return token;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.token);
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
        final InvalidToken other = (InvalidToken) obj;
        return Objects.equals(this.token, other.token);
    }

}
