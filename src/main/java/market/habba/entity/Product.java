package market.habba.entity;

import java.util.List;

/**
 * Информация о продукте.
 */
public class Product {

    /**
     * Название товара.
     */
    private String name;

    /**
     * Цена товара.
     */
    private String price;

    /**
     * Путь к изображениям товара.
     */
    private List<Image> images;

    /**
     * Краткое описание товара.
     */
    private String description;

    /**
     * Полное описание товара.
     */
    private String fullDescription;

    /**
     * true - имеется в продаже, false - не продается.
     */
    private boolean isActive;

    /**
     * Продавец товара.
     */
    private Owner owner;


}
