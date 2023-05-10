package com.mitocode.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Document(collection = "dishes")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dish {

    @Id
    @EqualsAndHashCode.Include
    private String id; //ObjectId | BSON Binary JSON

    @Size(min = 3)
    @Field //(name = "namex") //ES OPCIONAL
    private String name;

    @Min(value = 1)
    @Field
    private Double price;

    @NotNull
    @Field
    private Boolean status;
}
