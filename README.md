Progetto di E-commerce Full-Stack sviluppato a scopo universitario con Spring Boot per il Backend e React per il Frontend, destinato alla vendita online di prodotti ufficiali per la squadra di calcio Inter (merchandising). Il progetto è stato concepito per dimostrare la conoscenza e il buon utilizzo delle architetture a microservizi, dell'autenticazione sicura tramite JWT e della gestione della concorrenza in ambienti transazionali.

Backend (API REST)

    Framework: Spring Boot (Java 21)

    Sicurezza: Spring Security per l'autenticazione, JWT (JSON Web Token) per l'autorizzazione stateless.

    Database: PostgreSQL (relazionale)

    OR Mapping: Hibernate / JPA per la gestione delle entità.

    Build Tool: Maven

Frontend (Interfaccia Utente)

    Libreria: React

    Styling: Bootstrap per un design moderno e responsivo.

    Comunicazione: Fetch API per le chiamate HTTP protette con JWT.

Autenticazione e Autorizzazione

    Frontend: React gestisce la logica di login, salva il token JWT (tipicamente in localStorage) e lo allega a ogni richiesta API protetta.

    Backend: Spring Security intercetta ogni richiesta API e valida il JWT per autorizzare l'accesso (autorizzazione basata sui Ruoli codificati nel token).

Consistenza e Concorrenza

    Accesso Carrello: Se un utente accede al carrello da più dispositivi e lo stato del carrello locale non coincide con il DB al momento del checkout, il backend restituisce un errore 409 CONFLICT, forzando l'utente a ricaricare l'ultima versione.

    Impatto dell'Admin: Le modifiche del catalogo da parte di un amministratore (es. un cambio prezzo) non hanno effetto retroattivo su carrelli in corso, tutelando l'utente che completa l'acquisto alle condizioni iniziali.
-------------------------------------------
Istruzioni per l'Avvio

Prerequisiti

      Java Development Kit (JDK 21+)

      Node.js e npm

      Un'istanza di PostgreSQL

1. Avvio del Backend (Spring Boot)

    Configurare le credenziali del database PostgreSQL nel file application.properties.

    Avviare l'applicazione su interstore-backend>>src>>main>>java>>com>>interstore.interstore_backend>>InterStoreBAckendApplication (L'applicazione sarà disponibile su http://localhost:8080).

2. Avvio del Frontend (React)

    Navigare nella cartella del Frontend.

    Installare le dipendenze: npm install

    Avviare l'applicazione React: npm start (L'interfaccia utente sarà disponibile su http://localhost:3000).

   
