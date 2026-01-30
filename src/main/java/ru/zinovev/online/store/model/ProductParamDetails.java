package ru.zinovev.online.store.model;

import java.util.List;

public record ProductParamDetails(
        List<String> brand,
        List<String> color,
        List<Integer> ram,
        List<Integer> memory
) {
}
