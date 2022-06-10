package uz.pdp.model;

import lombok.Data;

@Data
public class Category {

    private static Integer counter = 0;

    private Integer id;
    private String prefix;
    private String name;

    public Category(String prefix, String name) {
        counter++;
        this.id = counter;
        this.prefix = prefix;
        this.name = name;
    }
}
