package uz.pdp.model;

import lombok.Data;

@Data
public class Cart {

    private Long userId;

    public Cart(Long userId) {
        this.userId = userId;
    }
}
