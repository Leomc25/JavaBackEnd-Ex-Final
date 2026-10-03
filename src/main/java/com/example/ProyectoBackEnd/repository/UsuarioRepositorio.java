/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.ProyectoBackEnd.repository;

import com.example.ProyectoBackEnd.dao.entity.UsuarioEntity; // Import correcto
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin(origins = "http://localhost:3000")
@RepositoryRestResource(path = "usuarios-rest")

public interface UsuarioRepositorio extends JpaRepository<UsuarioEntity, Integer> {

    
    UsuarioEntity findByUsuario(@Param("usuario") String usuario);//   para   login

    List<UsuarioEntity> findByEstado(@Param("estado") Integer estado);

    @Modifying
    @Transactional
    @Query("UPDATE UsuarioEntity u SET u.estado = 0, u.fechaEliminacion = :fecha WHERE u.idUsuario = :id")
    void eliminarLogico(@Param("id") Integer id, @Param("fecha") LocalDateTime fecha);
}