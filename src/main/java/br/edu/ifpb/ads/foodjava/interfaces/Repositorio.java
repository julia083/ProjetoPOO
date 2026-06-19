package br.edu.ifpb.ads.foodjava.interfaces;

import java.util.List;

public interface Repositorio<T> {

    List<T> listarTodos();

    void salvarTodos(List<T> lista);
}
