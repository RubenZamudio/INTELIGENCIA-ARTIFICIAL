import numpy as np
import matplotlib.pyplot as plt
from sklearn.datasets import fetch_california_housing

# Configuramos el estilo de las gráficas
plt.style.use('seaborn-v0_8-whitegrid' if 'seaborn-v0_8-whitegrid' in plt.style.available else 'default')

# 1. CARGA DE DATOS (California Housing)
california = fetch_california_housing(as_frame=True)

# X: Ingreso medio del vecindario (MedInc, en decenas de miles de USD)
# Y: Valor medio de la casa (MedHouseVal, en cientos de miles de USD)
# Tomamos un subconjunto pequeño (50 muestras) para que se vea claro en la gráfica
X = california.data['MedInc'].values[:50]
Y = california.target.values[:50]

print(f"Datos cargados con éxito: {len(X)} muestras analizadas.")

# 2. INICIALIZACIÓN DE PARÁMETROS
w = 0.0
b = 0.0

# Hiperparámetros
learning_rate = 0.01  # Tasa de aprendizaje ajustada para esta escala
epochs = 5000         # Iteraciones de entrenamiento
n = len(X)

# Historial para graficar la evolución del costo
historial_costos = []

# 3. ENTRENAMIENTO
for epoch in range(epochs):
    # Predicción actual
    Y_pred = w * X + b          # X -> Features     Y -> Labels

    # Cálculo del Costo (MSE con factor 1/2)
    cost = (1 / (2 * n)) * np.sum((Y_pred - Y) ** 2)
    historial_costos.append(cost)

    # Derivadas parciales (Gradientes) de J = 1/(2n) * sum((Y_pred - Y)^2)
    dw = (1 / n) * np.sum((Y_pred - Y) * X)
    db = (1 / n) * np.sum(Y_pred - Y)

    # Actualización de pesos
    w = w - learning_rate * dw
    b = b - learning_rate * db

print("¡Entrenamiento completado!")
print(f"Ecuación final aprendida: Valor de la casa = {w:.4f} * (Ingreso medio) + {b:.4f}")

# Verificación contra la solución exacta de mínimos cuadrados
w_exacto, b_exacto = np.polyfit(X, Y, 1)
print(f"Solución exacta (np.polyfit):  w = {w_exacto:.4f}, b = {b_exacto:.4f}")

# 4. VISUALIZACIÓN DE RESULTADOS
plt.figure(figsize=(10, 6))

# Dibujar los puntos reales
plt.scatter(X, Y, color='royalblue', s=70, label='Datos Reales (California Housing)', zorder=5)

# Dibujar la línea de tendencia aprendida por el modelo
X_linea = np.linspace(min(X) - 0.5, max(X) + 0.5, 100)
Y_linea = w * X_linea + b
plt.plot(X_linea, Y_linea, color='crimson', linewidth=3, label=f'Modelo Lineal: Y = {w:.2f}X + {b:.2f}')

# Etiquetas y formato estético
plt.title('Regresión Lineal From Scratch: Ingreso Medio vs Valor de la Casa', fontsize=12, fontweight='bold', pad=15)
plt.xlabel('Ingreso medio (MedInc, x $10,000 USD)', fontsize=11)
plt.ylabel('Valor medio de la casa (x $100,000 USD)', fontsize=11)
plt.legend(fontsize=11)
plt.grid(True, linestyle='--', alpha=0.6)

plt.show()

plt.figure(figsize=(8, 4))
plt.plot(range(epochs), historial_costos, color='darkorange', linewidth=2)
plt.title('Evolución de la Función de Costo (Error Cuadrático Medio)', fontsize=11, fontweight='bold')
plt.xlabel('Épocas / Iteraciones', fontsize=10)
plt.ylabel('Costo (MSE)', fontsize=10)
plt.grid(True, linestyle='--', alpha=0.6)
plt.show()