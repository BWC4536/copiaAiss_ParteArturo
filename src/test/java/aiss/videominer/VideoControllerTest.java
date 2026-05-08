package aiss.videominer;

import aiss.videominer.controller.VideoController;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.Video;
import aiss.videominer.repository.ChannelRepository;
import aiss.videominer.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VideoControllerTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private VideoController videoController;

    private Video fakeVideo;

    @BeforeEach
    void setUp() {
        fakeVideo = new Video();
        fakeVideo.setId("123");
        fakeVideo.setName("Test Video");
        fakeVideo.setDescription("Test Description");
    }

    @Test
    void whenGetVideoExists_thenReturnVideo() {
        // GIVEN
        when(videoRepository.findById("123")).thenReturn(Optional.of(fakeVideo));

        // WHEN
        Video result = videoController.getVideoById("123");

        // THEN
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Test Video", result.getName());
    }

    @Test
    void whenGetVideoNotExists_thenThrowNotFoundException() {
        // GIVEN
        when(videoRepository.findById("999")).thenReturn(Optional.empty());

        // THEN
        assertThrows(ResourceNotFoundException.class,
                () -> videoController.getVideoById("999"));
    }

    @Test
    void whenGetAllVideos_thenReturnList() {
        // GIVEN
        Page<Video> fakePage = new PageImpl<>(List.of(fakeVideo));
        when(videoRepository.findAll(any(Pageable.class))).thenReturn(fakePage);

        // WHEN
        List<Video> result = videoController.getAllVideos(0, 10, null, null);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void whenGetAllVideos_thenReturnEmptyList() {
        // GIVEN
        Page<Video> fakePage = new PageImpl<>(List.of());
        when(videoRepository.findAll(any(Pageable.class))).thenReturn(fakePage);

        // WHEN
        List<Video> result = videoController.getAllVideos(0, 10, null, null);

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenGetVideosByChannelNotExists_thenThrowNotFoundException() {
        // GIVEN
        when(channelRepository.existsById("chan999")).thenReturn(false);

        // THEN
        assertThrows(ResourceNotFoundException.class,
                () -> videoController.getVideosByChannel("chan999"));
    }

    @Test
    void whenGetVideosByChannelExists_thenReturnVideos() {
        // GIVEN
        when(channelRepository.existsById("chan1")).thenReturn(true);
        when(videoRepository.findByChannel_Id("chan1")).thenReturn(List.of(fakeVideo));

        // WHEN
        List<Video> result = videoController.getVideosByChannel("chan1");

        // THEN
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("123", result.get(0).getId());
    }
}
