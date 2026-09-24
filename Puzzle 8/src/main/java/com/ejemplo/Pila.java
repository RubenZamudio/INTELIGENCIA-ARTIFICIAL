package com.ejemplo;

public class Pila<T> {
    private final T[] elementos;
    private final int capacidad;
    private int cima = -1;
    private T retirado;
    private String mensaje;

    public Pila() {
        this(2_000_000);
    }

    @SuppressWarnings("unchecked")
    public Pila(int capacidad) {
        this.capacidad = capacidad;
        elementos = (T[]) new Object[capacidad];
    }

    public boolean apilar(T dato) {
        if (estaLlena()) {
            mensaje = "Pila llena (verflow)";
            return false;
        }

        elementos[++cima] = dato;
        mensaje = "Inserción pexitosa";
        return true;
    }

    public boolean desapilar() {
        if (estaVacia()) {
            mensaje = "Pila vacia (underflow)";
            return false;
        }

        retirado = elementos[cima];
        elementos[cima--] = null;
        mensaje = "Retiro exitoso";
        return true;
    }

    public boolean estaLlena() {
        return cima == capacidad - 1;
    }

    public int getTamanio() {
        return cima + 1;
    }

    public boolean estaVacia() {
        return cima == -1;
    }

    public T getRetirado() {
        return retirado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getCapacidad() {
        return capacidad;
    }
}
