package me.nolanjames.financemanagerapi.shared;

public interface BaseService<T, V> {

  T create(V response);
}
