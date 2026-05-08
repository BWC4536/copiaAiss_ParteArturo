package aiss.videominer.repository;

import aiss.videominer.model.Comment;
import aiss.videominer.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    Page<Comment> findById(String id, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.video.id = ?1")
    List<Comment> findByVideo_Id(String videoId);
}