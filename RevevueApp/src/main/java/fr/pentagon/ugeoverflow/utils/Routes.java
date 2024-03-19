package fr.pentagon.ugeoverflow.utils;

public final class Routes {
  public static final String ROOT = "/api";

  private Routes() {
  }

  public static final class User {
    private static final String IDENT = "/users";
    public static final String ROOT = Routes.ROOT + IDENT;
    public static final String FOLLOW = ROOT + "/follow";
    public static final String UNFOLLOW = ROOT + "/unfollow";
    public static final String CURRENT_USER = ROOT + "/current";
    public static final String PASSWORD = CURRENT_USER + "/password";
    public static final String FOLLOWING = CURRENT_USER + "/following";
    public static final String RECOMMENDED_REVIEW = CURRENT_USER + "/recommendedReview";

    private User() {
    }

  }
  public static final class Question {
    private Question() {}
    public static final String IDENT = "/questions";
    private static final String WITH_ID = "/{questionId}";
    public static final String ROOT = Routes.ROOT + IDENT;
    public static final String WITH_QUESTION_ID = ROOT + WITH_ID;
    public static final String SEARCH = Routes.ROOT + IDENT + "/search";
    public static final String CURRENT_USER = ROOT + "/currentUser";
  }

  public static final class Vote {
    public static final String IDENT = "/votes";
    public static final String ROOT = Routes.ROOT + "/votes";
    public static final String UP_VOTE = ROOT + "/upvote";
    public static final String DOWN_VOTE = ROOT + "/downvote";

    private Vote() {
    }

  }

  public static final class Auth {
    public static final String LOGIN = ROOT + "/login";
    public static final String LOGOUT = ROOT + "/logout";
    public static final String LOGGED = ROOT + "/logged";

    private Auth() {
    }
  }

  public static final class Review {
    public static final String ROOT = Routes.ROOT + "/reviews";

    private Review() {
    }
  }
  public static final class Tag{
    public static final String ROOT = Routes.ROOT + "/tags";

    private Tag() {}
  }
}
