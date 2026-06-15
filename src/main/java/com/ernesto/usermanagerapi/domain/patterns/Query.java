package com.ernesto.usermanagerapi.domain.patterns;

import java.util.List;

import lombok.Getter;

@Getter
public class Query<TItem> {

    private List<TItem> items;

    private Query(List<TItem> items) {
        this.items = items;
    }

    public static <TQuery> Query<TQuery> newQuery(List<TQuery> itemList) {
        return new Query<TQuery>(itemList);
    }
}
