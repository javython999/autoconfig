package memory;

public class Memory {

    private long used;
    private long free;

    public Memory(long used, long free) {
        this.used = used;
        this.free = free;
    }

    public long getUsed() {
        return used;
    }

    public long getFree() {
        return free;
    }

    @Override
    public String toString() {
        return "Memory{" +
                "used=" + used +
                ", free=" + free +
                '}';
    }
}
