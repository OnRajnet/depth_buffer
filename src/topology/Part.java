package topology;

/**
 * Created by Intel on 25.02.2018.
 */
public class Part {

    private final PartType type;
    private final int startIndex;
    private final int count;
    private final boolean isTransfomable;

    public Part(PartType type, int startIndex, int count, boolean isTransfomable) {
        this.type = type;
        this.startIndex = startIndex;
        this.count = count;
        this.isTransfomable = isTransfomable;
    }

    public Part(PartType type, int startIndex, int count) {
        this(type, startIndex, count, true);
    }

    public PartType getType() {
        return type;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getCount() {
        return count;
    }

    public boolean isTransfomable() {
        return isTransfomable;
    }

}
