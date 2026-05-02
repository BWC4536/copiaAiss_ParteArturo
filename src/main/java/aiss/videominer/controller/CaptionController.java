package aiss.videominer.controller;

import aiss.videominer.exception.BadRequestException;
import aiss.videominer.exception.ConflictException;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Tag(name = "Captions", description = "Operaciones con subtítulos")
@RestController
@RequestMapping("/videominer/captions")
public class CaptionController {

    @Autowired
    private CaptionRepository captionRepository;

    @Autowired
    private VideoRepository videoRepository;


    @Operation(
        summary = "Obtener subtítulos de un vídeo",
        description = "Devuelve todos los subtítulos asociados a un vídeo específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de subtítulos devuelta exitosamente"),
        @ApiResponse(responseCode = "404", description = "Vídeo no encontrado")
    })
    @GetMapping("/video/{videoId}")
    public List<Caption> getCaptionsByVideo(@PathVariable String videoId) {
        if (!videoRepository.existsById(videoId)) {
            throw new ResourceNotFoundException("Video not found with id: " + videoId);
        }

        return captionRepository.findByVideo_Id(videoId);
    }


    @Operation(
        summary = "Crear un nuevo subtítulo",
        description = "Crea un nuevo subtítulo y lo almacena en la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Subtítulo creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o ID vacío"),
        @ApiResponse(responseCode = "409", description = "Subtítulo ya existe con ese ID")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Caption createCaption(@Valid @RequestBody Caption caption) {
        if (caption.getId() == null || caption.getId().isEmpty()) {
            throw new BadRequestException("Caption ID cannot be blank");
        }

        if (captionRepository.existsById(caption.getId())) {
            throw new ConflictException("Caption already exists with id: " + caption.getId());
        }

        return captionRepository.save(caption);
    }


    @Operation(
        summary = "Obtener todos los subtítulos",
        description = "Devuelve una lista con todos los subtítulos almacenados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de subtítulos devuelta exitosamente")
    })
    @GetMapping
    public List<Caption> getAllCaptions() {
        return captionRepository.findAll();
    }


    @Operation(
        summary = "Obtener subtítulo por ID",
        description = "Devuelve un subtítulo específico por su ID. Retorna 404 si no existe"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subtítulo encontrado"),
        @ApiResponse(responseCode = "404", description = "Subtítulo no encontrado")
    })
    @GetMapping("/{id}")
    public Caption getCaptionById(@PathVariable String id) {
        Optional<Caption> caption = captionRepository.findById(id);
        if(caption.isEmpty()){
            throw new ResourceNotFoundException("caption not found with id: " + id);
        }
        return caption.get();
    }


    @Operation(
        summary = "Eliminar un subtítulo",
        description = "Elimina un subtítulo de la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Subtítulo eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Subtítulo no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCaption(@PathVariable String id) {
        if(!captionRepository.existsById(id)){
            throw new ResourceNotFoundException("Caption not found with id: " + id);
        }
        captionRepository.deleteById(id);
    }
}
