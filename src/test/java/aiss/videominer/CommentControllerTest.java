package aiss.videominer;

import aiss.videominer.controller.CommentController;
import aiss.videominer.exception.BadRequestException;
import aiss.videominer.exception.ConflictException;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.Comment;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private CommentController commentController;

    private Comment fakeComment;

    @BeforeEach
    void setUp() {
        fakeComment = new Comment();
        fakeComment.setId("com1");
        fakeComment.setText("This is a test comment");
        fakeComment.setCreatedOn("2024-01-01");
    }

    @Test
    void whenGetAllComments_thenReturnList() {
        Page<Comment> fakePage = new PageImpl<>(List.of(fakeComment));
        when(commentRepository.findAll(any(Pageable.class))).thenReturn(fakePage);

        ResponseEntity<List<Comment>> response = commentController.getAllComments(0, 10, null, null);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("com1", response.getBody().get(0).getId());
    }

    @Test
    void whenGetAllComments_thenReturnEmptyList() {
        Page<Comment> fakePage = new PageImpl<>(List.of());
        when(commentRepository.findAll(any(Pageable.class))).thenReturn(fakePage);

        ResponseEntity<List<Comment>> response = commentController.getAllComments(0, 10, null, null);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void whenGetCommentByIdExists_thenReturnComment() {
        when(commentRepository.findById("com1")).thenReturn(Optional.of(fakeComment));

        Comment result = commentController.getCommentById("com1");

        assertNotNull(result);
        assertEquals("com1", result.getId());
        assertEquals("This is a test comment", result.getText());
    }

    @Test
    void whenGetCommentByIdNotExists_thenThrowNotFoundException() {
        when(commentRepository.findById("com999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentController.getCommentById("com999"));
    }

    @Test
    void whenGetCommentsByVideoExists_thenReturnComments() {
        when(videoRepository.existsById("vid1")).thenReturn(true);
        when(commentRepository.findByVideo_Id("vid1")).thenReturn(List.of(fakeComment));

        List<Comment> result = commentController.getCommentsByVideo("vid1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("com1", result.get(0).getId());
    }

    @Test
    void whenGetCommentsByVideoNotExists_thenThrowNotFoundException() {
        when(videoRepository.existsById("vid999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> commentController.getCommentsByVideo("vid999"));
    }

    @Test
    void whenCreateCommentWithEmptyId_thenThrowBadRequestException() {
        Comment comment = new Comment();
        comment.setId("");

        assertThrows(BadRequestException.class,
                () -> commentController.createComment(comment));
    }

    @Test
    void whenCreateCommentAlreadyExists_thenThrowConflictException() {
        when(commentRepository.existsById("com1")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> commentController.createComment(fakeComment));
    }

    @Test
    void whenCreateCommentNew_thenReturnSavedComment() {
        when(commentRepository.existsById("com1")).thenReturn(false);
        when(commentRepository.save(fakeComment)).thenReturn(fakeComment);

        Comment result = commentController.createComment(fakeComment);

        assertNotNull(result);
        assertEquals("com1", result.getId());
        verify(commentRepository, times(1)).save(fakeComment);
    }

    @Test
    void whenDeleteCommentExists_thenNoException() {
        when(commentRepository.existsById("com1")).thenReturn(true);
        doNothing().when(commentRepository).deleteById("com1");

        assertDoesNotThrow(() -> commentController.deleteComment("com1"));
        verify(commentRepository, times(1)).deleteById("com1");
    }

    @Test
    void whenDeleteCommentNotExists_thenThrowNotFoundException() {
        when(commentRepository.existsById("com999")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> commentController.deleteComment("com999"));
    }
}
