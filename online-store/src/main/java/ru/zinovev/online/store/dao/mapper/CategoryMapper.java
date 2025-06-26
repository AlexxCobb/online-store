package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.model.CategoryDetails;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDetails toCategoryDetails(Category category);
}