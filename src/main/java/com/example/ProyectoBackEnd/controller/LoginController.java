package com.example.ProyectoBackEnd.controller;

import com.example.ProyectoBackEnd.dao.entity.AuditoriaEntity;
import com.example.ProyectoBackEnd.dao.entity.UsuarioEntity;
import com.example.ProyectoBackEnd.repository.AuditoriaRepositorio;
import com.example.ProyectoBackEnd.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping("/api/auth-rest")
@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = "X-Usuario-Logueado") //Lo usaremos para poder enlazar el FrontEND usando React
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaRepositorio auditoriaRepositorio;

    @PostMapping("/loginAccion")
    public ResponseEntity<?> loginAccion(@RequestBody UsuarioEntity ce, HttpSession session) {
        log.info("AUDITORIA: Intento de login para usuario: {}", ce.getUsuario());

        // 1. Buscamos por nombre para validar estados específicos
        UsuarioEntity dbUser = usuarioService.buscarPorNombre(ce.getUsuario());
         // CASO 1: Usuario no existe en BD
        if (dbUser == null) {
            log.warn("Login NO REGISTRADO: {}", ce.getUsuario());
            registrarIntentoFallido(null, "Intento fallido: Usuario no existe (" + ce.getUsuario() + ")");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado en el sistema");
            //throw new RuntimeException("Usuario no encontrado en el sistema");
        }

        // CASO 2: Usuario inactivo
        if (dbUser.getEstado() == 0) {
            log.warn("Login FALLIDO: El usuario '{}' está INACTIVO.", dbUser.getUsuario());
            registrarIntentoFallido(dbUser, "Intento fallido: Usuario inactivo (" + dbUser.getUsuario() + ")");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Usuario inactivo. Contacte al administrador.");
        }
  
        
        /*
        if (dbUser.getEstado() == 0) {
            log.warn("Login FALLIDO: El usuario '{}' está INACTIVO.", dbUser.getUsuario());
            registrarIntentoFallido(dbUser, "Intento fallido: Usuario inactivo");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Usuario inactivo. Contacte al administrador.");
            //throw new RuntimeException();
        }*/

        // 2. Validación de credenciales
        UsuarioEntity ue = usuarioService.validarLogin(ce);
        if (ue != null) {
            session.setAttribute("usuarioLogueado", ue);
            log.info("Login exitoso. Derivando a principal.", ue.getUsuario());

            // Retorno con HATEOAS  
            //return EntityModel.of(ue,
            //linkTo(methodOn(LoginController.class).obtenerUno(ue.getIdUsuario())).withSelfRel(),
            //linkTo(methodOn(TaskController.class).listarPaginado(0, 5)).withRel("tareas_principales")
            EntityModel<UsuarioEntity> recurso = EntityModel.of(ue,
                    linkTo(methodOn(LoginController.class).obtenerUno(ue.getIdUsuario())).withSelfRel(),
                    linkTo(methodOn(TaskController.class).listarPaginado(0, 5)).withRel("tareas_principales"
                    )
            );
            return ResponseEntity.ok(recurso);
        } else {
            log.warn("AUDITORIA: Contraseña incorrecta para el usuario {}", ce.getUsuario());
            //throw new RuntimeException("Credenciales inválidas");
            registrarIntentoFallido(dbUser, "Intento fallido: Contraseña incorrecta (" + dbUser.getUsuario() + ")"); // <-- LLAMADA AGREGADA
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales Incorrectas");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUno(@PathVariable(value = "id") int codigo) {
        UsuarioEntity u = usuarioService.buscarPorId(codigo);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        EntityModel<UsuarioEntity> recurso = EntityModel.of(u,
                linkTo(methodOn(LoginController.class).obtenerUno(codigo)).withSelfRel()
        );
        return ResponseEntity.ok(recurso);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }

    private void registrarIntentoFallido(UsuarioEntity user, String motivo) {
        try {
            AuditoriaEntity audit = new AuditoriaEntity();
            audit.setFechaHora(LocalDateTime.now());
            audit.setOperacion(motivo);
            audit.setMilisegundos(0L);
            audit.setUsuario(user);
            auditoriaRepositorio.save(audit);
        } catch (Exception e) {
            log.error("Error al registrar auditoría de intento fallido: {}", e.getMessage());
        }
    }

}
