/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ProyectoBackEnd.service;

import com.example.ProyectoBackEnd.dao.entity.TaskEntity;
import java.util.List;
import org.springframework.data.domain.Page;

 
public interface TaskService {
    Page<TaskEntity> listarTareasPorUsuario(Integer usuarioId);
    void guardarTarea(TaskEntity tarea);
    void eliminarTarea(int id);
    
    TaskEntity buscarPorId(int id);
    
    Page<TaskEntity> listarTareasPaginadas(Integer usuarioId, int pagina, int tamaño);
    
    Page<TaskEntity> listarPaginado(int pagina, int tamaño);
    
}
