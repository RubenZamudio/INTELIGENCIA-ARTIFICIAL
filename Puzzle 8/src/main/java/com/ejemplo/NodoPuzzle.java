package com.ejemplo;

public class NodoPuzzle {
    private final String estado;
    private final NodoPuzzle anterior;
    private final int nivel;
    private final int costoAcumulado;

    public NodoPuzzle(String estado, NodoPuzzle anterior) {
        this.estado = estado;
        this.anterior = anterior;

        if (anterior == null) {
            nivel = 0;
            costoAcumulado = 0;
        } else {
            nivel = anterior.getNivel() + 1;
            costoAcumulado = anterior.getCostoAcumulado() + 1;
        }
    }

    public Lista<NodoPuzzle> crearSucesores() {
        Lista<NodoPuzzle> sucesores = new Lista<>();

        int posicionVacia = estado.indexOf(' ');

        int[][] movimientosPermitidos = {
            {1, 3},
            {0, 2, 4},
            {1, 5},
            {0, 4, 6},
            {1, 3, 5, 7},
            {2, 4, 8},
            {3, 7},
            {4, 6, 8},
            {5, 7}
        };

        for (int destino : movimientosPermitidos[posicionVacia]) {
            String estadoSiguiente = intercambiarFichas(estado, posicionVacia, destino);
            sucesores.agregarAlFinal(new NodoPuzzle(estadoSiguiente, this));
        }

        return sucesores;
    }

    private static String intercambiarFichas(String estado, int origen, int destino) {
        char[] fichas = estado.toCharArray();
        char fichaTemporal = fichas[origen];
        fichas[origen] = fichas[destino];
        fichas[destino] = fichaTemporal;
        return new String(fichas);
    }

    public String getEstado() {
        return estado;
    }

    public NodoPuzzle getAnterior() {
        return anterior;
    }

    public int getNivel() {
        return nivel;
    }

    public int getCostoAcumulado() {
        return costoAcumulado;
    }

    @Override
    public String toString(){
        return estado;
    }
}
