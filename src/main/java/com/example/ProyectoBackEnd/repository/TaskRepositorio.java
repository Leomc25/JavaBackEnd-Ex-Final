/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.ProyectoBackEnd.repository;

import com.example.ProyectoBackEnd.dao.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@RepositoryRestResource(path = "tareas-rest")
@CrossOrigin(origins = "http://localhost:3000")
public interface TaskRepositorio extends JpaRepository<TaskEntity, Integer>{
    
    @RestResource(path = "porUsuario")
    Page<TaskEntity> findByUsuario_IdUsuario(@Param("id") Integer idUsuario, Pageable pageable); 
}
