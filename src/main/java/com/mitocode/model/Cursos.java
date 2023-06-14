package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "cursos")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cursos {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    
    @NotNull
    @Size(min = 5, max = 50)
    @Field
    private String nombre;
    
    @NotNull
    @Size(min = 2, max = 5)
    @Field
    private String siglas;

    @NotNull
    @Field
    private Boolean estado;
    
}
