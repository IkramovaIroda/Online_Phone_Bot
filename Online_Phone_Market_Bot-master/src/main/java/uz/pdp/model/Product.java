package uz.pdp.model;

import lombok.Data;

import static uz.pdp.bot.util.BotConstants.DEFAULT_IMAGE;

@Data
public class Product {

    private static Integer counter = 0;

    private Integer id;
    private Integer categoryId;
    private String name;
    private Double price;
    private String imageUrl = DEFAULT_IMAGE;

    public Product(String name, Double price, Integer categoryId, String imageUrl) {
        counter++;

        this.id = counter;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
