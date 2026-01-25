package com.example.Horacije.administration.sharedTools.helpers;

import java.util.ArrayList;
import java.util.List;

public class Helpers {

    public static <T>List<T> listConverter(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
