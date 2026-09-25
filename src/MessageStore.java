import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Encapsulates knowledge and guarantees that messages stay distinct. */
public final class MessageStore {
    private final Set<String> messages = new LinkedHashSet<>();

    public boolean add(String message) {
        return messages.add(message);
    }

    public int addAll(Collection<String> newMessages) {
        int oldSize = messages.size();
        messages.addAll(newMessages);
        return messages.size() - oldSize;
    }

    public boolean remove(String message) {
        return messages.remove(message);
    }

    public void clear() {
        messages.clear();
    }

    public int size() {
        return messages.size();
    }

    public boolean contains(String message) {
        return messages.contains(message);
    }

    public Set<String> view() {
        return Collections.unmodifiableSet(messages);
    }

    public List<String> unknownTo(MessageStore other) {
        List<String> result = new ArrayList<>();
        for (String message : messages) {
            if (!other.contains(message)) {
                result.add(message);
            }
        }
        return result;
    }
}
