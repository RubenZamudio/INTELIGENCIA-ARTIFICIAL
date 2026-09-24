package com.ejemplo;

public final class Utils {
    private Utils() {
    }

    public static void mostrarTablero(String estado) {
        StringBuilder salida = new StringBuilder();

        for (int indice = 0; indice < estado.length(); indice++) {
            if (indice % 3 != 0) {
                salida.append("  ");
            }

            salida.append(estado.charAt(indice));
            if ((indice + 1) % 3 == 0) {
                salida.append('\n');
            }
        }

        System.out.println(salida);
    }
}
