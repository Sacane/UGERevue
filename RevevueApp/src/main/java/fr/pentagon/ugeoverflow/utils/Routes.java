package fr.pentagon.ugeoverflow.utils;

public final class Routes {
    private Routes() {}
    public static final String ROOT = "/api";
    public static final class User {
        private User() {}
        private static final String IDENT = "/users";
        public static final String ROOT = Routes.ROOT + IDENT;
        public static final String FOLLOW = ROOT + "/follow";
        public static final String UNFOLLOW = ROOT + "/unfollow";
    }
    public static final class Question {
        private Question() {}
        public static final String IDENT = "/questions";
        private static final String WITH_ID = "/{questionId}";
        public static final String ROOT = Routes.ROOT + IDENT;
        public static final String WITH_QUESTION_ID = ROOT + WITH_ID;
        public static final String SEARCH = Routes.ROOT + IDENT + "/search";
    }
    public static final class Vote {
        private Vote() {}
        public static final String IDENT = "/votes";
        public static final String ROOT = Routes.ROOT + "/votes";
        public static final String UP_VOTE = ROOT + "/upvote/";
        public static final String DOWN_VOTE = ROOT + "/downvote/";

    }
    public static final class Auth {
        private Auth() {}
        public static final String LOGIN =  ROOT + "/login";
        public static final String LOGOUT = ROOT + "/logout";
    }
    public static final class Review {
        private Review() {}
        public static final String ROOT = Routes.ROOT + "/reviews";
    }
}
