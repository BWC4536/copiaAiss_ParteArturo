package aiss.videominer.controller;

import aiss.videominer.exception.BadRequestException;
import aiss.videominer.exception.ConflictException;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Comment;
import aiss.videominer.repository.CommentRepository;
import aiss.videominer.repository.VideoRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Comments", description = "Operaciones con comentarios")
@RestController
@RequestMapping("/videominer/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Operation(
        summary = "Obtener comentarios de un vídeo",
        description = "Devuelve todos los comentarios asociados a un vídeo específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comentarios devuelta exitosamente"),
        @ApiResponse(responseCode = "404", description = "Vídeo no encontrado")
    })
    @GetMapping("/video/{videoId}")
    public List<Comment> getCommentsByVideo(@PathVariable String videoId) {
        if (!videoRepository.existsById(videoId)) {
            throw new ResourceNotFoundException("Video not found with id: " + videoId);
        }
        return commentRepository.findByVideo_Id(videoId);
    }

    @Operation(
        summary = "Crear un nuevo comentario",
        description = "Crea un nuevo comentario y lo almacena en la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comentario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o ID vacío"),
        @ApiResponse(responseCode = "409", description = "Comentario ya existe con ese ID")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@Valid @RequestBody Comment comment) {
        if (comment.getId() == null || comment.getId().isEmpty()) {
            throw new BadRequestException("Comment ID cannot be blank");
        }
        if (commentRepository.existsById(comment.getId())) {
            throw new ConflictException("Comment already exists with id: " + comment.getId());
        }

        return commentRepository.save(comment);
    }

    @Operation(
        summary = "Obtener todos los comentarios",
        description = "Devuelve una lista con todos los comentarios almacenados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de comentarios devuelta exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false) String id,
                                                        @RequestParam(required = false) String order) {
        Pageable paging;

        if(order != null){
            if(order.startsWith("-")){
                paging = PageRequest.of(page, size, Sort.by(order.substring(1)).descending());
            } else {
                paging = PageRequest.of(page, size, Sort.by(order).ascending());
            }
        } else{
            paging = PageRequest.of(page, size);
        }

        Page<Comment> pageComments;

        if(id != null){
            pageComments = commentRepository.findById(id, paging);
        } else {
            pageComments = commentRepository.findAll(paging);
        }

        return ResponseEntity.ok(pageComments.getContent());
    }

    @Operation(
        summary = "Obtener comentario por ID",
        description = "Devuelve un comentario específico por su ID. Retorna 404 si no existe"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comentario encontrado"),
        @ApiResponse(responseCode = "404", description = "Comentario no encontrado")
    })
    @GetMapping("/{id}")
    public Comment getCommentById(@PathVariable String id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isEmpty()){
            throw new ResourceNotFoundException("comment not found with id: " + id);
        }
        return comment.get();
    }

    @Operation(
        summary = "Eliminar un comentario",
        description = "Elimina un comentario de la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comentario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Comentario no encontrado")
    })
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable String id) {
        if(!commentRepository.existsById(id)){
            throw new ResourceNotFoundException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }
}
