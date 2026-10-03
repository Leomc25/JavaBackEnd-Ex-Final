/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ProyectoBackEnd.aspect;

import com.example.ProyectoBackEnd.dao.entity.AuditoriaEntity;
import com.example.ProyectoBackEnd.dao.entity.UsuarioEntity;
import com.example.ProyectoBackEnd.repository.AuditoriaRepositorio;
import com.example.ProyectoBackEnd.repository.UsuarioRepositorio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LogginAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AuditoriaRepositorio auditoriaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Around("execution(* com.example.ProyectoBackEnd.service.*.*(..))")
    public Object medirTiempoYRegistrar(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();
        String nombreMetodo = joinPoint.getSignature().getName();

        // 1. Intentamos capturar el usuario ANTES de que el método termine
        if ("buscarPorNombre".equals(nombreMetodo)
                || "buscarPorId".equals(nombreMetodo)
                || nombreMetodo.toLowerCase().contains("auditoria")) {
            return joinPoint.proceed();
        }
        // 2. Esto ayuda a capturar el usuario en el proceso de Login
        String usuarioDetectado = identificarUsuario(joinPoint);

        try {
            Object resultado = joinPoint.proceed();
            long tiempoTotal = System.currentTimeMillis() - inicio;

            // SI ERA VALIDACIÓN DE LOGIN Y RETORNÓ NULL (FALLÓ), NO REGISTRAR "INICIÓ SESIÓN"
            if ("validarLogin".equals(nombreMetodo) && resultado == null) {
                return null;
            }

            // Si tuvo éxito el login, extraemos el nombre real del usuario logueado
            if (resultado instanceof UsuarioEntity) {
                usuarioDetectado = ((UsuarioEntity) resultado).getUsuario();
            }
            registrarEnBaseDeDatos(nombreMetodo, tiempoTotal, usuarioDetectado);
            
            // 3. SI ERA LOGIN Y TODAVÍA ERA NULL, EXTRAERLO DEL OBJETO RETORNADO
            /*
            if (usuarioDetectado == null && resultado instanceof UsuarioEntity) {
                usuarioDetectado = ((UsuarioEntity) resultado).getUsuario();
            }*/
            // REGISTRO CON TRADUCCIÓN Y USUARIO DETECTADO (REGISTRO FORMAL)

            logger.info("Operación {} completada en {} ms", nombreMetodo, tiempoTotal);
            return resultado;

        } catch (Throwable e) {
            long tiempoTotal = System.currentTimeMillis() - inicio;
            registrarEnBaseDeDatos("ERROR: " + nombreMetodo, tiempoTotal, usuarioDetectado);
            logger.error("Error en {}: {}", nombreMetodo, e.getMessage());
            throw e;
        }
    }

    private String identificarUsuario(ProceedingJoinPoint joinPoint) {
        // Prioridad 1: Header HTTP 'X-Usuario-Logueado' enviado por Axios (api.js) o Sesión HTTP
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String headerUsuario = request.getHeader("X-Usuario-Logueado");
                if (headerUsuario != null && !headerUsuario.trim().isEmpty()) {
                    return headerUsuario.trim();
                }

                HttpSession session = request.getSession(false);
                if (session != null) {
                    UsuarioEntity ue = (UsuarioEntity) session.getAttribute("usuarioLogeado");
                    if (ue == null) {
                        ue = (UsuarioEntity) session.getAttribute("usuarioLogueado");
                    }
                    if (ue != null) {
                        return ue.getUsuario();
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // Prioridad 2: Buscar en argumentos del método (útil para Login cuando aún no hay header)
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UsuarioEntity) {
                return ((UsuarioEntity) arg).getUsuario();
            }
            if (arg instanceof String && !((String) arg).trim().isEmpty()) {
                return (String) arg;
            }
        }

        return null;
    }

    private void registrarEnBaseDeDatos(String metodoOriginal, long ms, String usuarioNombre) {
        try {
            // --- PASO 1: TRADUCTOR DE OPERACIONES ---
            String operacionAmigable;
            switch (metodoOriginal) {
                // --- Autenticación y Cierre ---
                case "loginAccion":
                case "validarLogin":
                    operacionAmigable = "Inició sesión";
                    break;
                case "cerrarSesion":
                case "LogOut":
                    operacionAmigable = "Cerró sesión";
                    break;
                // --- Banners / Módulos del Administrador ---
                case "listarTodos":
                    operacionAmigable = "Ingresó a Gestión de Usuarios";
                    break;
                case "listarEliminados":
                    operacionAmigable = "Ingresó al módulo de Usuarios Eliminados";
                    break;
                // --- Acciones Administrativas sobre Usuarios ---
                case "guardarUsuario":
                    operacionAmigable = "Registró o actualizó un usuario";
                    break;
                case "eliminarUsuario":
                    operacionAmigable = "Desactivó un usuario (Baja lógica)";
                    break;
                case "reactivarUsuario":
                    operacionAmigable = "Reactivó un usuario";
                    break;

                // --- Tareas ---
                case "listarPaginado":
                case "listarPorUsuario":
                    operacionAmigable = "Visualizó el panel de tareas";
                    break;
                case "guardarTarea":
                    operacionAmigable = "Creó o editó una tarea";
                    break;
                case "cambiarEstado":
                    operacionAmigable = "Cambió el estado de una tarea";
                    break;
                case "eliminarTarea":
                    operacionAmigable = "Eliminó una tarea";
                    break;
                default:
                    operacionAmigable = "Ejecutó: " + metodoOriginal;
            }

            AuditoriaEntity audit = new AuditoriaEntity();
            audit.setFechaHora(LocalDateTime.now());
            audit.setOperacion(operacionAmigable);
            audit.setMilisegundos(ms);

            // Vinculación con la entidad Usuario
            // Asignación de usuario real (admin, test, etc.)
            if (usuarioNombre != null && !usuarioNombre.trim().isEmpty()) {
                UsuarioEntity user = usuarioRepositorio.findByUsuario(usuarioNombre);
                if (user != null) {
                    audit.setUsuario(user);
                }
            }

            auditoriaRepositorio.save(audit);
        } catch (Exception e) {
            logger.error("Error al auditar: " + e.getMessage());
        }
    }
}
