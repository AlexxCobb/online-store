package ru.zinovev.online.store.dao.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.dao.entity.Product;
import ru.zinovev.online.store.dao.entity.ProductParameter;

import java.math.BigDecimal;
import java.util.List;

public class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> hasBrand(List<String> brand) {
        return hasParameter("brand", brand);
    }

    public static Specification<Product> hasColor(List<String> color) {
        return hasParameter("color", color);
    }

    public static Specification<Product> hasRam(List<Integer> ram) {
        return hasParameter("ram", ram.stream().map(String::valueOf).toList());
    }

    public static Specification<Product> hasMemory(List<Integer> memory) {
        return hasParameter("memory", memory.stream().map(String::valueOf).toList());
    }

    public static Specification<Product> hasCategories(List<String> publicCategoryIds) {
        return (root, query, criteriaBuilder) -> {
            Join<Product, Category> categoryJoin = root.join("category");
            return categoryJoin.get("publicCategoryId").in(publicCategoryIds);
        };
    }

    public static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        if (maxPrice == null) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"),
                                                                                           minPrice));
        }
        return ((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("price"), minPrice, maxPrice));
    }

    private static Specification<Product> hasParameter(String key, List<String> values) {
        return (root, query, cb) -> {
            if (values.isEmpty()) {
                return null;
            }
            Subquery<Long> sq = query.subquery(Long.class);
            Root<Product> subRoot = sq.from(Product.class); // correlate
            Join<Product, ProductParameter> parametersJoin = subRoot.join("parameters");
            sq.select(subRoot.get("id"))
                    .where(cb.and(
                            cb.equal(parametersJoin.get("key"), key), parametersJoin.get("value").in(values),
                            cb.equal(root.get("id"), subRoot.get("id"))
                    ));
            return cb.exists(sq); // разобрать
        };
    }
}
