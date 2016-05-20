package org.arturjoshi.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
@ToString
public class User implements Identifiable<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "pass", nullable = false)
    @JsonIgnore
    private String pass;

    @Column(nullable = false)
    private String phonenumber;

    @Column(nullable = false)
    private String email;

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.pass = user.pass;
        this.phonenumber = user.phonenumber;
        this.email = user.email;
    }

    @ManyToMany
    @JoinTable(name = "friends",
        joinColumns = {@JoinColumn(name = "iduser1")},
        inverseJoinColumns = {@JoinColumn(name = "iduser2")})
    @JsonIgnore
    private Set<User> friends;

    @ManyToMany
    @JoinTable(name = "friends_requests",
        joinColumns = {@JoinColumn(name = "iduser1")},
        inverseJoinColumns = {@JoinColumn(name = "iduser2")})
    @JsonIgnore
    private Set<User> friendsRequests;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private Set<Event> eventsOrganized;

    @ManyToMany(mappedBy = "guests")
    @JsonIgnore
    private Set<Event> events;

    @ManyToMany(mappedBy = "invitations")
    @JsonIgnore
    private Set<Event> eventInvitations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!username.equals(user.username)) return false;
        if (!phonenumber.equals(user.phonenumber)) return false;

        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + phonenumber.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}