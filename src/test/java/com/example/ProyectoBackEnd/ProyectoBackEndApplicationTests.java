package com.example.ProyectoBackEnd;

import com.example.ProyectoBackEnd.dao.entity.UsuarioEntity;
import com.example.ProyectoBackEnd.repository.UsuarioRepositorio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class ProyectoBackEndApplicationTests {
/*
    @Autowired
    private UsuarioRepositorio urepo;

    @Autowired
    private BCryptPasswordEncoder codificador;*/

    @Test
    void contextLoads() {
        //BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        /*
            String claveAdmin = "admin123";
        String claveUser = "12345";
        
        System.out.println("----------------------------------------------");
        System.out.println("ADMIN HASH: " + encoder.encode(claveAdmin));
        System.out.println("USER HASH: " + encoder.encode(claveUser));
        System.out.println("----------------------------------------------");
         */
 /*
        ----------------------------------------------
        Esto impacto en el Output
ADMIN HASH: $2a$10$nWYZIxUFuJ8xzZyqQhvgju3tYcnIbbjKn3KDZuUoK9jCLSlnL8lm2
USER HASH: $2a$10$kNe/Ocg488RK67r3LDSGpenNSzPCL.W.XiFd92HF3vKbsVLH9okei
----------------------------------------------
         */
 /*
 UsuarioEntity ue = new UsuarioEntity();
        ue.setUsuario("userNuevo");
        // Ajusta los campos según tu entidad (pueden ser nombre, apellido, etc.)
        ue.setEstado(1);
        ue.setRol("USER"); // No olvides el rol para que funcione la seguridad

        // Aquí ocurre la encriptación
        ue.setContraseña(codificador.encode("12345"));

        // Aquí impacta en MySQL
        UsuarioEntity a = urepo.save(ue);

        // Verificación
        Assertions.assertNotNull(a);
        System.out.println("Usuario insertado correctamente con clave encriptada");
         */
    }

}
