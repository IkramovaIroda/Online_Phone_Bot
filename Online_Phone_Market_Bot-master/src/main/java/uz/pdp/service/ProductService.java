package uz.pdp.service;

import uz.pdp.model.Product;
import uz.pdp.repository.DataBase;

import java.util.ArrayList;
import java.util.List;

public class ProductService {

    public static List<Product> getProductsByCategory(Integer categoryId){

        List<Product> productList = new ArrayList<>();

        for (Product product : DataBase.products) {
            if(product.getCategoryId().equals(categoryId)){
                productList.add(product);
            }
        }
        return productList;
    }

    public static Product getProductById(Integer productId){

        for (Product product : DataBase.products) {
            if(product.getId().equals(productId)){
                return product;
            }
        }
        return null;
    }
}
