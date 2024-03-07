package fr.pentagon.ugeoverflow.algorithm.search;

import java.util.HashSet;
import java.util.Set;

public class CommonJavaSearchAlgorithm implements SearchAlgorithm{
    private final SearchAlgorithm searchAlgorithm;
    private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Set.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "float", "for", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native", "new", "package", "private",
            "protected", "public", "return", "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
    ));

    private static final Set<String> JAVA_CORE_NOTIONS = new HashSet<>(Set.of(
            "ArrayList", "LinkedList", "HashMap", "HashSet", "TreeMap", "TreeSet",
            "PriorityQueue", "Deque", "Stack", "Queue", "Vector",
            "Hashtable", "ConcurrentHashMap", "CopyOnWriteArrayList", "CopyOnWriteArraySet",
            "BlockingQueue", "ConcurrentLinkedQueue", "ArrayBlockingQueue", "LinkedBlockingQueue",
            "ConcurrentSkipListMap", "ConcurrentSkipListSet", "LinkedHashMap", "WeakHashMap",
            "IdentityHashMap", "Properties", "EnumSet", "EnumMap", "Collections", "Arrays",
            "Comparator", "Comparable", "Iterator", "Iterable", "List", "Set", "Map",
            "Stream", "Optional", "Function", "Predicate", "Consumer",
            "Supplier", "BiFunction", "BiConsumer", "BiPredicate", "UnaryOperator", "BinaryOperator",
            "StreamAPI", "LambdaExpressions", "FunctionalInterfaces", "concurrent"
    ));

    public CommonJavaSearchAlgorithm(SearchAlgorithm searchAlgorithm) {
        this.searchAlgorithm = searchAlgorithm;
    }

    @Override
    public int apply(String token) {
        int coefficient = 0;
        if(JAVA_KEYWORDS.contains(token)) {
            coefficient += 2;
        }
        if(JAVA_CORE_NOTIONS.contains(token)) {
            coefficient += 3;
        }
        return searchAlgorithm.apply(token) * coefficient;
    }
}
