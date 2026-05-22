package movierental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Contributor {

    @Id
    private String id;

    private String name;
    private String role;
    private LocalDate dateOfBirth;
    private String country;

    // PHOTO
    private String photo;

    // NEW ATTRIBUTES
    private String description;
    private String awards;
    private String notableWorks;

    // DEFAULT CONSTRUCTOR
    public Contributor() {}

    // UPDATED CONSTRUCTOR
    public Contributor(
            String id,
            String name,
            String role,
            int age,
            String country,
            String photo,
            String description,
            String awards,
            String notableWorks
    ) {

        this.id = id;
        this.name = name;
        this.role = role;
        this.country = country;
        this.photo = photo;
        this.description = description;
        this.awards = awards;
        this.notableWorks = notableWorks;
    }

    // GETTERS & SETTERS

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // NAME
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ROLE
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // COUNTRY
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // PHOTO
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    // DESCRIPTION
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // AWARDS
    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    // NOTABLE WORKS
    public String getNotableWorks() {
        return notableWorks;
    }

    public void setNotableWorks(String notableWorks) {
        this.notableWorks = notableWorks;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}