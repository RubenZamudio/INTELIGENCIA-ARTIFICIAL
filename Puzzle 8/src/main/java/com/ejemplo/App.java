package com.ejemplo;

public final class App {
    private static final String ESTADO_INICIAL = "12534 678";
    private static final String ESTADO_OBJETIVO = "12345678 ";

    private App() {
    }

    public static void main(String[] args) {
        Utils.mostrarTablero(ESTADO_INICIAL);

        PuzzleTree buscador = new PuzzleTree(ESTADO_INICIAL, ESTADO_OBJETIVO);
        buscador.buscarEnAnchura();
        buscador.buscarEnProfundidad();
        buscador.buscarPorCostoUniforme();
        buscador.buscarConProfundidadIterativa();
    }
}
