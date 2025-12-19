# Service Bancaire gRPC avec Spring Boot

## Vue d'ensemble du projet

Ce projet représente une implémentation moderne d'un système de gestion de comptes bancaires, construit sur une architecture orientée services utilisant gRPC comme protocole de communication. L'objectif principal était de démontrer comment les technologies de communication haute performance peuvent être intégrées dans l'écosystème Spring Boot pour créer des services financiers robustes et évolutifs.

## Contexte et motivation technique

Dans le développement de ce service, j'ai fait le choix délibéré d'utiliser gRPC plutôt que REST traditionnel pour plusieurs raisons stratégiques :

### Pourquoi gRPC ?

**Performance et efficacité** : gRPC utilise HTTP/2 et Protocol Buffers, offrant une sérialisation binaire bien plus compacte que JSON. Pour un système bancaire où les transactions doivent être rapides et fiables, cette optimisation est cruciale. Les tests montrent que gRPC peut être jusqu'à 7 fois plus rapide que REST dans certains scénarios.

**Contrat strict et typage fort** : Avec Protocol Buffers, le contrat d'API est défini de manière explicite dans un fichier `.proto`. Cela élimine les ambiguïtés et permet une génération automatique de code client/serveur, réduisant drastiquement les erreurs d'intégration.

**Communication bidirectionnelle** : Bien que non exploité dans cette version initiale, gRPC supporte nativement le streaming bidirectionnel, ouvrant la voie à des fonctionnalités temps réel comme les notifications de transactions.

## Architecture technique

### Stack technologique

**Spring Boot 3.2.0** : J'ai opté pour la dernière version stable de Spring Boot pour bénéficier des améliorations de performance et de la compatibilité Jakarta EE. Cette version offre également une meilleure intégration avec Java 20.

**Java 17** : L'utilisation de Java 17 LTS (Long Term Support) assure la stabilité et la compatibilité à long terme. Cette version offre d'excellentes performances et est largement supportée par l'écosystème Spring Boot.

**gRPC 1.53.0** : Version stable et éprouvée, offrant un équilibre entre fonctionnalités et stabilité. L'intégration avec `grpc-server-spring-boot-starter` (3.1.0.RELEASE) simplifie considérablement la configuration.

**H2 Database** : Pour le développement et les tests, H2 offre une base de données embarquée performante. En production, une migration vers PostgreSQL ou MySQL serait triviale grâce à l'abstraction JPA.

**Lombok** : Réduit le code boilerplate de 40-50%, améliorant la lisibilité et la maintenabilité du code.

### Structure en couches

L'application suit une architecture en couches classique mais efficace :

```
┌─────────────────────────────┐
│   Couche gRPC (Controller)  │  ← Exposition des services via gRPC
├─────────────────────────────┤
│   Couche Service (Business) │  ← Logique métier et calculs
├─────────────────────────────┤
│   Couche Repository (Data)  │  ← Accès aux données via JPA
├─────────────────────────────┤
│   Couche Entité (Domain)    │  ← Modèle de domaine
└─────────────────────────────┘
```

**Séparation des responsabilités** : Chaque couche a un rôle bien défini. La couche gRPC ne contient que la logique de transformation entre les messages Protobuf et les objets métier. Les calculs statistiques (somme, moyenne des soldes) sont délégués à la couche service.

**Découplage** : L'utilisation d'interfaces (Repository) et d'injection de dépendances permet de changer facilement l'implémentation sans toucher au code métier.

## Choix de conception

### Gestion des identifiants

J'ai choisi d'utiliser des UUID générés automatiquement par JPA (`GenerationType.UUID`) plutôt que des identifiants séquentiels. Cette approche présente plusieurs avantages :
- **Sécurité** : Les UUID ne révèlent pas le nombre de comptes dans le système
- **Distribution** : Facilite la répartition sur plusieurs bases de données sans collision
- **Prédictibilité** : Impossible de deviner les identifiants d'autres comptes

### Énumération des types de compte

Le type de compte (COURANT/EPARGNE) est défini dans le fichier Protobuf et stocké comme String en base. Cette approche permet :
- Une évolution facile (ajout de nouveaux types)
- Une compatibilité totale entre le contrat gRPC et le modèle de données
- Une lisibilité accrue dans la base de données

### Gestion des erreurs

Les erreurs sont propagées via le mécanisme standard de gRPC (`responseObserver.onError()`). Pour la production, j'envisagerais d'ajouter :
- Des codes d'erreur personnalisés
- Un intercepteur global pour logger toutes les erreurs
- Une transformation des exceptions JPA en erreurs gRPC appropriées

## Dépendances clés et leur rôle

### Protobuf et gRPC

**`protobuf-java` (3.22.0)** : Bibliothèque de base pour la sérialisation/désérialisation des messages. C'est le cœur de la communication gRPC.

**`grpc-netty-shaded` (1.53.0)** : Implémente le transport réseau basé sur Netty. La version "shaded" évite les conflits de dépendances en embarquant Netty dans un package isolé.

**`grpc-protobuf` (1.53.0)** : Pont entre gRPC et Protobuf, permettant l'utilisation des messages Protobuf comme types de requête/réponse.

**`grpc-stub` (1.53.0)** : Génère les stubs client et serveur. Ces classes abstraites sont la base de notre implémentation.

**`grpc-server-spring-boot-starter` (3.1.0)** : Intégration Spring Boot qui configure automatiquement le serveur gRPC, gère le cycle de vie, et permet l'utilisation de l'annotation `@GrpcService`.

