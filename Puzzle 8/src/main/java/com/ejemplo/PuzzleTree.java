package com.ejemplo;

import java.util.HashSet;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;

public class PuzzleTree {
    private static final int CAPACIDAD_FRONTERA = 2_000_000;
    private static final int LIMITE_PROFUNDIDAD = 50;

    private final String estadoObjetivo;
    private final NodoPuzzle raiz;

    public PuzzleTree(String estadoInicial, String estadoObjetivo) {
        this.estadoObjetivo = estadoObjetivo;
        raiz = new NodoPuzzle(estadoInicial, null);
    }

    public void buscarEnAnchura() {
        long inicio = System.currentTimeMillis();
        int evaluados = 0;
        int generados = 1;
        int memoriaMaxima = 0;

        Set<String> visitados = new HashSet<>();
        Cola<NodoPuzzle> frontera = new Cola<>(CAPACIDAD_FRONTERA);
        NodoPuzzle actual = raiz;
        visitados.add(actual.getEstado());
        frontera.encolar(actual);

        while (!frontera.estaVacia() && frontera.desencolar()) {
            evaluados++;
            actual = frontera.getRetirado();

            int memoriaActual = visitados.size() + frontera.getTamanio();
            memoriaMaxima = Math.max(memoriaMaxima, memoriaActual);

            if (actual.getEstado().compareTo(estadoObjetivo) == 0) {
                mostrarResumen("Búsqueda en anchura (BFS)", actual.getNivel(), evaluados, generados, memoriaMaxima, inicio);
                break;
            }

            Lista<NodoPuzzle> sucesores = actual.crearSucesores();
            Nodo<NodoPuzzle> cursor = sucesores.getPrimero();
            while (cursor != null) {
                String estadoHijo = cursor.getValor().getEstado();
                if (!visitados.contains(estadoHijo)) {
                    visitados.add(estadoHijo);
                    frontera.encolar(cursor.getValor());
                    generados++;
                }
                cursor = cursor.getSiguiente();
            }
        }
    }

    public void buscarEnProfundidad() {
        long inicio = System.currentTimeMillis();
        int evaluados = 0;
        int generados = 1;
        int memoriaMaxima = 0;

        Set<String> visitados = new HashSet<>();
        Pila<NodoPuzzle> frontera = new Pila<>(CAPACIDAD_FRONTERA);
        NodoPuzzle actual = raiz;
        visitados.add(actual.getEstado());
        frontera.apilar(actual);

        while (!frontera.estaVacia() && frontera.desapilar()) {
            evaluados++;
            actual = frontera.getRetirado();

            int memoriaActual = visitados.size() + frontera.getTamanio();
            memoriaMaxima = Math.max(memoriaMaxima, memoriaActual);

            if (actual.getEstado().compareTo(estadoObjetivo) == 0) {
                mostrarResumen("Búsqueda en profundidad (DFS)", actual.getNivel(), evaluados, generados, memoriaMaxima, inicio);
                break;
            }

            Lista<NodoPuzzle> sucesores = actual.crearSucesores();
            Nodo<NodoPuzzle> cursor = sucesores.getPrimero();
            while (cursor != null) {
                String estadoHijo = cursor.getValor().getEstado();
                if (!visitados.contains(estadoHijo)) {
                    frontera.apilar(cursor.getValor());
                    visitados.add(estadoHijo);
                    generados++;
                }
                cursor = cursor.getSiguiente();
            }
        }
    }

