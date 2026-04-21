package io.project.paymybuddy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_relation")
public class Relation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relation_id", nullable = false)
    private User relation;

    public Relation() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getRelation() {
        return relation;
    }

    public void setRelation(User relation) {
        this.relation = relation;
    }
}
