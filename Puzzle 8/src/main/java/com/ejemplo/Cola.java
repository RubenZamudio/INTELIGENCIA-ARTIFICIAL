package com.ejemplo;

import java.io.Serializable;

public class Cola<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int capacidad;
    private final T[] elementos;
    private int inicio = -1;
    private int finalCola = -1;
    private T retirado;

    public Cola() {
        this(2_000_000);
    }

    @SuppressWarnings("unchecked")
    public Cola(int capacidad) {
        this.capacidad = capacidad;
        elementos = (T[]) new Object[capacidad];
    }

    public boolean encolar(T dato) {
        if (estaLlena()) {
            return false;
        }

        if (inicio == -1) {
            inicio = 0;
            finalCola = 0;
        } else {
            finalCola++;
        }
        elementos[finalCola] = dato;
        return true;
    }

    public boolean desencolar() {
        if (estaVacia()) {
            return false;
        }

        retirado = elementos[inicio];
        elementos[inicio] = null;
        if (inicio == finalCola) {
            inicio = -1;
            finalCola = -1;
        } else {
            inicio++;
        }
        return true;
    }

    public int getTamanio() {
        if (estaVacia()) {
            return 0;
        }
        return finalCola - inicio + 1;
    }

    public boolean estaLlena() {
        return finalCola == capacidad - 1;
    }

    public boolean estaVacia() {
        return finalCola == -1;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public T getRetirado() {
        return retirado;
    }
}
