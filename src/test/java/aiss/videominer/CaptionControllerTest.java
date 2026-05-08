package aiss.videominer;

import aiss.videominer.controller.CaptionController;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CaptionControllerTest {

    @Mock
    private CaptionRepository captionRepository;

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private CaptionController captionController;

    private Caption fakeCaption;

    @BeforeEach
    void setUp() {
        fakeCaption = new Caption();
        fakeCaption.setId("cap1");
        fakeCaption.setName("English subtitles");
        fakeCaption.setLanguage("en");
    }

    @Test
    void whenGetAllCaptions_thenReturnList() {
        when(captionRepository.findAll()).thenReturn(List.of(fakeCaption));

        List<Caption> result = captionController.getAllCaptions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("cap1", result.get(0).getId());
    }

    @Test
    void whenGetAllCaptions_thenReturnEmptyList() {
        when(captionRepository.findAll()).thenReturn(List.of());

        List<Caption> result = captionController.getAllCaptions();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenGetCaptionByIdExists_thenReturnCaption() {
        when(captionRepository.findById("cap1")).thenReturn(Optional.of(fakeCaption));

        Caption result = captionController.getCaptionById("cap1");

        assertNotNull(result);
        assertEquals("cap1", result.getId());
        assertEquals("en", result.getLanguage());
    }

    @Test
    void whenGetCaptionByIdNotExists_thenThrowNotFoundException() {
        when(captionRepository.findById("cap999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> captionController.getCaptionById("cap999"));
    }

    @Test
    void whenGetCaptionsByVideoExists_thenReturnCaptions() {
        when(videoRepository.existsById("vid1")).thenReturn(true);
        when(captionRepository.findByVideo_Id("vid1")).thenReturn(List.of(fakeCaption));

        List<Caption> result = captionController.getCaptionsByVideo("vid1");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void whenGetCaptionsByVideoNotExists_thenThrowNotFoundException() {
        when(videoRepository.existsById("vid999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> captionController.getCaptionsByVideo("vid999"));
    }

    @Test
    void whenDeleteCaptionExists_thenNoException() {
        when(captionRepository.existsById("cap1")).thenReturn(true);
        doNothing().when(captionRepository).deleteById("cap1");

        assertDoesNotThrow(() -> captionController.deleteCaption("cap1"));
        verify(captionRepository, times(1)).deleteById("cap1");
    }

    @Test
    void whenDeleteCaptionNotExists_thenThrowNotFoundException() {
        when(captionRepository.existsById("cap999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> captionController.deleteCaption("cap999"));
    }
}
