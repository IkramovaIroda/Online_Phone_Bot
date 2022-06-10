package uz.pdp.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import uz.pdp.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBase {

    public static HashMap<String, List> map = null;

    public static List<User> users = new ArrayList<>();

    public static List<Category> categories = new ArrayList<>();
    public static List<Product> products = new ArrayList<>();

    public static List<Cart> carts = new ArrayList<>();
    public static List<CartProduct> cartProducts = new ArrayList<>();

    public static void readDataFromJsonFile() {

        String path = "src/main/resources";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path, "users.json")))) {
            users = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(new File(path, "categories.json")))) {
            categories = gson.fromJson(reader, new TypeToken<List<Category>>() {}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(new File(path, "products.json")))) {
            products = gson.fromJson(reader, new TypeToken<List<Product>>() {}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(new File(path, "carts.json")))) {
            carts = gson.fromJson(reader, new TypeToken<List<Cart>>() {}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(new File(path, "cartproducts.json")))) {
            cartProducts = gson.fromJson(reader, new TypeToken<List<CartProduct>>() {}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        map = new HashMap<>();
        map.put("categories", DataBase.categories);
        map.put("products", DataBase.products);
        map.put("users", DataBase.users);
        map.put("carts", DataBase.carts);
        map.put("cartproducts", DataBase.cartProducts);

    }

    public static void writeDataToJsonFile(String objStr){
        String path = "src/main/resources";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(path, objStr+".json")))) {
            out.write(gson.toJson(map.get(objStr)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
