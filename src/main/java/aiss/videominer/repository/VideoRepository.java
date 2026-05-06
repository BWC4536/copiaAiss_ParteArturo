package aiss.videominer.repository;

import aiss.videominer.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VideoRepository extends JpaRepository<Video, String> {

    Page<Video> findById(String id, Pageable pageable);

    @Query(value = "SELECT * FROM Video WHERE channelId = :channelId", nativeQuery = true)
    List<Video> findByChannel_Id(@Param("channelId") String channelId);
}