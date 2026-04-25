# 😼 MiEmojiKeyboard

Teclado Android personalizado con soporte para emojis estándar y emojis personalizados desde tu galería.

## Características

- ✅ 8 categorías de emojis (Caritas, Gestos, Animales, Comida, Actividades, Viajes, Símbolos, Objetos)
- ✅ Emojis personalizados desde tu galería de fotos
- ✅ Borrar, pegar desde portapapeles, cambiar teclado
- ✅ Guardar y eliminar emojis personalizados (mantén pulsado)
- ✅ Diseño oscuro moderno

## Cómo compilar

```bash
cd MiEmojiKeyboard
./gradlew assembleDebug
```

El APK estará en `app/build/outputs/apk/debug/app-debug.apk`

## Cómo instalar en el dispositivo

1. Habilitar "Fuentes desconocidas" en Ajustes
2. `adb install app/build/outputs/apk/debug/app-debug.apk`
3. Ir a Ajustes → Sistema → Idioma y teclado → Teclados virtuales
4. Activar **MiEmoji Keyboard**
5. Seleccionarlo como teclado predeterminado

## Estructura del proyecto

```
MiEmojiKeyboard/
├── app/src/main/
│   ├── java/com/example/mikeyboard/
│   │   ├── data/
│   │   │   ├── EmojiCategory.kt       ← 8 categorías con +300 emojis
│   │   │   ├── CustomEmoji.kt         ← Modelo de datos
│   │   │   └── CustomEmojiRepository.kt ← Persistencia en SharedPrefs
│   │   ├── ui/
│   │   │   ├── EmojiGridAdapter.kt    ← Grid de emojis estándar
│   │   │   └── CustomEmojiAdapter.kt  ← Grid de emojis personalizados
│   │   ├── utils/
│   │   │   └── ImageUtils.kt          ← Resize eficiente de imágenes
│   │   ├── MyKeyboardService.kt       ← Servicio principal del teclado
│   │   └── EmojiPickerActivity.kt     ← UI para añadir emojis
│   └── res/
│       ├── layout/
│       │   ├── keyboard_view.xml
│       │   ├── item_emoji.xml
│       │   ├── item_custom_emoji.xml
│       │   └── activity_emoji_picker.xml
│       └── xml/method.xml
```

## Dependencias

- Kotlin 1.9
- AndroidX RecyclerView
- Gson (persistencia de emojis)
- Material Components
