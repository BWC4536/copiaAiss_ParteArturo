package aiss.videominer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Juan C. Alonso
 */
@Entity
@Table(name = "Caption")
public class Caption {

    @Id
    @JsonProperty("id")
    @NotBlank(message = "Caption ID cannot be blank")
    private String id;

    @JsonProperty("name")
    // @NotBlank(message = "Caption name cannot be blank")
    @Size(max = 1000, message = "Caption name cannot exceed 1000 characters")
    private String name;

    @JsonProperty("language")
    // @NotBlank(message = "Caption language cannot be blank")
    @Size(min = 1, max = 50, message = "Caption language must be between 1 and 50 characters")
    private String language;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    @NotNull(message = "Caption must belong to a video")
    private Video video;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "Caption{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", video=" + (video != null ? video.getId() : "null") +
                '}';
    }
}