package com.sanedge.category.entity;

import com.sanedge.common.entity.BaseModel;

import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@MongoEntity(collection = "categories")
public class Category extends BaseModel {

    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    @Size(max = 100)
    private String slugCategory;

    private String imageCategory;
}
