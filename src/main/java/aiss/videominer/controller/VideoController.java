package aiss.videominer.controller;

import aiss.videominer.exception.BadRequestException;
import aiss.videominer.exception.ConflictException;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.Video;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Caption;
import aiss.videominer.repository.ChannelRepository;
import aiss.videominer.repository.VideoRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


@Tag(name = "Videos", description = "Operaciones con vídeos")
@RestController
@RequestMapping("/videominer/videos")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ChannelRepository channelRepository;


    @Operation(
        summary = "Crear un nuevo vídeo",
        description = "Crea un nuevo vídeo y lo almacena en la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vídeo creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o ID vacío"),
        @ApiResponse(responseCode = "409", description = "Vídeo ya existe con ese ID")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Video createVideo(@Valid @RequestBody Video video) {
        if (video.getId() == null || video.getId().isEmpty()) {
            throw new BadRequestException("Video ID cannot be blank");
        }

        if (videoRepository.existsById(video.getId())) {
            throw new ConflictException("Video already exists with id: " + video.getId());
        }

        // Establecer referencias bidireccionales para el author
        if (video.getAuthor() != null) {
            video.getAuthor().setVideo(video);
        }

        // Establecer referencias bidireccionales para los comments anidados
        if (video.getComments() != null && !video.getComments().isEmpty()) {
            for (Comment comment : video.getComments()) {
                comment.setVideo(video);
            }
        }

        // Establecer referencias bidireccionales para los captions anidados
        if (video.getCaptions() != null && !video.getCaptions().isEmpty()) {
            for (Caption caption : video.getCaptions()) {
                caption.setVideo(video);
            }
        }

        return videoRepository.save(video);
    }


    @Operation(
        summary = "Obtener todos los vídeos",
        description = "Devuelve una lista con todos los vídeos almacenados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de vídeos devuelta exitosamente")
    })
    @GetMapping
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }


    @Operation(
        summary = "Obtener vídeos de un canal",
        description = "Devuelve todos los vídeos que pertenecen a un canal específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de vídeos devuelta exitosamente"),
        @ApiResponse(responseCode = "404", description = "Canal no encontrado")
    })
    @GetMapping("/channel/{channelId}")
    public List<Video> getVideosByChannel(@PathVariable String channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new ResourceNotFoundException("Channel not found with id: " + channelId);
        }

        return videoRepository.findByChannel_Id(channelId);
    }


    @Operation(
        summary = "Obtener vídeo por ID",
        description = "Devuelve un vídeo específico por su ID. Retorna 404 si no existe"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vídeo encontrado"),
        @ApiResponse(responseCode = "404", description = "Vídeo no encontrado")
    })
    @GetMapping("/{id}")
    public Video getVideoById(@PathVariable String id) {
        Optional<Video> video = videoRepository.findById(id);
        if(video.isEmpty()){
            throw new ResourceNotFoundException("Video not found with id: " + id);
        }
        return video.get();
    }


    @Operation(
        summary = "Actualizar un vídeo",
        description = "Actualiza los datos de un vídeo existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vídeo actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Vídeo no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Video updateVideo(@PathVariable String id, @Valid @RequestBody Video videoDetails) {
        Optional<Video> video = videoRepository.findById(id);
        if(video.isEmpty()){
            throw new ResourceNotFoundException("Video not found with id: " + id);
        }
        Video _video = video.get();
        if (videoDetails.getName() != null) {
            _video.setName(videoDetails.getName());
        }
        if (videoDetails.getDescription() != null) {
            _video.setDescription(videoDetails.getDescription());
        }
        if (videoDetails.getReleaseTime() != null) {
            _video.setReleaseTime(videoDetails.getReleaseTime());
        }

        // Establecer referencias bidireccionales para el author
        if (videoDetails.getAuthor() != null) {
            videoDetails.getAuthor().setVideo(_video);
            _video.setAuthor(videoDetails.getAuthor());
        }

        // Establecer referencias bidireccionales para los comments anidados
        if (videoDetails.getComments() != null && !videoDetails.getComments().isEmpty()) {
            for (Comment comment : videoDetails.getComments()) {
                comment.setVideo(_video);
            }
            _video.setComments(videoDetails.getComments());
        }

        // Establecer referencias bidireccionales para los captions anidados
        if (videoDetails.getCaptions() != null && !videoDetails.getCaptions().isEmpty()) {
            for (Caption caption : videoDetails.getCaptions()) {
                caption.setVideo(_video);
            }
            _video.setCaptions(videoDetails.getCaptions());
        }

        return videoRepository.save(_video);
    }


    @Operation(
        summary = "Eliminar un vídeo",
        description = "Elimina un vídeo y todos sus datos asociados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Vídeo eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Vídeo no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVideo(@PathVariable String id) {
        if(!videoRepository.existsById(id)){
            throw new ResourceNotFoundException("Channel not found with id: " + id);
        }
        videoRepository.deleteById(id);
    }
}
