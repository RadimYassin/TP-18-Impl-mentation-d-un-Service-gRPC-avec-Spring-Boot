# Service Bancaire gRPC avec Spring Boot
<img width="961" height="876" alt="Screenshot 2025-12-19 231209" src="https://github.com/user-attachments/assets/063fb619-f509-438c-b6e9-e168b5e1beb8" />

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![gRPC](https://img.shields.io/badge/gRPC-1.53.0-blue.svg)](https://grpc.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)

Un service bancaire moderne implémentant gRPC avec Spring Boot pour la gestion de comptes bancaires haute performance.

## 📋 Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [API gRPC](#-api-grpc)
- [Base de données](#-base-de-données)
- [Tests](#-tests)
- [Déploiement](#-déploiement)

## 🎯 Vue d'ensemble

Ce projet démontre l'implémentation d'un service bancaire utilisant **gRPC** comme protocole de communication au lieu de REST traditionnel. L'architecture orientée services permet des communications haute performance grâce à la sérialisation binaire de Protocol Buffers.

### Pourquoi gRPC ?

- **Performance** : Jusqu'à 7x plus rapide que REST grâce à HTTP/2 et Protocol Buffers
- **Typage fort** : Contrat d'API strict défini dans `.proto` éliminant les ambiguïtés
- **Génération de code** : Stubs client/serveur générés automatiquement
- **Streaming** : Support natif du streaming bidirectionnel pour les fonctionnalités temps réel

## ✨ Fonctionnalités

- ✅ **Gestion des comptes** : Création et consultation de comptes bancaires
- ✅ **Types de comptes** : Support des comptes COURANT et EPARGNE
- ✅ **Statistiques** : Calcul en temps réel du nombre de comptes, somme et moyenne des soldes
- ✅ **Base de données** : Persistance avec JPA/Hibernate et H2
- ✅ **Console H2** : Interface web pour gérer la base de données
- ✅ **Hot reload** : Spring Boot DevTools pour le développement

## 🏗️ Architecture

### Structure en couches

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

### Structure du projet

```
src/
├── main/
│   ├── java/ma/projet/grpc/
│   │   ├── GrpcApplication.java          # Point d'entrée
│   │   ├── controllers/
│   │   │   └── CompteServiceImpl.java    # Implémentation gRPC
│   │   ├── services/
│   │   │   └── CompteService.java        # Logique métier
│   │   ├── repositories/
│   │   │   └── CompteRepository.java     # Accès données
│   │   └── entities/
│   │       └── Compte.java               # Entité JPA
│   ├── proto/
│   │   └── CompteService.proto           # Définition gRPC
│   └── resources/
│       └── application.properties        # Configuration
└── test/
    └── java/                             # Tests unitaires
```

## 🛠️ Technologies

| Technologie | Version | Rôle |
|------------|---------|------|
| **Java** | 17 LTS | Langage de programmation |
| **Spring Boot** | 3.2.0 | Framework applicatif |
| **gRPC** | 1.53.0 | Protocole de communication |
| **Protocol Buffers** | 3.21.7 | Sérialisation des données |
| **H2 Database** | Runtime | Base de données embarquée |
| **Hibernate/JPA** | 6.3.1 | ORM pour la persistance |
| **Lombok** | 1.18.30 | Réduction du code boilerplate |
| **Maven** | 3.8+ | Gestion des dépendances |

## 📦 Prérequis

- **Java 17** ou supérieur ([Télécharger](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** ([Télécharger](https://maven.apache.org/download.cgi))
- **Un client gRPC** (optionnel pour les tests) :
  - [BloomRPC](https://github.com/bloomrpc/bloomrpc) (recommandé)
  - [Postman](https://www.postman.com/) (supporte gRPC)
  - [grpcurl](https://github.com/fullstorydev/grpcurl) (ligne de commande)

## 🚀 Installation

### 1. Cloner le projet

```bash
git clone https://github.com/RadimYassin/TP-18-Impl-mentation-d-un-Service-gRPC-avec-Spring-Boot.git
cd TP-18-Impl-mentation-d-un-Service-gRPC-avec-Spring-Boot
```

### 2. Compiler le projet

```bash
mvn clean install
```

Cette commande :
- Nettoie le répertoire `target/`
- Compile le fichier `.proto` et génère les classes Java
- Compile le code source
- Exécute les tests
- Package l'application en JAR

### 3. Lancer l'application

```bash
mvn spring-boot:run
```

Ou avec le JAR généré :

```bash
java -jar target/grpc-service-1.0.0.jar
```

## ⚙️ Configuration

### Ports

L'application démarre sur deux ports :

| Service | Port | Description |
|---------|------|-------------|
| **gRPC Server** | `9090` | Service gRPC pour les clients |
| **HTTP Server** | `8081` | Console H2 et endpoints Spring Boot |

### Fichier `application.properties`

```properties
# Serveur gRPC
grpc.server.port=9090

# Base de données H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=

# Console H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Serveur HTTP
server.port=8081

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 💻 Utilisation

### Accès à la console H2

1. Ouvrir le navigateur : **http://localhost:8081/h2-console**
2. Paramètres de connexion :
   - **JDBC URL** : `jdbc:h2:mem:testdb`
   - **Username** : `sa`
   - **Password** : *(laisser vide)*

### Tester avec BloomRPC

1. **Importer le proto** : `src/main/proto/CompteService.proto`
2. **Configurer l'adresse** : `localhost:9090`
3. **Tester les services** :

#### Créer un compte

```json
{
  "compte": {
    "solde": 5000.50,
    "dateCreation": "2024-12-19",
    "type": "EPARGNE"
  }
}
```

#### Récupérer tous les comptes

```json
{}
```

#### Rechercher par ID

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Obtenir les statistiques

```json
{}
```

## 📡 API gRPC

### Service `CompteService`

| Méthode | Requête | Réponse | Description |
|---------|---------|---------|-------------|
| `AllComptes` | `GetAllComptesRequest` | `GetAllComptesResponse` | Liste tous les comptes |
| `CompteById` | `GetCompteByIdRequest` | `GetCompteByIdResponse` | Recherche un compte par ID |
| `TotalSolde` | `GetTotalSoldeRequest` | `GetTotalSoldeResponse` | Statistiques des soldes |
| `SaveCompte` | `SaveCompteRequest` | `SaveCompteResponse` | Crée un nouveau compte |

### Messages Protobuf

#### Compte

```protobuf
message Compte {
    string id = 1;
    float solde = 2;
    string dateCreation = 3;
    TypeCompte type = 4;
}

enum TypeCompte {
    COURANT = 0;
    EPARGNE = 1;
}
```

#### Statistiques

```protobuf
message SoldeStats {
    int32 count = 1;      // Nombre de comptes
    float sum = 2;        // Somme des soldes
    float average = 3;    // Moyenne des soldes
}
```

## 🗄️ Base de données

### Schéma de la table `compte`

| Colonne | Type | Contrainte | Description |
|---------|------|------------|-------------|
| `id` | VARCHAR(255) | PRIMARY KEY | UUID généré automatiquement |
| `solde` | FLOAT | NOT NULL | Solde du compte |
| `date_creation` | VARCHAR(255) | - | Date de création |
| `type` | VARCHAR(255) | - | Type de compte (COURANT/EPARGNE) |

### Choix de conception

- **UUID** : Identifiants uniques pour la sécurité et la distribution
- **H2 en mémoire** : Parfait pour le développement et les tests
- **JPA/Hibernate** : Abstraction permettant une migration facile vers PostgreSQL/MySQL

## 🧪 Tests

### Exécuter les tests

```bash
mvn test
```

### Tests manuels avec grpcurl

```bash
# Lister les services
grpcurl -plaintext localhost:9090 list

# Créer un compte
grpcurl -plaintext -d '{
  "compte": {
    "solde": 1000,
    "dateCreation": "2024-12-19",
    "type": "COURANT"
  }
}' localhost:9090 CompteService/SaveCompte

# Récupérer tous les comptes
grpcurl -plaintext localhost:9090 CompteService/AllComptes
```

## 🚢 Déploiement

### Production avec PostgreSQL

1. **Ajouter la dépendance PostgreSQL** dans `pom.xml` :

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

2. **Modifier `application.properties`** :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/banque
spring.datasource.username=admin
spring.datasource.password=${DB_PASSWORD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Docker

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/grpc-service-1.0.0.jar app.jar
EXPOSE 9090 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker build -t grpc-banking-service .
docker run -p 9090:9090 -p 8081:8081 grpc-banking-service
```

## 🔒 Sécurité

⚠️ **Attention** : Cette version est un prototype de développement.

### Pour la production, ajouter :

- ✅ **TLS mutuel** : Chiffrer les communications gRPC
- ✅ **Authentification** : OAuth2 ou JWT
- ✅ **Validation** : Valider tous les inputs
- ✅ **Rate limiting** : Protéger contre les abus
- ✅ **Audit logging** : Tracer les opérations sensibles

## 📝 Évolutions futures

### Court terme
- Tests unitaires et d'intégration avec JUnit 5
- Validation des données (solde minimum, format de date)
- Pagination pour `AllComptes`

### Moyen terme
- Authentification/Autorisation avec Spring Security
- Observabilité avec Micrometer et Sleuth
- Cache Redis pour les comptes fréquemment consultés

### Long terme
- Streaming bidirectionnel pour les notifications temps réel
- Event Sourcing pour l'historique des modifications
- Multi-tenancy pour plusieurs organisations

## 👤 Auteur

**Radim Yassin**

- GitHub: [@RadimYassin](https://github.com/RadimYassin)
- Projet: TP 18 - Implémentation d'un Service gRPC avec Spring Boot

## 📄 Licence

Ce projet est développé dans un cadre éducatif.

---

**Version** : 1.0.0  
**Date** : Décembre 2024  
**Framework** : Spring Boot 3.2.0  
**Java** : 17 LTS
