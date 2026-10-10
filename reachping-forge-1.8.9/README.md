# ReachPing – Forge 1.8.9

Spielt einen Sound, wenn du ein Entity aus größerer Distanz triffst.

## Bauen
1. Ein Forge-1.8.9-Projekt (MDK 11.15.1.2318) mit JDK 8 und Gradle 2.14 einrichten
   (dieses Projekt enthält die passende build.gradle schon).
2. `gradlew setupDecompWorkspace` und danach `gradlew build`
3. Fertige Jar: `build/libs/reachping-forge-1.8.9-1.0.jar` -> in den `mods`-Ordner.

## Bedienung
- `/reachping` öffnet die Einstellungen
- Tastenbelegung "Toggle ReachPing" (Steuerung > ReachPing), standardmäßig unbelegt
- Config: `config/reachping.cfg`
- Sounds heißen in 1.8.9 anders, z. B. `note.pling`, `note.bd`, `random.orb`, `random.levelup`
