# TP complet : Eureka, Client, Voiture et Gateway

Projet Maven multi-module, Java 17, Spring Boot 3.5.7 et Spring Cloud 2025.0.0.
Ouvrir ce dossier (ou son pom.xml) dans IntelliJ IDEA, Eclipse ou VS Code.

## 1. Structure et étapes réalisées

| Module | Port | Fonction |
| --- | --- | --- |
| eureka-server | 8761 | Annuaire avec @EnableEurekaServer, sans auto-enregistrement |
| client-service | 8081 | SERVICE-CLIENT, entité Client, repository JPA, service, contrôleur REST |
| car-service | 8082 | SERVICE-CAR, entité Car, repository JPA, service, contrôleur et CarResponse |
| gateway | 8888 | Routes lb://SERVICE-CLIENT et lb://SERVICE-CAR via Eureka |

Les sources sont fournies directement, avec les dépendances équivalentes à Spring Initializr.
Client possède id, nom et age (Float conformément à l'énoncé). Car possède id, marque, modele, matricule et clientId.
Les détails Client sont récupérés par un RestTemplate annoté @LoadBalanced, en utilisant le nom SERVICE-CLIENT.
Chaque microservice métier possède sa propre base MySQL : clientservicedb et carservicedb.
La configuration show-sql est placée sous spring.jpa, et OFF est entre guillemets dans le YAML Eureka.
Les ressources absentes renvoient 404, les données invalides 400, une panne du service Client 503.

## 2. Prérequis et compilation

Installer Java 17 et disposer d'une connexion Internet au premier lancement. Le wrapper télécharge Maven.
Dans PowerShell, depuis ce dossier :

```powershell
.\mvnw.cmd clean verify
```

Sous Linux/macOS : `sh mvnw clean verify`.
Les tests utilisent H2 en mémoire ; ils ne nécessitent pas MySQL.

Pour lancer et vérifier automatiquement les quatre applications en mode démonstration, puis les arrêter :

```powershell
powershell -ExecutionPolicy Bypass -File .\verify-demo.ps1
```

Les ports 8761, 8081, 8082 et 8888 doivent être libres. Le compte rendu de la vérification effectuée se trouve dans VERIFICATION.md.

## 3. Préparer MySQL (mode normal du TP)

Démarrer votre serveur MySQL sur localhost:3306, avec root sans mot de passe comme dans l'énoncé.
Les deux bases sont créées automatiquement si le compte a les permissions nécessaires.
Ou, avec Docker Desktop démarré et le port 3306 libre :

```powershell
docker compose up -d --wait
```

Le fichier compose.yml fournit un serveur MySQL local de démonstration avec stockage persistant.
Pour un autre compte, définir dans chacun des terminaux métier avant le lancement :

```powershell
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'votre-mot-de-passe'
```

DB_URL peut remplacer l'URL JDBC de chaque service ; EUREKA_URL peut remplacer l'adresse de l'annuaire.

## 4. Démarrer les quatre applications

Après compilation, ouvrir quatre terminaux PowerShell dans ce dossier.

Terminal 1 :

```powershell
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar
```

Ouvrir http://localhost:8761 : aucune instance au départ est normal.

Terminal 2 :

```powershell
java -jar client-service/target/client-service-0.0.1-SNAPSHOT.jar
```

Terminal 3 :

```powershell
java -jar car-service/target/car-service-0.0.1-SNAPSHOT.jar
```

Terminal 4 :

```powershell
java -jar gateway/target/gateway-0.0.1-SNAPSHOT.jar
```

Attendre que SERVICE-CLIENT, SERVICE-CAR et GATEWAY apparaissent UP dans Eureka, puis environ 30 à 60 secondes pour la propagation du registre.
Arrêter chaque application avec Ctrl+C. Pour arrêter MySQL Docker : `docker compose stop`.

### Démonstration sans MySQL

Pour les terminaux 2 et 3 uniquement, ajouter `--spring.profiles.active=demo` aux commandes java.
Cela utilise deux bases H2 distinctes en mémoire ; les données sont perdues à l'arrêt. Le mode par défaut reste MySQL.

## 5. Tester les endpoints

Dans un cinquième terminal :

```powershell
powershell -ExecutionPolicy Bypass -File .\test-endpoints.ps1
```

Le script crée un client, crée sa voiture, vérifie les détails Client dans la voiture puis teste les deux routes Gateway.
Il ajoute des données à chaque exécution. En cas de 503 au démarrage, attendre la propagation Eureka et relancer.

| Méthode | URL directe | Utilité |
| --- | --- | --- |
| GET | http://localhost:8081/api/client | Tous les clients |
| GET | http://localhost:8081/api/client/1 | Client par ID |
| POST | http://localhost:8081/api/client | Ajouter un client |
| GET | http://localhost:8082/api/car | Voitures avec détails Client |
| GET | http://localhost:8082/api/car/1 | Voiture avec détails Client |
| POST | http://localhost:8082/api/car | Ajouter une voiture associée à un client existant |

Corps JSON Client : `{"nom":"Soufiane","age":22}`.
Corps JSON Voiture : `{"marque":"Renault","modele":"Clio","matricule":"12345-A-6","clientId":1}`.
Utiliser l'identifiant renvoyé par la création du client.

Via Gateway : http://localhost:8888/SERVICE-CAR/api/car et http://localhost:8888/SERVICE-CLIENT/api/client.
Les mêmes sous-chemins /{id} et méthodes POST sont disponibles via Gateway.
Un POST réussi renvoie 201 et l'identifiant généré. Les bases sont initialement vides.

## 6. Tests et références

ClientApiTest vérifie création, persistance, lecture, validation et 404 avec Spring Boot/JPA/H2.
CarServiceTest vérifie l'enrichissement Client, le 404 voiture et le 503 en cas de panne réseau.
Le script test-endpoints.ps1 vérifie le parcours réel complet après démarrage.

Documentation : [compatibilité Spring Cloud](https://spring.io/projects/spring-cloud/), [Eureka](https://docs.spring.io/spring-cloud-netflix/reference/spring-cloud-netflix.html).
Les extensions sécurité, circuit breaker, monitoring avancé, traçabilité et configuration centralisée sont des pistes facultatives de l'énoncé ; elles ne sont pas nécessaires au fonctionnement de ce TP.
