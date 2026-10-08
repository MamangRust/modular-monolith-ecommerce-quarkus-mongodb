package com.sanedge.product.entity;

import org.bson.types.ObjectId;

import com.sanedge.common.entity.BaseModel;

import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@MongoEntity(collection = "products")
public class Product extends BaseModel {

    private ObjectId merchantId;

    private ObjectId categoryId;

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @NotNull
    private Integer price;

    @NotNull
    private Integer countInStock;

    private String brand;

    private Integer weight;

    private Float rating;

    @Size(max = 100)
    private String slugProduct;

    private String imageProduct;
}
