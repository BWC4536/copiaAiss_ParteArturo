package aiss.videominer.repository;

import aiss.videominer.model.Caption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CaptionRepository extends JpaRepository<Caption, String> {

    @Query("SELECT c FROM Caption c WHERE c.video.id = :videoId")
    List<Caption> findByVideo_Id(@Param("videoId") String videoId);
}