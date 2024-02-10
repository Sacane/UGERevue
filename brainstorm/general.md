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

## @RequireAuthentication
Nous avons maintenant accès à l'annotation custom @RequireAuthentication qui permet de check automatiquement si un utilisateur est authentifié ou non lorsqu'on utilise une route qui la contient.
Exemple : 
```java
    @PostMapping
    @RequireAuthentication
    public ResponseEntity<String> createReview(@RequestBody NewReviewDTO newReviewDTO) {
        ...
    }
```

Attention, quand vous faite les tests, l'annotation ne fonctionne pas directement, il faut passer par une classe de test utilitaire
si vous voulez que votre route test bien que l'utilisateur est connecté ou non : 

```java
@Component
public class AuthTestUtils {

    @Autowired
    private RequestMappingHandlerMapping requireScanner;

    /**
     * assert either if the route contains the @RequireAuth annotation and user is connected or it does not contains it.
     * @param path path of the api route
     *
     * Ex:
     *             assertUserIsConnected("/api/reviews", "GET")
     */
    public void assertUserIsConnected(String path, String method) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(path);
        request.setMethod(method);
        MockHttpServletResponse response = new MockHttpServletResponse();
        var filter = new AuthFilter(requireScanner);
        var mockFilterChain = new MockFilterChain();
        assertDoesNotThrow(() -> filter.doFilter(request, response, mockFilterChain));
    }

    /**
     * assert that the given route is annoted with @RequireAuthentication but the user is not connected
     * @param path
     * @param method
     */
    public void assertNotConnected(String path, String method) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(path);
        request.setMethod(method);
        MockHttpServletResponse response = new MockHttpServletResponse();
        var filter = new AuthFilter(requireScanner);
        var mockFilterChain = new MockFilterChain();
        assertThrows(HttpException.class, () -> filter.doFilter(request, response, mockFilterChain));
    }
}

```