    public void buscarPorCostoUniforme() {
        long inicio = System.currentTimeMillis();
        int evaluados = 0;
        int generados = 1;
        int memoriaMaxima = 0;

        Set<String> visitados = new HashSet<>();
        PriorityQueue<NodoPuzzle> frontera = new PriorityQueue<>(Comparator.comparingInt(NodoPuzzle::getCostoAcumulado));
        NodoPuzzle actual = raiz;
        visitados.add(actual.getEstado());
        frontera.add(actual);

        while (!frontera.isEmpty()) {
            actual = frontera.poll();
            evaluados++;

            int memoriaActual = visitados.size() + frontera.size();
            memoriaMaxima = Math.max(memoriaMaxima, memoriaActual);

            if (actual.getEstado().compareTo(estadoObjetivo) == 0) {
                mostrarResumen("Búsqueda de costo uniforme (UCS)", actual.getNivel(), evaluados, generados, memoriaMaxima, inicio);
                System.out.println("Costo de la solución: " + actual.getCostoAcumulado());
                break;
            }

            Lista<NodoPuzzle> sucesores = actual.crearSucesores();
            Nodo<NodoPuzzle> cursor = sucesores.getPrimero();
            while (cursor != null) {
                NodoPuzzle hijo = cursor.getValor();
                if (!visitados.contains(hijo.getEstado())) {
                    visitados.add(hijo.getEstado());
                    frontera.add(hijo);
                    generados++;
                }
                cursor = cursor.getSiguiente();
            }
        }
    }

    public void buscarConProfundidadIterativa() {
        long inicio = System.currentTimeMillis();
        int evaluadosTotales = 0;
        int generadosTotales = 0;
        int memoriaMaxima = 0;
        boolean encontrado = false;

        for (int limiteActual = 0; limiteActual <= LIMITE_PROFUNDIDAD; limiteActual++) {
            Set<String> visitados = new HashSet<>();
            Pila<NodoPuzzle> frontera = new Pila<>(CAPACIDAD_FRONTERA);
            NodoPuzzle actual = raiz;
            visitados.add(actual.getEstado());
            frontera.apilar(actual);
            generadosTotales++;

            while (!frontera.estaVacia() && frontera.desapilar()) {
                evaluadosTotales++;
                actual = frontera.getRetirado();

                int memoriaActual = visitados.size() + frontera.getTamanio();
                memoriaMaxima = Math.max(memoriaMaxima, memoriaActual);

                if (actual.getEstado().compareTo(estadoObjetivo) == 0) {
                    mostrarResumen("Búsqueda iterativa limitada (IDS)", actual.getNivel(), evaluadosTotales, generadosTotales, memoriaMaxima, inicio);
                    System.out.println("Límite de profundidad donde se encontró: " + limiteActual);
                    encontrado = true;
                    break;
                }

                if (actual.getNivel() < limiteActual) {
                    Lista<NodoPuzzle> sucesores = actual.crearSucesores();
                    Nodo<NodoPuzzle> cursor = sucesores.getPrimero();
                    while (cursor != null) {
                        String estadoHijo = cursor.getValor().getEstado();
                        if (!visitados.contains(estadoHijo)) {
                            visitados.add(estadoHijo);
                            frontera.apilar(cursor.getValor());
                            generadosTotales++;
                        }
                        cursor = cursor.getSiguiente();
                    }
                }
            }

            if (encontrado) {
                break;
            }
        }

        if (!encontrado) {
            System.out.println("No se encontró la meta dentro del límite de profundidad: " + LIMITE_PROFUNDIDAD);
        }
    }

    private void mostrarResumen(String algoritmo, int profundidad, int evaluados, int generados, int memoriaMaxima, long inicio) {
        double segundos = (System.currentTimeMillis() - inicio) / 1000.0;

        System.out.println();
        System.out.println(algoritmo);
        System.out.println("Profundidad de la meta: " + profundidad);
        System.out.println("Nodos evaluados: " + evaluados);
        System.out.println("Nodos generados: " + generados);
        System.out.println("Máximo de nodos en memoria: " + memoriaMaxima);
        System.out.println("Tiempo real: " + segundos + " segundos");
    }

    private void mostrarCamino(NodoPuzzle nodoFinal) {
        Pila<NodoPuzzle> camino = new Pila<>(CAPACIDAD_FRONTERA);
        NodoPuzzle actual = nodoFinal;

        while (actual != null) {
            camino.apilar(actual);
            actual = actual.getAnterior();
        }

        while (!camino.estaVacia() && camino.desapilar()) {
            Utils.mostrarTablero(camino.getRetirado().getEstado());
        }
    }
}
