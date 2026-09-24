package com.ejemplo;

public class Lista<T> {
    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private T ultimoDato;
    private int tamanio;

    public boolean agregarAlInicio(T dato) {
        Nodo<T> nuevo = crearNodo(dato);
        if (nuevo == null) {
            return false;
        }

        tamanio++;
        if (primero == null) {
            primero = nuevo;
            ultimo = nuevo;
            return true;
        }

        nuevo.setSiguiente(primero);
        primero = nuevo;
        return true;
    }

    public boolean agregarAlFinal(T dato) {
        Nodo<T> nuevo = crearNodo(dato);
        if (nuevo == null) {
            return false;
        }

        tamanio++;
        if (primero == null) {
            primero = nuevo;
            ultimo = nuevo;
            return true;
        }

        ultimo.setSiguiente(nuevo);
        ultimo = nuevo;
        return true;
    }

    public boolean agregarOrdenado(T dato) {
        if (dato == null) {
            return false;
        }

        if (primero == null || dato.toString().compareTo(primero.getValor().toString()) <= 0) {
            return agregarAlInicio(dato);
        }
        if (dato.toString().compareTo(ultimo.getValor().toString()) >= 0) {
            return agregarAlFinal(dato);
        }

        Nodo<T> nuevo = crearNodo(dato);
        if (nuevo == null) {
            return false;
        }

        tamanio++;
        Nodo<T> anterior = null;
        Nodo<T> actual = primero;
        String representacion = dato.toString();
        while (actual != null) {
            if (actual.getValor().toString().compareTo(representacion) > 0) {
                break;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        anterior.setSiguiente(nuevo);
        nuevo.setSiguiente(actual);
        return true;
    }

    public boolean eliminar(int posicion) {
        if (posicion < 1 || posicion > tamanio) {
            return false;
        }

        tamanio--;
        if (primero == ultimo) {
            ultimoDato = primero.getValor();
            primero = null;
            ultimo = null;
            return true;
        }

        Nodo<T> anterior = null;
        Nodo<T> actual = primero;
        for (int indice = 1; indice < posicion; indice++) {
            anterior = actual;
            actual = actual.getSiguiente();
        }

        if (anterior == null) {
            ultimoDato = primero.getValor();
            primero = primero.getSiguiente();
            return true;
        }
        if (actual == ultimo) {
            ultimoDato = ultimo.getValor();
            anterior.setSiguiente(null);
            ultimo = anterior;
            return true;
        }

        ultimoDato = actual.getValor();
        anterior.setSiguiente(actual.getSiguiente());
        return true;
    }

    public boolean eliminar(T dato) {
        if (dato == null) {
            return false;
        }

        String representacion = dato.toString();
        Nodo<T> actual = primero;
        int posicion = 0;
        while (actual != null) {
            posicion++;
            if (actual.getValor().toString().equalsIgnoreCase(representacion)) {
                return eliminar(posicion);
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean buscar(T dato) {
        if (dato == null) {
            return false;
        }

        String representacion = dato.toString();
        Nodo<T> actual = primero;
        while (actual != null) {
            if (actual.getValor().toString().equalsIgnoreCase(representacion)) {
                ultimoDato = actual.getValor();
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    private Nodo<T> crearNodo(T dato) {
        try {
            return new Nodo<>(dato);
        } catch (OutOfMemoryError error) {
            return null;
        }
    }

    public int getTamanio() {
        return tamanio;
    }

    public Nodo<T> getPrimero() {
        return primero;
    }

    public Nodo<T> getUltimo() {
        return ultimo;
    }

    public T getUltimoDato() {
        return ultimoDato;
    }
}
