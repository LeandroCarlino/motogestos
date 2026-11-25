# 📱 Moto Gestos para Android

> **Lleva los gestos clásicos de Motorola a cualquier teléfono Android.**  
> Enciende la linterna con dos sacudidas o abre la cámara con un doble giro de muñeca, incluso con la pantalla apagada.

---

## 📥 Descarga Directa

No necesitas clonar el repositorio ni compilar nada. Puedes descargar el instalador directamente:

- 👉 **[Descargar MotoGestos.apk (Directo desde el Repositorio)](https://raw.githubusercontent.com/LeandroCarlino/motogestos/main/MotoGestos.apk)**
- 📦 **[Ver Versiones en GitHub Releases](https://github.com/LeandroCarlino/motogestos/releases)**

*(El archivo pesa apenas **~25 KB**, ultra liviano y sin dependencias pesadas).*

---

## ✨ Características

- 🔦 **Doble hachazo (Agitar 2 veces):** Enciende o apaga la linterna al instante con retroalimentación háptica (vibración).
- 📷 **Doble giro de muñeca:** Abre la aplicación de cámara predeterminada del sistema.
- 💤 **Soporte con pantalla apagada:** Mantiene la lectura de sensores activa mediante un servicio en primer plano optimizado (`WakeLock` parcial eficiente).
- 🚀 **Inicio automático:** El servicio se inicia automáticamente al reiniciar el dispositivo (`BOOT_COMPLETED`).
- ⚡ **Ultra ligero y eficiente:** Construido 100% en Kotlin nativo, sin bibliotecas externas infladas, minimizando el consumo de batería y memoria RAM.

---

## 🛠️ Instalación y Configuración

1. **Descarga el APK:** Haz clic en el enlace de descarga superior desde tu móvil.
2. **Instala la app:** Abre el archivo descargado. Si Android te lo solicita, permite la instalación desde fuentes desconocidas para tu navegador/administrador de archivos.
3. **Abre Moto Gestos:**
   - Activa el interruptor **"Gestos activos"**.
   - Si deseas el gesto de cámara, activa la casilla **"Girar la muñeca 2 veces = abrir cámara"**.
4. **Configura los permisos recomendados (desde los botones de la app):**
   - **Mostrar sobre otras apps:** Necesario para que el gesto de giro pueda lanzar la cámara sobre la pantalla de bloqueo o sobre otra aplicación abierta.
   - **Batería sin restricciones:** Indispensable para evitar que el sistema operativo mate el proceso cuando el teléfono entre en reposo profundo (Doze mode).

---

## ⚙️ Sensibilidad y Ajustes

La detección utiliza algoritmos de umbral adaptativo sobre los sensores del dispositivo:
- **Linterna:** Acelerómetro con filtro paso bajo (`CHOP_THRESHOLD = 17 m/s²`).
- **Cámara:** Giroscopio en el eje longitudinal (`TWIST_THRESHOLD = 6 rad/s`).

---

## 🏗️ Compilación desde el código fuente

Si prefieres compilar el proyecto por tu cuenta:

```bash
# Clonar el repositorio
git clone https://github.com/LeandroCarlino/motogestos.git
cd motogestos

# Compilar APK Release con Gradle
gradle assembleRelease --no-daemon
```

El APK resultante se generará en `app/build/outputs/apk/release/app-release.apk`.

Además, el repositorio cuenta con **GitHub Actions** configurado en `.github/workflows/build.yml`, compilando automáticamente un APK nuevo en cada push.

---

## 📄 Licencia

Este proyecto es libre y de código abierto. ¡Siéntete libre de colaborar o personalizarlo!
