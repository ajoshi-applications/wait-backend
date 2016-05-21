package org.arturjoshi.users.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.arturjoshi.events.domain.Event;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
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

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "pass", nullable = false)
    @JsonIgnore
    private String pass;

    @Column
    private String phonenumber;

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.pass = user.pass;
        this.phonenumber = user.phonenumber;
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
        return phonenumber.equals(user.phonenumber);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + phonenumber.hashCode();
        return result;
    }
}