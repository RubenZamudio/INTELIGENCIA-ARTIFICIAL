import os
import tkinter as tk
from tkinter import ttk

import numpy as np
import tensorflow as tf
from PIL import Image, ImageDraw
from tensorflow.keras.layers import Dense, Flatten, Input
from tensorflow.keras.models import Sequential, load_model

RUTA_MODELO = "modelo_mlp.keras"
TAMANO_CANVAS = 280   
GROSOR_PINCEL = 20   

def crear_y_entrenar_modelo():
    (x_train, y_train), (x_test, y_test) = tf.keras.datasets.mnist.load_data()

    x_train = x_train.astype("float32") / 255
    x_test = x_test.astype("float32") / 255

    modelo = Sequential([
        Input(shape=(28, 28)),
        Flatten(),
        Dense(256, activation="sigmoid"),
        Dense(128, activation="sigmoid"),
        Dense(10, activation="softmax"),
    ])

    modelo.compile(optimizer="adam",
                   loss="sparse_categorical_crossentropy",
                   metrics=["accuracy"])

    modelo.fit(x_train, y_train, epochs=10, batch_size=128,
               validation_split=0.2)

    perdida, precision = modelo.evaluate(x_test, y_test, verbose=0)
    print(f"Test loss: {perdida:.4f} | Test accuracy: {precision:.4f}")

    modelo.save(RUTA_MODELO)
    return modelo


def obtener_modelo():
    if os.path.exists(RUTA_MODELO):
        print("Cargando modelo guardado...")
        return load_model(RUTA_MODELO)
    print("No hay modelo guardado, entrenando (tarda un poco)...")
    return crear_y_entrenar_modelo()


def preparar_imagen(imagen_pil):
    # Recortamos al rectángulo que contiene el trazo
    caja = imagen_pil.getbbox()
    if caja is None:
        return None
    digito = imagen_pil.crop(caja)

    # Escalamos para que el lado más grande mida 20 px, manteniendo proporción
    ancho, alto = digito.size
    escala = 20.0 / max(ancho, alto)
    nuevo_ancho = max(1, int(round(ancho * escala)))
    nuevo_alto = max(1, int(round(alto * escala)))
    digito = digito.resize((nuevo_ancho, nuevo_alto), Image.LANCZOS)

    # Lo pegamos en el centro de un lienzo de 28x28
    lienzo = Image.new("L", (28, 28), 0)
    pos_x = (28 - nuevo_ancho) // 2
    pos_y = (28 - nuevo_alto) // 2
    lienzo.paste(digito, (pos_x, pos_y))

    arreglo = np.array(lienzo, dtype="float32") / 255.0

    # Movemos el dígito para que su centro de masa quede en el centro (14, 14)
    ys, xs = np.indices(arreglo.shape)
    masa_total = arreglo.sum()
    centro_y = (ys * arreglo).sum() / masa_total
    centro_x = (xs * arreglo).sum() / masa_total
    desplazamiento_y = int(round(14 - centro_y))
    desplazamiento_x = int(round(14 - centro_x))
    arreglo = np.roll(arreglo, (desplazamiento_y, desplazamiento_x), axis=(0, 1))

    return arreglo


