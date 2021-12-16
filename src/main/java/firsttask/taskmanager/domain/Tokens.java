package firsttask.taskmanager.domain;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
//@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
public class Tokens {
    @Id
    private String jwtToken;

    @ManyToOne
    private User user;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Tokens{" +"username"+user.getUsername()+'\''+
                "jwtToken='" + jwtToken + '\'' +
                '}';
    }
}
