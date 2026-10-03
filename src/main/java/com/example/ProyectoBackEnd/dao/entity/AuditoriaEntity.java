/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ProyectoBackEnd.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*; // Cambiado de javax a jakarta para Spring Boot 3
import java.time.LocalDateTime;
import lombok.Data;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="auditoria")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditoriaEntity extends RepresentationModel<AuditoriaEntity>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idauditoria;
    
    @Column(name = "fechahora")
    private LocalDateTime fechaHora;
    
    @ManyToOne
    @JoinColumn(name = "id_usuarios")
    private UsuarioEntity usuario;
    
    private String operacion;
    
    @Column(name = "milisegundos")
    private Long milisegundos;
    
    @com.fasterxml.jackson.annotation.JsonProperty("usuarioNombre")
    public String getUsuarioNombrePlano() {
        if (this.usuario != null) {
            return this.usuario.getUsuario();
        }
        return null;
    }
  
}