class AplicacionDigitos:
    def __init__(self, ventana, modelo):
        self.ventana = ventana
        self.modelo = modelo
        self.ventana.title("Reconocedor de dígitos - MLP")
        self.ventana.resizable(False, False)

        # Imagen interna (PIL) donde se replica lo que dibujas en el canvas
        self.imagen = Image.new("L", (TAMANO_CANVAS, TAMANO_CANVAS), 0)
        self.pincel = ImageDraw.Draw(self.imagen)
        self.ultimo_x = None
        self.ultimo_y = None

        self.construir_interfaz()

    def construir_interfaz(self):
        marco_izquierdo = ttk.Frame(self.ventana, padding=10)
        marco_izquierdo.grid(row=0, column=0)
        marco_derecho = ttk.Frame(self.ventana, padding=10)
        marco_derecho.grid(row=0, column=1, sticky="n")

        # --- Zona de dibujo ---
        ttk.Label(marco_izquierdo, text="Dibuja un número (0-9):").pack()
        self.canvas = tk.Canvas(marco_izquierdo, width=TAMANO_CANVAS,
                                height=TAMANO_CANVAS, bg="black",
                                cursor="pencil")
        self.canvas.pack(pady=5)
        self.canvas.bind("<B1-Motion>", self.al_dibujar)
        self.canvas.bind("<ButtonRelease-1>", self.al_soltar)

        ttk.Button(marco_izquierdo, text="Limpiar",
                   command=self.limpiar).pack(pady=5)

        # --- Resultado ---
        ttk.Label(marco_derecho, text="Predicción:",
                  font=("Arial", 14)).pack()
        self.etiqueta_prediccion = ttk.Label(marco_derecho, text="-",
                                             font=("Arial", 72, "bold"))
        self.etiqueta_prediccion.pack()
        self.etiqueta_confianza = ttk.Label(marco_derecho, text="",
                                            font=("Arial", 12))
        self.etiqueta_confianza.pack(pady=(0, 10))

        # Una barra de probabilidad por cada dígito
        self.barras = []
        self.etiquetas_porcentaje = []
        for digito in range(10):
            fila = ttk.Frame(marco_derecho)
            fila.pack(fill="x", pady=1)
            ttk.Label(fila, text=str(digito), width=2).pack(side="left")
            barra = ttk.Progressbar(fila, length=150, maximum=100)
            barra.pack(side="left", padx=5)
            etiqueta = ttk.Label(fila, text="0.0%", width=7)
            etiqueta.pack(side="left")
            self.barras.append(barra)
            self.etiquetas_porcentaje.append(etiqueta)

    # --- Eventos del mouse ---
    def al_dibujar(self, evento):
        """Dibuja un trazo continuo en el canvas y en la imagen interna."""
        if self.ultimo_x is not None:
            self.canvas.create_line(self.ultimo_x, self.ultimo_y,
                                    evento.x, evento.y,
                                    fill="white", width=GROSOR_PINCEL,
                                    capstyle=tk.ROUND, smooth=True)
            self.pincel.line([self.ultimo_x, self.ultimo_y, evento.x, evento.y],
                             fill=255, width=GROSOR_PINCEL)

        # Círculo en el punto actual para que los extremos queden redondos
        radio = GROSOR_PINCEL // 2
        self.pincel.ellipse([evento.x - radio, evento.y - radio,
                             evento.x + radio, evento.y + radio], fill=255)
        self.ultimo_x = evento.x
        self.ultimo_y = evento.y

    def al_soltar(self, evento):
        self.ultimo_x = None
        self.ultimo_y = None
        self.predecir()

    # --- Acciones ---
    def predecir(self):
        entrada = preparar_imagen(self.imagen)
        if entrada is None:
            return

        # El modelo espera un lote: forma (1, 28, 28)
        probabilidades = self.modelo.predict(entrada.reshape(1, 28, 28),
                                             verbose=0)[0]
        digito = int(np.argmax(probabilidades))

        self.etiqueta_prediccion.config(text=str(digito))
        self.etiqueta_confianza.config(
            text=f"Confianza: {probabilidades[digito] * 100:.1f}%")

        for i in range(10):
            porcentaje = float(probabilidades[i]) * 100
            self.barras[i]["value"] = porcentaje
            self.etiquetas_porcentaje[i].config(text=f"{porcentaje:.1f}%")

    def limpiar(self):
        self.canvas.delete("all")
        self.pincel.rectangle([0, 0, TAMANO_CANVAS, TAMANO_CANVAS], fill=0)
        self.ultimo_x = None
        self.ultimo_y = None
        self.etiqueta_prediccion.config(text="-")
        self.etiqueta_confianza.config(text="")
        for i in range(10):
            self.barras[i]["value"] = 0
            self.etiquetas_porcentaje[i].config(text="0.0%")


if __name__ == "__main__":
    modelo_cargado = obtener_modelo()
    ventana_principal = tk.Tk()
    aplicacion = AplicacionDigitos(ventana_principal, modelo_cargado)
    ventana_principal.mainloop()
