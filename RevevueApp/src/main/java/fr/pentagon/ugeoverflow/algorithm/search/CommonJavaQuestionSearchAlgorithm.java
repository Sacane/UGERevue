package fr.pentagon.ugeoverflow.algorithm.search;

import fr.pentagon.ugeoverflow.model.Question;

import java.util.HashSet;
import java.util.Set;

public class CommonJavaQuestionSearchAlgorithm implements QuestionSearchAlgorithm {
    private final QuestionSearchAlgorithm questionSearchAlgorithm;
    private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Set.of(
            "abstract", "abstraite", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "classe", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "float", "for", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native", "new", "package", "private",
            "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
            "thread", "heritage"
    ));

    private static final Set<String> JAVA_CORE_NOTIONS = new HashSet<>(Set.of(
            "arraylist", "linkedlist", "hashmap", "hashset", "treemap", "treeset",
            "priorityqueue", "deque", "stack", "queue", "vector",
            "hashtable", "concurrenthashmap", "copyonwritearraylist", "copyonwritearrayset",
            "blockingqueue", "concurrentlinkedqueue", "arrayblockingqueue", "linkedblockingqueue",
            "concurrentskiplistmap", "concurrentskiplistset", "linkedhashmap", "weakhashmap",
            "identityhashmap", "properties", "enumset", "enummap", "collections", "arrays",
            "comparator", "comparable", "iterator", "iterable", "list", "set", "map",
            "stream", "optional", "function", "predicate", "consumer",
            "supplier", "bifunction", "biconsumer", "bipredicate", "unaryoperator", "binaryoperator",
            "streamapi", "lambdaexpressions", "functionalinterfaces", "concurrent"
    ));

    public CommonJavaQuestionSearchAlgorithm(QuestionSearchAlgorithm questionSearchAlgorithm) {
        this.questionSearchAlgorithm = questionSearchAlgorithm;
    }

    @Override
    public int apply(String token, Question question) {
        int coefficient = 0;
        if(JAVA_KEYWORDS.contains(token)) {
            coefficient = 2;
        }
        if(JAVA_CORE_NOTIONS.contains(token)) {
            coefficient = 4;
        }
        return questionSearchAlgorithm.apply(token, question) + (100 * coefficient);
    }
}
