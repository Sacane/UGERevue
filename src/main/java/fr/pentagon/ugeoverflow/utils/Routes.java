package fr.pentagon.ugeoverflow.utils;

public final class Routes {
    private Routes() {}
    public static final String ROOT = "/api";
    public static final class User {
        private User() {}
        private static final String IDENT = "/users";
        public static final String ROOT = Routes.ROOT + IDENT;
        public static final String FOLLOW = ROOT + "/follow/{id}";
        public static final String UNFOLLOW = ROOT + "/unfollow/{id}";
    }
    public static final class Question {
        private Question() {}
        private static final String IDENT = "/questions";
        private static final String WITH_ID = "/{questionId}";
        public static final String ROOT = Routes.ROOT + IDENT;
        public static final String WITH_QUESTION_ID = ROOT + WITH_ID;
    }
    public static final class Vote {
        private Vote() {}
        public static final String ROOT = Routes.ROOT + "/votes";
        public static final String UP_VOTE = ROOT + "/upvote/";
        public static final String DOWN_VOTE = ROOT + "/downvote/";

    }
}
