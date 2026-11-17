# Vérification du projet — 17 novembre 2026

- Java utilisé : OpenJDK Temurin 17.0.19.
- Maven verify : succès, les quatre modules sont compilés et les JAR exécutables sont générés.
- ClientApiTest : 1 test réussi (création 201, lecture, absence 404, validation 400).
- CarServiceTest : 3 tests réussis (enrichissement, absence 404, indisponibilité 503).
- Démarrage réel des quatre applications : succès.
- Registre Eureka : SERVICE-CLIENT, SERVICE-CAR et GATEWAY présents.
- POST Client et POST Car : succès.
- GET Car avec détails Client via RestTemplate/Eureka : succès.
- GET des deux API via Gateway : succès.
- Applications arrêtées après vérification.

Le parcours réel a été exécuté avec le profil demo (bases H2 séparées), car MySQL et le moteur Docker ne sont pas démarrés sur cette machine. Le mode MySQL est configuré par défaut mais n'a pas été exécuté ici.

Pour reproduire automatiquement la vérification après compilation, avec les ports libres :

```powershell
powershell -ExecutionPolicy Bypass -File .\verify-demo.ps1
```

Ce script lance les quatre applications en arrière-plan, attend leur disponibilité, teste les endpoints puis arrête les processus qu'il a créés.
