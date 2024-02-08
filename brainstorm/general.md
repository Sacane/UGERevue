## Définitions 

- Questions : 
Poster une question sur l'application en donnant un fichier java constitué de commentaires

- Reviews = Commentaires
  - Généraux (qui se rapporte à tout le code)
  - Ciblé (qui prends une ligne de début et une ligne de fin)

## Gestion des revues

On affiche les 3 premiers reviews imbriqués sur la page de question, si il y en a + on affiche le nombre de review imbriqués et on 
ajoute un lien vers une page spécifique à cette revue.

## Gestion du projet 

Semaine du 26/02 : full android en laissant JEE


## Idées / A faire

- Vérifier que tout les DTO aient l'annotation @Valid
- Accès à l'annotation custom @RequireAuthentication qui permet de check automatiquement si un utilisateur est authentifié ou non lorsqu'on utilise une route qui la contient.
Exemple : 
```java
    @PostMapping
    @RequireAuthentication
    public ResponseEntity<String> createReview(@RequestBody NewReviewDTO newReviewDTO) {
        ...
    }
```