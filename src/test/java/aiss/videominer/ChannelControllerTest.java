package aiss.videominer;

import aiss.videominer.controller.ChannelController;
import aiss.videominer.exception.BadRequestException;
import aiss.videominer.exception.ConflictException;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
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
public class ChannelControllerTest {

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelController channelController;

    private Channel fakeChannel;

    @BeforeEach
    void setUp() {
        fakeChannel = new Channel();
        fakeChannel.setId("chan1");
        fakeChannel.setName("Test Channel");
        fakeChannel.setDescription("Test Description");
        fakeChannel.setCreatedTime("2024-01-01");
    }

    @Test
    void whenGetAllChannels_thenReturnList() {
        Page<Channel> fakePage = new PageImpl<>(List.of(fakeChannel));
        when(channelRepository.findAll(any(Pageable.class))).thenReturn(fakePage);

        List<Channel> result = channelController.getAllChannels(0, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("chan1", result.get(0).getId());
    }

    @Test
    void whenGetAllChannels_thenReturnEmptyList() {
        Page<Channel> fakePage = new PageImpl<>(List.of());
        when(channelRepository.findAll(any(Pageable.class))).thenReturn(fakePage);

        List<Channel> result = channelController.getAllChannels(0, 10, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenGetChannelByIdExists_thenReturnChannel() {
        when(channelRepository.findById("chan1")).thenReturn(Optional.of(fakeChannel));

        Channel result = channelController.getChannelById("chan1");

        assertNotNull(result);
        assertEquals("chan1", result.getId());
        assertEquals("Test Channel", result.getName());
    }

    @Test
    void whenGetChannelByIdNotExists_thenThrowNotFoundException() {
        when(channelRepository.findById("chan999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> channelController.getChannelById("chan999"));
    }

    @Test
    void whenCreateChannelWithEmptyId_thenThrowBadRequestException() {
        Channel channel = new Channel();
        channel.setId("");

        assertThrows(BadRequestException.class,
                () -> channelController.createChannel(channel));
    }

    @Test
    void whenCreateChannelAlreadyExists_thenThrowConflictException() {
        when(channelRepository.existsById("chan1")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> channelController.createChannel(fakeChannel));
    }

    @Test
    void whenCreateChannelNew_thenReturnSavedChannel() {
        when(channelRepository.existsById("chan1")).thenReturn(false);
        when(channelRepository.save(fakeChannel)).thenReturn(fakeChannel);

        Channel result = channelController.createChannel(fakeChannel);

        assertNotNull(result);
        assertEquals("chan1", result.getId());
        verify(channelRepository, times(1)).save(fakeChannel);
    }

    @Test
    void whenUpdateChannelNotExists_thenThrowNotFoundException() {
        when(channelRepository.findById("chan999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> channelController.updateChannel("chan999", fakeChannel));
    }

    @Test
    void whenUpdateChannelExists_thenReturnUpdatedChannel() {
        Channel updatedDetails = new Channel();
        updatedDetails.setId("chan1");
        updatedDetails.setName("Updated Name");
        updatedDetails.setDescription("Updated Description");

        when(channelRepository.findById("chan1")).thenReturn(Optional.of(fakeChannel));
        when(channelRepository.save(any(Channel.class))).thenReturn(fakeChannel);

        Channel result = channelController.updateChannel("chan1", updatedDetails);

        assertNotNull(result);
        verify(channelRepository, times(1)).save(any(Channel.class));
    }

    @Test
    void whenDeleteChannelExists_thenNoException() {
        when(channelRepository.existsById("chan1")).thenReturn(true);
        doNothing().when(channelRepository).deleteById("chan1");

        assertDoesNotThrow(() -> channelController.deleteChannel("chan1"));
        verify(channelRepository, times(1)).deleteById("chan1");
    }

    @Test
    void whenDeleteChannelNotExists_thenThrowNotFoundException() {
        when(channelRepository.existsById("chan999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> channelController.deleteChannel("chan999"));
    }
}
