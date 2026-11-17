# Gestion d’un parc automobile avec Spring Cloud

Ce projet de TP associe des voitures à leurs propriétaires. Il est organisé en quatre applications : un annuaire Eureka, une passerelle HTTP, un service de clients et un service de voitures.

## Parcours d’une requête

La passerelle découvre les services dans Eureka. Le service voiture lit ses données dans sa propre base H2, puis demande les informations du propriétaire au service client via la passerelle. La classe `ClientDirectory` prend en charge cet appel distant ; `CarService` construit la réponse métier.

Si le propriétaire ne peut pas être chargé, la voiture reste consultable avec un champ `client` nul. Une erreur HTTP est journalisée pour faciliter le diagnostic. Cette réponse partielle ne distingue pas un client inexistant d’un service indisponible.

## Préparer et lancer

Installer Java 17 et Maven. Les bases H2 sont créées sur disque au démarrage : aucune installation de MySQL n’est nécessaire.

Depuis la racine :

```powershell
mvn clean verify
```

Ouvrir ensuite quatre terminaux à la racine et lancer une commande par terminal, dans cet ordre :

```powershell
mvn -f eureka-server/pom.xml spring-boot:run
mvn -f client-service/pom.xml spring-boot:run
mvn -f gateway-service/pom.xml spring-boot:run
mvn -f car-service/pom.xml spring-boot:run
```

| Application | Port | Rôle |
| --- | --- | --- |
| Eureka | 8761 | Annuaire des instances |
| Clients | 8081 | Informations des propriétaires |
| Voitures | 8082 | Parc et association au propriétaire |
| Gateway | 8888 | Point d’entrée HTTP |

## Essayer le parcours complet

Conserver l’identifiant réellement renvoyé lors de la création du client :

```powershell
$owner = Invoke-RestMethod -Method Post -Uri 'http://localhost:8888/SERVICE-CLIENT/api/client' -ContentType 'application/json' -Body '{"nom":"Salma","age":24}'
$payload = @{ brand = 'Dacia'; model = 'Sandero'; matricule = '4821-B-8'; client_id = $owner.id } | ConvertTo-Json
$car = Invoke-RestMethod -Method Post -Uri 'http://localhost:8888/SERVICE-CAR/api/car' -ContentType 'application/json' -Body $payload
Invoke-RestMethod "http://localhost:8888/SERVICE-CAR/api/car/$($car.id)"
```

Les deux services exposent également une lecture de collection et une lecture par identifiant sur `/api/client` et `/api/car`. Une voiture inconnue renvoie HTTP 404.

## Configurer l’appel distant

Le service voiture utilise par défaut la passerelle locale. Pour choisir une autre adresse, définir la variable dans le terminal avant son démarrage :

```powershell
$env:CLIENT_SERVICE_URL = 'http://localhost:8888/SERVICE-CLIENT/api/client'
```

Les délais de connexion et de lecture sont de cinq secondes chacun. Chaque voiture entraîne un appel au service client si elle possède un identifiant de propriétaire ; une grande collection peut donc être lente lorsque ce service est indisponible.

## Points à expliquer pendant la démonstration

- Eureka fournit les adresses des services à la passerelle.
- Chaque service métier possède sa base de données H2.
- L’association entre voiture et client utilise un identifiant, sans jointure entre bases.
- L’accès HTTP est isolé dans `ClientDirectory` pour pouvoir tester les erreurs réseau séparément.
- Les consoles H2 et la configuration locale sont destinées au TP.
