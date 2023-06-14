package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "matricula")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Matricula {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    
    @NotNull
    @Field
    private LocalDate fechaMatricula;
    
    @NotNull
    @Field
    private Estudiantes estudiante;

    @NotNull
    @Field
    private List<MatriculaDetalle> items;
    
    @NotNull
    @Field
    private Boolean estado;
    
}