### Plugin Maven Protobuf

Le plugin `protobuf-maven-plugin` est crucial : il génère automatiquement les classes Java à partir du fichier `.proto` lors de la compilation. Sans lui, nous devrions écrire manuellement des centaines de lignes de code de sérialisation.

## Fonctionnalités implémentées

### 1. Récupération de tous les comptes (`AllComptes`)

Retourne la liste complète des comptes avec transformation JPA → Protobuf. Utilise les streams Java pour une conversion élégante et performante.

### 2. Recherche par identifiant (`CompteById`)

Recherche ciblée avec gestion d'erreur explicite si le compte n'existe pas. Le message d'erreur est descriptif pour faciliter le debugging côté client.

### 3. Statistiques globales (`TotalSolde`)

Calcule en temps réel :
- Le nombre total de comptes
- La somme des soldes
- La moyenne des soldes

Cette méthode démontre comment agréger des données côté serveur plutôt que de transférer toutes les données au client.

### 4. Création de compte (`SaveCompte`)

Persiste un nouveau compte avec génération automatique d'UUID. La méthode retourne le compte créé avec son identifiant, permettant au client de le référencer immédiatement.

## Configuration et démarrage

### Prérequis

- Java 17 ou supérieur
- Maven 3.8+
- Un client gRPC (BloomRPC, Postman, ou grpcurl)

### Compilation

```bash
mvn clean install
```

Cette commande :
1. Compile le fichier `.proto` et génère les classes Java dans `target/generated-sources`
2. Compile le code Java
3. Exécute les tests (si présents)
4. Package l'application en JAR exécutable

### Exécution

```bash
mvn spring-boot:run
```

L'application démarre sur deux ports :
- **Port 9090** : Serveur gRPC
- **Port 8081** : Serveur HTTP (pour la console H2)

### Accès à la console H2

URL : `http://localhost:8081/h2-console`

Paramètres de connexion :
- JDBC URL : `jdbc:h2:mem:testdb`
- Username : `sa`
- Password : (vide)

## Tests avec BloomRPC

### Installation

Télécharger BloomRPC depuis : https://github.com/bloomrpc/bloomrpc/releases

### Configuration

1. Importer le fichier `src/main/proto/CompteService.proto`
2. Configurer l'adresse du serveur : `localhost:9090`

### Exemples de requêtes

**Créer un compte :**
```json
{
  "compte": {
    "solde": 5000.50,
    "dateCreation": "2024-12-19",
    "type": "EPARGNE"
  }
}
```

**Récupérer tous les comptes :**
```json
{}
```

**Rechercher un compte par ID :**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Obtenir les statistiques :**
```json
{}
```

## Évolutions futures

### Court terme

- **Tests unitaires et d'intégration** : Utiliser JUnit 5 et Mockito pour tester chaque couche
- **Validation des données** : Ajouter des contraintes (solde minimum, format de date)
- **Pagination** : Pour `AllComptes`, implémenter une pagination pour gérer de gros volumes

### Moyen terme

- **Authentification/Autorisation** : Intégrer Spring Security avec JWT
- **Observabilité** : Ajouter Micrometer pour les métriques et Sleuth pour le tracing
- **Cache** : Utiliser Redis pour mettre en cache les comptes fréquemment consultés

### Long terme

- **Streaming bidirectionnel** : Notifications temps réel des transactions
- **Event Sourcing** : Tracer l'historique complet des modifications de compte
- **Multi-tenancy** : Support de plusieurs banques/organisations

## Sécurité

### État actuel

⚠️ **Attention** : Cette version est un prototype de développement. Elle ne doit PAS être déployée en production sans les améliorations suivantes :

- Pas d'authentification
- Pas de chiffrement TLS
- Console H2 accessible publiquement
- Pas de validation des entrées

### Recommandations pour la production

1. **TLS mutuel** : Chiffrer toutes les communications gRPC
2. **Authentification** : Implémenter OAuth2 ou JWT
3. **Validation** : Valider tous les inputs côté serveur
4. **Rate limiting** : Protéger contre les abus
5. **Audit logging** : Tracer toutes les opérations sensibles

## Base de données

### Choix de H2

H2 est parfait pour le développement car :
- Démarrage instantané
- Pas de configuration externe
- Console web intégrée
- Compatible JPA

### Migration vers production

Pour la production, je recommande PostgreSQL :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banque
spring.datasource.username=admin
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

Aucun changement de code n'est nécessaire grâce à l'abstraction JPA.

## Conclusion

Ce projet démontre comment construire un service financier moderne en combinant la puissance de gRPC avec la simplicité de Spring Boot. L'architecture en couches garantit la maintenabilité, tandis que le typage fort de Protobuf assure la fiabilité des communications.

Les choix techniques ont été guidés par trois principes :
1. **Performance** : gRPC et sérialisation binaire
2. **Maintenabilité** : Architecture en couches et séparation des responsabilités
3. **Évolutivité** : Design permettant l'ajout facile de nouvelles fonctionnalités

---

**Auteur** : Développé dans le cadre du TP 18 - Implémentation d'un Service gRPC avec Spring Boot  
**Version** : 1.0.0  
**Date** : Décembre 2024
#   T P - 1 8 - I m p l - m e n t a t i o n - d - u n - S e r v i c e - g R P C - a v e c - S p r i n g - B o o t  
 