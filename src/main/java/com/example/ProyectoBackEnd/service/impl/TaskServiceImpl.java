package com.example.ProyectoBackEnd.service.impl;  

import com.example.ProyectoBackEnd.dao.entity.TaskEntity;
import com.example.ProyectoBackEnd.repository.TaskRepositorio;
import com.example.ProyectoBackEnd.service.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepositorio tareaRepositorio;  

    @Override
    public void guardarTarea(TaskEntity tarea) {
        tareaRepositorio.save(tarea);
    }

    @Transactional
    @Override
    public void eliminarTarea(int id) {
        tareaRepositorio.deleteById(id);
    }

    @Override
    public TaskEntity buscarPorId(int id) {
        return tareaRepositorio.findById(id).orElse(null);
    }

    @Override
    public Page<TaskEntity> listarTareasPaginadas(Integer usuarioId, int pagina, int tamaño) {
        // Ordenamos por idTarea de forma descendente (las más nuevas primero)
        Pageable pageable = PageRequest.of(pagina, tamaño, Sort.by("idTarea").descending());
        return tareaRepositorio.findByUsuario_IdUsuario(usuarioId, pageable);
    }

    @Override
    public Page<TaskEntity> listarPaginado(int pagina, int tamaño) {
        return tareaRepositorio.findAll(PageRequest.of(pagina, tamaño, Sort.by("idTarea").descending()));
    }

    @Override
    public Page<TaskEntity> listarTareasPorUsuario(Integer usuarioId) {
        // Usamos un tamaño grande por defecto si no se especifica paginación
        Pageable pageable = PageRequest.of(0, 100, Sort.by("idTarea").descending());
        return tareaRepositorio.findByUsuario_IdUsuario(usuarioId, pageable);
    }
}