package uz.pdp.service;

import uz.pdp.model.CartProduct;
import uz.pdp.repository.DataBase;

public class CartProductService {
    public static void deleteCartProductById(Long cartProductId){
        CartProduct cartProduct = null;

        for (CartProduct cartProduct1 : DataBase.cartProducts) {
            if(cartProduct1.getId().equals(cartProductId)){
                cartProduct = cartProduct1;
                break;
            }
        }

        if(cartProduct != null){
            DataBase.cartProducts.remove(cartProduct);
            DataBase.writeDataToJsonFile("cartproducts");
        }
    }
}
