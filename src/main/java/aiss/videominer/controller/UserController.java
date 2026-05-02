package aiss.videominer.controller;

import aiss.videominer.exception.ConflictException;
import aiss.videominer.exception.ResourceNotFoundException;
import aiss.videominer.model.User;
import aiss.videominer.model.Video;
import aiss.videominer.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Tag(name = "Users", description = "Operaciones con usuarios")
@RestController
@RequestMapping("/videominer/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @Operation(
        summary = "Crear un nuevo usuario",
        description = "Crea un nuevo usuario y lo almacena en la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o ID vacío"),
        @ApiResponse(responseCode = "409", description = "Usuario ya existe con ese ID")
    })
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getId() == null || user.getId().toString().isEmpty()) {
            throw new ResourceNotFoundException("User ID cannot be blank");
        }

        if (userRepository.existsById(user.getId().toString())) {
            throw new ConflictException("User already exists with id: "+user.getId());
        }

        return userRepository.save(user);
    }

    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Devuelve una lista con todos los usuarios almacenados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios devuelta exitosamente")
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(
        summary = "Obtener usuario por ID",
        description = "Devuelve un usuario específico por su ID. Retorna 404 si no existe"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return user.get();
    }


    @Operation(
        summary = "Actualizar un usuario",
        description = "Actualiza los datos de un usuario existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @Valid @RequestBody User userDetails) {
        Optional<User> user = userRepository.findById(id);

        if(user.isEmpty()){
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        User _user = user.get();
        if (userDetails.getName() != null) {
            _user.setName(userDetails.getName());
        }
        if (userDetails.getUser_link() != null) {
            _user.setUser_link(userDetails.getUser_link());
        }
        if (userDetails.getPicture_link() != null) {
            _user.setPicture_link(userDetails.getPicture_link());
        }

        return userRepository.save(_user);
    }


    @Operation(
        summary = "Eliminar un usuario",
        description = "Elimina un usuario de la base de datos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
