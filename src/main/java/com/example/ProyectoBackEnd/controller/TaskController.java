package com.example.ProyectoBackEnd.controller;

import com.example.ProyectoBackEnd.dao.entity.TaskEntity;
import com.example.ProyectoBackEnd.service.TaskService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@Slf4j
@RestController
@RequestMapping("/api/tareas")
@CrossOrigin(origins = "http://localhost:3000",exposedHeaders = "X-Usuario-Logueado")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 1. LISTAR POR USUARIO (Derivación del Login)
    @GetMapping("/usuario/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<TaskEntity>> listarPorUsuario(@PathVariable(value = "id") int codigo) {
        log.info("API: Consultando tareas del usuario: {}", codigo);

        List<TaskEntity> lista = taskService.listarTareasPorUsuario(codigo).getContent();

        List<EntityModel<TaskEntity>> tareas = lista.stream()
                .map(t -> EntityModel.of(t,
                linkTo(methodOn(TaskController.class).obtenerUno(t.getIdTarea())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(tareas,
                linkTo(methodOn(TaskController.class).listarPorUsuario(codigo)).withSelfRel());
    }

    // 2. OBTENER UNO
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK) // Agregado para consistencia
    public EntityModel<TaskEntity> obtenerUno(@PathVariable(value = "id") int codigo) {
        TaskEntity tarea = taskService.buscarPorId(codigo);
        return EntityModel.of(tarea,
                linkTo(methodOn(TaskController.class).obtenerUno(codigo)).withSelfRel());
    }

    // 3. INSERTAR (Aquí es donde estaba el error, ahora ya encuentra a listarPaginado)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<TaskEntity> insertar(@RequestBody TaskEntity ce) {
        log.info("API: Insertando nueva tarea");
        taskService.guardarTarea(ce);

        return EntityModel.of(ce,
                linkTo(methodOn(TaskController.class).obtenerUno(ce.getIdTarea())).withSelfRel(),
                // Al existir el método abajo con (int, int), esto ya no marca error
                linkTo(methodOn(TaskController.class).listarPaginado(0, 5)).withRel("lista_completa")
        );
    }

    // 4. MODIFICAR
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<TaskEntity> modificar(@RequestBody TaskEntity ce, @PathVariable(value = "id") int codigo) {
        log.info("API: Modificando tarea codigo: {}", codigo);
        ce.setIdTarea(codigo);
        taskService.guardarTarea(ce);

        return EntityModel.of(ce,
                linkTo(methodOn(TaskController.class).obtenerUno(codigo)).withSelfRel());
    }

    // 5. ELIMINAR
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable(value = "id") int codigo) {
        log.info("API: Eliminando tarea codigo: {}", codigo);
        taskService.eliminarTarea(codigo);
    }

    // 6. LISTAR PAGINADO GENERAL (El método que "salva" al insertar)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<EntityModel<TaskEntity>> listarPaginado(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "5") int tamaño) {

        Page<TaskEntity> tareasPage = taskService.listarPaginado(pagina, tamaño);

        List<EntityModel<TaskEntity>> tareasResources = tareasPage.getContent().stream()
                .map(tarea -> EntityModel.of(tarea,
                linkTo(methodOn(TaskController.class).obtenerUno(tarea.getIdTarea())).withSelfRel()))
                .collect(Collectors.toList());

        return CollectionModel.of(tareasResources,
                linkTo(methodOn(TaskController.class).listarPaginado(pagina, tamaño)).withSelfRel());
    }
}
