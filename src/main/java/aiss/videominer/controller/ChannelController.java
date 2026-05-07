package aiss.videominer.controller;

import aiss.videominer.exception.BadRequestException;
import aiss.videominer.exception.ConflictException;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.model.Video;
import aiss.videominer.model.Comment;
import aiss.videominer.model.Caption;
import aiss.videominer.repository.ChannelRepository;
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


@Tag(name = "Channels", description = "Operaciones con canales")
@RestController
@RequestMapping("/videominer/channels")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    public ChannelController(ChannelRepository channelRepository){
        this.channelRepository = channelRepository;
    }

    @Operation(
        summary = "Crear un nuevo canal",
        description = "Crea un nuevo canal y lo almacena en la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Canal creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o ID vacío"),
        @ApiResponse(responseCode = "409", description = "Canal ya existe con ese ID")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Channel createChannel(@Valid @RequestBody Channel channel) {
        if (channel.getId() == null || channel.getId().isEmpty()) {
            throw new BadRequestException("Channel ID cannot be empty");
        }

        if (channelRepository.existsById(channel.getId())) {
            throw new ConflictException("Channel already exists with id: " + channel.getId());
        }

        // Establecer referencias bidireccionales para los videos anidados
        if (channel.getVideos() != null && !channel.getVideos().isEmpty()) {
            for (Video video : channel.getVideos()) {
                // Establecer referencia inversa del video al canal
                video.setChannel(channel);


                // Establecer referencias bidireccionales para comments
                if (video.getComments() != null && !video.getComments().isEmpty()) {
                    for (Comment comment : video.getComments()) {
                        comment.setVideo(video);
                    }
                }

                // Establecer referencias bidireccionales para captions
                if (video.getCaptions() != null && !video.getCaptions().isEmpty()) {
                    for (Caption caption : video.getCaptions()) {
                        caption.setVideo(video);
                    }
                }
            }
        }

        return channelRepository.save(channel);
    }


    @Operation(
        summary = "Obtener todos los canales",
        description = "Devuelve una lista con todos los canales almacenados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de canales devuelta exitosamente")
    })
    @GetMapping
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }


    @Operation(
        summary = "Obtener un canal por ID",
        description = "Devuelve un canal específico por su ID. Retorna 404 si no existe"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal encontrado"),
        @ApiResponse(responseCode = "404", description = "Canal no encontrado")
    })
    @GetMapping("/{id}")
    public Channel getChannelById(@PathVariable String id) {
        Optional<Channel> channel = channelRepository.findById(id);
        if(channel.isEmpty()){
            throw new ResourceNotFoundException("Channel not found with id: " + id);
        }
        return channel.get();
    }

    @Operation(
        summary = "Actualizar un canal",
        description = "Actualiza los datos de un canal existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Canal actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Canal no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public Channel updateChannel(@PathVariable String id, @Valid @RequestBody Channel channelDetails) {
        Optional<Channel> channel = channelRepository.findById(id);
        if(channel.isEmpty()){
            throw new ResourceNotFoundException("Channel not found with id: " + id);
        }
        Channel _channel = channel.get();
        if (channelDetails.getName() != null) {
            _channel.setName(channelDetails.getName());
        }
        if (channelDetails.getDescription() != null) {
            _channel.setDescription(channelDetails.getDescription());
        }
        if (channelDetails.getCreatedTime() != null) {
            _channel.setCreatedTime(channelDetails.getCreatedTime());
        }
        if (channelDetails.getSubscriberCount() != null) {
            _channel.setSubscriberCount(channelDetails.getSubscriberCount());
        }

        // Establecer referencias bidireccionales para los videos anidados
        if (channelDetails.getVideos() != null && !channelDetails.getVideos().isEmpty()) {
            for (Video video : channelDetails.getVideos()) {
                // Establecer referencia inversa del video al canal
                video.setChannel(_channel);

                // Establecer referencias bidireccionales para comments
                if (video.getComments() != null && !video.getComments().isEmpty()) {
                    for (Comment comment : video.getComments()) {
                        comment.setVideo(video);
                    }
                }

                // Establecer referencias bidireccionales para captions
                if (video.getCaptions() != null && !video.getCaptions().isEmpty()) {
                    for (Caption caption : video.getCaptions()) {
                        caption.setVideo(video);
                    }
                }
            }
            _channel.getVideos().clear();
            _channel.getVideos().addAll(channelDetails.getVideos());
        }

        return channelRepository.save(_channel);
    }

    @Operation(
        summary = "Eliminar un canal",
        description = "Elimina un canal y todos sus datos asociados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Canal eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Canal no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(@PathVariable String id) {
        if(!channelRepository.existsById(id)){
            throw new ResourceNotFoundException("Channel not found with id: " + id);
        }
        channelRepository.deleteById(id);
    }
}
