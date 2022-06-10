package uz.pdp.bot.service;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.bot.enums.BotState;
import uz.pdp.bot.util.BotConstants;
import uz.pdp.bot.util.BotMenu;
import uz.pdp.model.User;
import uz.pdp.model.*;
import uz.pdp.repository.DataBase;
import uz.pdp.service.ProductService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BotService {

    public static SendMessage start(Update update) {

        registerUser(update);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText(BotConstants.MENU_HEADER);
        sendMessage.setReplyMarkup(getMenuKeyboard());

        return sendMessage;
    }

    public static SendMessage menu(Long chatId) {
        for (User user : DataBase.users) {
            if (user.getChatId().equals(chatId)) {
                user.setBotState(BotState.SHOW_MENU);
                DataBase.writeDataToJsonFile("users");
                break;
            }
        }

        SendMessage sendMessage = new SendMessage();

        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(BotConstants.MENU_HEADER);
        sendMessage.setReplyMarkup(getInlineKeyboardMarkupFromList(DataBase.categories,
                BotConstants.CATEGORY_PREFIX, false));

        return sendMessage;
    }

    public static SendMessage settings(Long chatId) {
        for (User user : DataBase.users) {
            if (user.getChatId().equals(chatId)) {
                user.setBotState(BotState.CHANGE_SETTINGS);
                DataBase.writeDataToJsonFile("users");
                break;
            }
        }

        SendMessage sendMessage = editReplyButtonToLan(String.valueOf(chatId));

        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Choose language: ");

        return sendMessage;
    }

    public static EditMessageText showProducts(Message message, Integer categoryId) {
        for (User user : DataBase.users) {
            if (user.getChatId().equals(message.getChatId())) {
                user.setBotState(BotState.SHOW_PRODUCTS);
                DataBase.writeDataToJsonFile("users");
                break;
            }
        }

        List<Product> productsByCategory = ProductService.getProductsByCategory(categoryId);

        EditMessageText editMessageText = new EditMessageText();

        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setParseMode(ParseMode.MARKDOWN);
        editMessageText.setText(BotConstants.MENU_HEADER);

        InlineKeyboardMarkup inlineKeyboardMarkup =
                getInlineKeyboardMarkupFromList(productsByCategory, BotConstants.PRODUCT_PREFIX
                        , true);
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);

        return editMessageText;
    }


    public static SendPhoto showProductDetail(Long chatId, Integer productId) {

        for (User user : DataBase.users) {
            if (user.getChatId().equals(chatId)) {
                user.setBotState(BotState.SELECT_PRODUCT);
                DataBase.writeDataToJsonFile("users");
                break;
            }
        }


        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));

        for (Product product : DataBase.products) {
            if (product.getId().equals(productId)) {
                sendPhoto.setPhoto(new InputFile(product.getImageUrl()));
                sendPhoto.setCaption("Product name: " + product.getName() + "\n" + "Product price: " + product.getPrice() + " sum \n\n" +
                        "🗃Choose amount of product: ");
                break;
            }
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();


        for (int i = 1, count = 1; i <= 3; i++) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            for (int j = 1; j <= 3; j++) {
                InlineKeyboardButton button = new InlineKeyboardButton(count + "");
                button.setCallbackData(BotConstants.PRODUCT_AMOUNT_PREFIX + BotConstants.SEPARATOR + productId
                        + BotConstants.SEPARATOR + count);

                buttonList.add(button);

                count++;
            }
            inlineKeyboard.add(buttonList);
        }

        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("\uD83D\uDD19");
        button.setCallbackData(BotConstants.PRODUCT_AMOUNT_PREFIX + BotConstants.SEPARATOR + productId
                + BotConstants.SEPARATOR + "0");
        buttonList.add(button);
        inlineKeyboard.add(buttonList);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);

        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);

        return sendPhoto;
    }

    private static void registerUser(Update update) {

        org.telegram.telegrambots.meta.api.objects.User from = update.getMessage().getFrom();
        boolean hasUser = false;

        for (User user : DataBase.users) {
            if (user.getChatId().equals(update.getMessage().getChatId())) {
                hasUser = true;
                user.setId(from.getId());
                user.setChatId(update.getMessage().getChatId());
                user.setUsername(from.getUserName() != null ? from.getUserName() : "");
                user.setFirstName(from.getFirstName() != null ? from.getFirstName() : "");
                user.setLastName(from.getLastName() != null ? from.getLastName() : "");
                user.setPhoneNumber(update.getMessage().getContact() != null ?
                        update.getMessage().getContact().getPhoneNumber() : "");
                break;
            }
        }

        if (!hasUser) {
            User newUser = new User(from.getId(), update.getMessage().getChatId());
            newUser.setUsername(from.getUserName() != null ? from.getUserName() : "");
            newUser.setFirstName(from.getFirstName() != null ? from.getFirstName() : "");
            newUser.setLastName(from.getLastName() != null ? from.getLastName() : "");
            newUser.setPhoneNumber(update.getMessage().getContact() != null ?
                    update.getMessage().getContact().getPhoneNumber() : "");

            DataBase.users.add(newUser);

            Cart newCart = new Cart(newUser.getId());
            DataBase.carts.add(newCart);
        }

        DataBase.writeDataToJsonFile("users");
        DataBase.writeDataToJsonFile("carts");
    }

    private static ReplyKeyboardMarkup getMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(BotMenu.MENU));
        keyboardRowList.add(keyboardRow1);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(BotMenu.CART));
        keyboardRow2.add(new KeyboardButton(BotMenu.SETTINGS));
        keyboardRowList.add(keyboardRow2);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;
    }

    private static InlineKeyboardMarkup getInlineKeyboardMarkupFromList(List list,
                                                                        String prefix,
                                                                        boolean hasBack) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();

        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();

            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            InlineKeyboardButton button = null;
            if (next instanceof Category) {
                button = new InlineKeyboardButton(((Category) next).getPrefix() + ((Category) next).getName());
                button.setCallbackData(prefix + BotConstants.SEPARATOR + ((Category) next).getId());
            } else if (next instanceof Product) {
                button = new InlineKeyboardButton(((Product) next).getName());
                button.setCallbackData(prefix + BotConstants.SEPARATOR + ((Product) next).getId());
            }

            buttonList.add(button);

            if (iterator.hasNext()) {
                next = iterator.next();
                InlineKeyboardButton button1 = null;
                if (next instanceof Category) {
                    button1 =
                            new InlineKeyboardButton(((Category) next).getPrefix() + ((Category) next).getName());
                    button1.setCallbackData(prefix + BotConstants.SEPARATOR + ((Category) next).getId());
                } else if (next instanceof Product) {
                    button1 = new InlineKeyboardButton(((Product) next).getName());
                    button1.setCallbackData(prefix + BotConstants.SEPARATOR + ((Product) next).getId());
                }

                buttonList.add(button1);
            }

            inlineKeyboard.add(buttonList);
        }

        if (hasBack) {
            List<InlineKeyboardButton> buttonList = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton("\uD83D\uDD19");
            button.setCallbackData(BotConstants.PRODUCT_PREFIX + BotConstants.SEPARATOR + "0");
            buttonList.add(button);
            inlineKeyboard.add(buttonList);
        }

        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);

        return inlineKeyboardMarkup;
    }


    public static EditMessageText backToMenu(Message message) {
        for (User user : DataBase.users) {
            if (user.getChatId().equals(message.getChatId())) {
                user.setBotState(BotState.SHOW_MENU);
                DataBase.writeDataToJsonFile("users");
                break;
            }
        }

        EditMessageText editMessageText = new EditMessageText();

        editMessageText.setParseMode(ParseMode.MARKDOWN);
        editMessageText.setChatId(String.valueOf(message.getChatId()));
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setText(BotConstants.MENU_HEADER);
        editMessageText.setReplyMarkup(getInlineKeyboardMarkupFromList(DataBase.categories,
                BotConstants.CATEGORY_PREFIX, false));

        return editMessageText;
    }

    public static SendMessage addProductToCart(Long chatId, Integer productId, Integer amount) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode(ParseMode.MARKDOWN);
        sendMessage.setText("📥Product added to your basket");

        User currentUser = null;
        for (User user : DataBase.users) {
            if (user.getChatId().equals(chatId)) {
                currentUser = user;
                user.setBotState(BotState.SELECT_COUNT_PRODUCT);
                DataBase.writeDataToJsonFile("users");
                break;
            }
        }

        if (currentUser == null) {
            sendMessage.setText("\uD83E\uDD37Not found user");
            return sendMessage;
        }

        Cart userCart = null;

        for (Cart cart : DataBase.carts) {
            if (cart.getUserId().equals(currentUser.getId())) {
                userCart = cart;
            }
        }

        if (userCart == null) {
            sendMessage.setText("\uD83E\uDD37Not found user's basket");
            return sendMessage;
        }

        CartProduct cartProduct = null;

        for (CartProduct cartProduct1 : DataBase.cartProducts) {
            if (cartProduct1.getCartId().equals(userCart.getUserId()) && cartProduct1.getProductId().equals(productId)) {
                cartProduct = cartProduct1;
                cartProduct.setAmount(cartProduct.getAmount() + amount);
                break;
            }
        }

        if (cartProduct == null) {
            cartProduct = new CartProduct(userCart.getUserId(), productId, amount);
            DataBase.cartProducts.add(cartProduct);
        }

        DataBase.writeDataToJsonFile("cartproducts");

        return sendMessage;
    }

    public static SendMessage showCart(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        User currentUser = null;
        for (User user : DataBase.users) {
            if (user.getChatId().equals(chatId)) {
                currentUser = user;
                user.setBotState(BotState.SHOW_CART);
                DataBase.writeDataToJsonFile("users");
                break;
            }
        }

        if(currentUser == null){
            sendMessage.setText("\uD83E\uDD37Not found user😔");
            return sendMessage;
        }

        Cart userCart = null;

        for (Cart cart : DataBase.carts) {
            if (cart.getUserId().equals(currentUser.getId())) {
                userCart = cart;
            }
        }

        if (userCart == null) {
            sendMessage.setText("\uD83E\uDD37User's basket not found😔");
            return sendMessage;
        }

        List<CartProduct> cartProductList = new ArrayList<>();

        for (CartProduct cartProduct : DataBase.cartProducts) {
            if(cartProduct.getCartId().equals(userCart.getUserId())){
                cartProductList.add(cartProduct);
            }
        }

        if(cartProductList.isEmpty()){
            sendMessage.setText("🛒Your card is empty😔");
            return sendMessage;
        }

        String text = "*🛒Your card has:* \n\n";
        Double total = 0d;

        for (int i = 0; i < cartProductList.size(); i++) {
            CartProduct cartProduct = cartProductList.get(i);
            Product product = ProductService.getProductById(cartProduct.getProductId());
            if(product != null){
                text += (i+1)+". *"+product.getName()+ "* x "+cartProduct.getAmount()+ " = "+
                        (product.getPrice()*cartProduct.getAmount())+"\n";
                total += product.getPrice()*cartProduct.getAmount();
            }
        }
        text += "\n*Your total shopping: \t\t"+total+" sum 😉*";
        sendMessage.setText(text);

        sendMessage.setReplyMarkup(getInlineKeyboardForCart(cartProductList));

        return sendMessage;
    }

    private static InlineKeyboardMarkup getInlineKeyboardForCart(List<CartProduct> cartProductList){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineKeyboard = new ArrayList<>();

        Iterator<CartProduct> iterator = cartProductList.iterator();
        while (iterator.hasNext()) {
            CartProduct cartProduct = iterator.next();
            Product product = ProductService.getProductById(cartProduct.getProductId());

            List<InlineKeyboardButton> buttonList = new ArrayList<>();

            if(product != null){
                InlineKeyboardButton button = new InlineKeyboardButton("❌ "+product.getName());
                button.setCallbackData(BotConstants.CART_PRODUCT_DELETE_PREFIX+BotConstants.SEPARATOR+
                        cartProduct.getId());
                buttonList.add(button);
            }

            if (iterator.hasNext()){
                cartProduct = iterator.next();
                product = ProductService.getProductById(cartProduct.getProductId());
                if(product != null){
                    InlineKeyboardButton button = new InlineKeyboardButton("❌ "+product.getName());
                    button.setCallbackData(BotConstants.CART_PRODUCT_DELETE_PREFIX+BotConstants.SEPARATOR+
                            cartProduct.getId());
                    buttonList.add(button);
                }
            }

            inlineKeyboard.add(buttonList);
        }

        List<InlineKeyboardButton> buttonList = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton("\uD83D\uDD19Back to menu");
        button.setCallbackData(BotConstants.TO_MENU);
        buttonList.add(button);
        inlineKeyboard.add(buttonList);

        List<InlineKeyboardButton> buttonList1 = new ArrayList<>();
        // order commit
        InlineKeyboardButton commitButton = new InlineKeyboardButton(BotConstants.ORDER_COMMIT);
        commitButton.setCallbackData(BotConstants.ORDER_COMMIT);
        buttonList1.add(commitButton);
        // order cancel
        InlineKeyboardButton cancelButton = new InlineKeyboardButton(BotConstants.ORDER_CANCEL);
        cancelButton.setCallbackData(BotConstants.ORDER_CANCEL);
        buttonList1.add(cancelButton);

        inlineKeyboard.add(buttonList1);

        inlineKeyboardMarkup.setKeyboard(inlineKeyboard);
        return inlineKeyboardMarkup;
    }

    public static SendMessage orderCancel(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        DataBase.cartProducts.removeIf(cartProduct -> cartProduct.getCartId().equals(chatId));
        DataBase.writeDataToJsonFile("cartproducts");

        sendMessage.setText("🗑Yur card is empty");

        return sendMessage;
    }

    public static SendMessage orderCommit(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        DataBase.cartProducts.removeIf(cartProduct -> cartProduct.getCartId().equals(chatId));
        DataBase.writeDataToJsonFile("cartproducts");

        sendMessage.setText("☎Send your phone number:");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton shareContactButton = new KeyboardButton("☎Send my phone number");
        shareContactButton.setRequestContact(true);

        keyboardRow.add(shareContactButton);
        keyboardRowList.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage gettedContactAskLocation(Long chatId, Contact contact) {

        for (User user : DataBase.users) {
            if(user.getChatId().equals(chatId)){
                user.setFirstName(contact.getFirstName());
                user.setLastName(contact.getLastName());
                user.setPhoneNumber(contact.getPhoneNumber());
                break;
            }
        }

        DataBase.writeDataToJsonFile("users");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode(ParseMode.MARKDOWN);

        sendMessage.setText("📍Send your current location:");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton shareContactButton = new KeyboardButton("📍Send my current location");
        shareContactButton.setRequestLocation(true);

        keyboardRow.add(shareContactButton);
        keyboardRowList.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage gettedLocationAskPhotoDocument(Long chatId, Location location) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode(ParseMode.MARKDOWN);


        String text = "Thanks for use our services😊" +
                "For again shopping choose below";

        sendMessage.setText(text);
        sendMessage.setReplyMarkup(getMenuKeyboard());

        return sendMessage;
    }

    public static SendMessage editReplyButtonToLan(String chaId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Choose Language");
        sendMessage.setChatId(chaId);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();

        replyKeyboardMarkup.setResizeKeyboard(true);

        String[] lans = new String[]{"\uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7FENG", "\uD83C\uDDF7\uD83C\uDDFARU", "\uD83C\uDDFA\uD83C\uDDFFUZ", "\uD83D\uDD19"};
        KeyboardRow keyboardRow = new KeyboardRow();

        for (int i = 0; i < lans.length; i++) {
            KeyboardButton keyboardButton = new KeyboardButton(lans[i]);
            keyboardRow.add(keyboardButton);
            if ((i + 1) % 3 == 0) {
                keyboardRowList.add(keyboardRow);
                keyboardRow = new KeyboardRow();
            }
        }
        if (!keyboardRow.isEmpty())
            keyboardRowList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}
