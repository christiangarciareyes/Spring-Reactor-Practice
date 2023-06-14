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
@Document(collection = "estudiantes")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Estudiantes {
	
    @Id
    @EqualsAndHashCode.Include
    private String id;

    @NotNull
    @Size(min = 2, max = 30)
    @Field
    private String nombres;

    @NotNull
    @Size(min = 2, max = 30)
    @Field
    private String apellidos;

    @NotNull
    @Size(min = 8, max = 8)
    @Field
    private String dni;
    
    @NotNull
    @Size(min = 2, max = 2)
    @Field
    private String edad; 
    //Se consideró de tipo String debido a que al ser de tipo int causaba que al listar las matriculas apareciera tammbien la edad y 
    //solo se deberia mostar el ID. Esto no afecta el ordenamiento asc y/o desc.

}
