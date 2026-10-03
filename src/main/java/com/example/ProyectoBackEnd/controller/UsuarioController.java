package com.example.ProyectoBackEnd.controller;

import com.example.ProyectoBackEnd.dao.entity.UsuarioEntity;
import com.example.ProyectoBackEnd.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import com.example.ProyectoBackEnd.controller.TaskController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{id}")
    public EntityModel<UsuarioEntity> obtenerUno(@PathVariable(value = "id") int codigo) {
        log.info("API: Consultando detalles del usuario codigo: {}", codigo);
        UsuarioEntity u = usuarioService.buscarPorId(codigo);

        return EntityModel.of(u,
                linkTo(methodOn(UsuarioController.class).obtenerUno(codigo)).withSelfRel());
    }

    @GetMapping
    public List<UsuarioEntity> listarTodos() {
        log.info("API: Listando todos los usuarios registrados");
        return usuarioService.listarTodos();
    }

    @PostMapping
    public void guardar(@RequestBody UsuarioEntity usuario) {
        log.info("API: Guardando nuevo usuario: {}", usuario.getUsuario());
        usuarioService.guardarUsuario(usuario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        log.info("API: Eliminando (lógico) usuario con ID: {}", id);
        usuarioService.eliminarUsuario(id);
    }

    @GetMapping("/eliminados")
    public List<UsuarioEntity> listarEliminados() {
        log.info("API: Consultando lista de usuarios inactivos");
        return usuarioService.listarEliminados();
    }

    @GetMapping("/buscarPorNombre")
    public EntityModel<UsuarioEntity> buscarPorNombre(@RequestParam("usuario") String nombre) {
        log.info("API: Buscando usuario por nombre: {}", nombre);
        UsuarioEntity u = usuarioService.buscarPorNombre(nombre); // Asegúrate de que el Service tenga este método

        if (u == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return EntityModel.of(u,
                linkTo(methodOn(UsuarioController.class).obtenerUno(u.getIdUsuario())).withSelfRel());
    }

}
