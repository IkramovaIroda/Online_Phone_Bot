package uz.pdp.model;

import lombok.Data;

@Data
public class CartProduct {
    private static Long counter = 0L;

    private Long id;
    private Long cartId;
    private Integer productId;
    private Integer amount;

    public CartProduct(Long cartId, Integer productId, Integer amount) {
        counter++;

        this.id = counter;
        this.cartId = cartId;
        this.productId = productId;
        this.amount = amount;
    }
}
