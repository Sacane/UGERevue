package fr.pentagon.ugeoverflow.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue()
    private long id;
    private String name;
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name = "user_tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> usersOf = new HashSet<>();
    @ManyToMany(mappedBy = "tagsList")
    private Set<Review> reviewsOf = new HashSet<>();

    public Tag(){}

    public Tag(String name){
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsersOf() {
        return usersOf;
    }

    public void setUsersOf(Set<User> usersOf) {
        this.usersOf = usersOf;
    }

    public Set<Review> getReviewsOf() {
        return reviewsOf;
    }

    public void setReviewsOf(Set<Review> reviewsOf) {
        this.reviewsOf = reviewsOf;
    }

    public void addUser(User user){
        this.usersOf.add(user);
    }
    public void addReview(Review review){
        this.reviewsOf.add(review);
    }

    public void removeUser(User user){
        this.usersOf.remove(user);
    }

    public void removeReview(Review review){
        this.reviewsOf.remove(review);
    }
}
