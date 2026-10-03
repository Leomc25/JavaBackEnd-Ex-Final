/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ProyectoBackEnd.service;

import com.example.ProyectoBackEnd.dao.entity.UsuarioEntity;
import java.util.List;

public interface UsuarioService {
    
    UsuarioEntity validarLogin(UsuarioEntity usuario);
    
    List<UsuarioEntity> listarTodos();
    void guardarUsuario(UsuarioEntity usuario);
    UsuarioEntity buscarPorId(Integer id);
    void eliminarUsuario(Integer id);
    UsuarioEntity buscarPorNombre(String usuario);
    List<UsuarioEntity> listarEliminados();
    
}
