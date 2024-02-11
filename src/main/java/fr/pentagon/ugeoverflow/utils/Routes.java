package fr.pentagon.ugeoverflow.utils;

public final class Routes {
    private Routes() {}
    public static final String ROOT = "/api";
    public static final class User {
        private User() {}
        public static final String ROOT = Routes.ROOT + "/users";
        public static final String FOLLOW = ROOT + "/follow/{id}";
        public static final String UNFOLLOW = ROOT + "/unfollow/{id}";
    }
    public static final class Question {
        private Question() {}
        public static final String ROOT = Routes.ROOT + "/questions";
        public static final String WITH_QUESTION_ID = ROOT + "/{questionId}";
    }
    public static final class Vote {
        private Vote() {}
        private static final String QUESTION_ID = "/questions/{questionId}";
        public static final String ROOT = Routes.ROOT + "/votes";
        public static final String WITH_QUESTION_ID = ROOT + QUESTION_ID;
        public static final String UP_VOTE = ROOT + "/upvote/" + QUESTION_ID;
        public static final String DOWN_VOTE = ROOT + "/downvote/" + QUESTION_ID;
        public static final String HAS_VOTED_ON_QUESTION = ROOT + QUESTION_ID + "/users/{userId}";

    }
}
