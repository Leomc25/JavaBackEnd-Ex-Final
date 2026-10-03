package com.example.ProyectoBackEnd.service.impl;

import com.example.ProyectoBackEnd.dao.entity.UsuarioEntity;
import com.example.ProyectoBackEnd.repository.UsuarioRepositorio;
import com.example.ProyectoBackEnd.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UsuarioEntity validarLogin(UsuarioEntity loginRequest) {
        // Estilo Guía: Una sola línea de búsqueda
        UsuarioEntity user = usuarioRepositorio.findByUsuario(loginRequest.getUsuario());

        // Validación directa
        if (user != null && user.getContraseña().equals(loginRequest.getContraseña()) && user.getEstado() == 1) {
            return user;
        }
        return null;
    }

    @Override
    public void eliminarUsuario(Integer id) {
        // Estilo Guía: Usar la @Query personalizada que ya hicimos en el Repo
        // Esto es mucho más eficiente que el código anterior
        usuarioRepositorio.eliminarLogico(id, LocalDateTime.now());
    }

    @Override
    public List<UsuarioEntity> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public void guardarUsuario(UsuarioEntity usuario) {
        if (usuario.getEstado() == null) {
            usuario.setEstado(1);
        }
        usuarioRepositorio.save(usuario);
    }

    @Override
    public UsuarioEntity buscarPorId(Integer id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

    @Override
    public List<UsuarioEntity> listarEliminados() {
        return usuarioRepositorio.findByEstado(0);
    }

    @Override
    public UsuarioEntity buscarPorNombre(String usuario) {
        return usuarioRepositorio.findByUsuario(usuario);
    }

     
}
