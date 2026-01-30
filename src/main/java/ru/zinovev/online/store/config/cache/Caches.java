package ru.zinovev.online.store.config.cache;

public final class Caches {
    private Caches() {
    }

    public static final class Product {
        private Product() {
        }

        public static final String STAND = "productStand";
        public static final String PRICES = "productPrices";
        public static final String PARAMS = "productParams";
    }

    public static final class Category {
        private Category() {
        }

        public static final String ALL = "categories";
        public static final String BY_ID = "category";
    }

    public static final class Address {
        private Address() {
        }

        public static final String BY_ID = "address";
        public static final String BY_SYSTEM_TYPE = "addressBySystemType";
        public static final String ALL = "systemAddresses";
    }

    public static final class Role{
        private Role(){
        }

        public static final String BY_NAME = "role";
    }
}
