package aiss.videominer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "VMUser")
public class User {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty("name")
    @NotBlank(message = "User name cannot be blank")
    @Size(min = 1, max = 255, message = "User name must be between 1 and 255 characters")
    private String name;

    @JsonProperty("user_link")
    @NotBlank(message = "User link cannot be blank")
    @Size(max = 1000, message = "User link cannot exceed 1000 characters")
    private String user_link;

    @JsonProperty("picture_link")
    @Size(max = 1000, message = "Picture link cannot exceed 1000 characters")
    private String picture_link;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUser_link() { return user_link; }
    public void setUser_link(String user_link) { this.user_link = user_link; }

    public String getPicture_link() { return picture_link; }
    public void setPicture_link(String picture_link) { this.picture_link = picture_link; }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", user_link='" + user_link + '\'' +
                ", picture_link='" + picture_link + '\'' +
                '}';
    }
}
