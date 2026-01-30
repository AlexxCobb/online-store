package ru.zinovev.online.store.dao.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.zinovev.online.store.controller.dto.CategoryDto;
import ru.zinovev.online.store.dao.entity.Category;
import ru.zinovev.online.store.model.CategoryDetails;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDetails toCategoryDetails(Category category);

    @Mapping(target = "publicCategoryId", ignore = true)
    CategoryDetails toCategoryDetails(CategoryDto categoryDto);
}