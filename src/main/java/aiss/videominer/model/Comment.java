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
@Table(name = "Comment")
public class Comment {

    @Id
    @JsonProperty("id")
    @NotBlank(message = "Comment ID cannot be blank")
    private String id;

    @JsonProperty("text")
    @Column(columnDefinition="TEXT")
    //@NotBlank(message = "Comment text cannot be blank")
    @Size(max = 5000, message = "Comment text cannot exceed 5000 characters")
    private String text;

    @JsonProperty("createdOn")
    //@NotBlank(message = "Comment creation date cannot be blank")
    private String createdOn;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    @NotNull(message = "Comment must belong to a video")
    private Video video;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", video=" + (video != null ? video.getId() : "null") +
                '}';
    }
}
