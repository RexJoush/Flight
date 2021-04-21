package entity;

public enum Week {
    Monday("Monday", 1),
    Tuesday("Tuesday", 2),
    Wednesday("Wednesday", 3),
    Thursday("Thursday", 4),
    Friday("Friday", 5),
    Saturday("Saturday", 6),
    Sunday("Sunday", 0);

    private String name;
    private int index;

    Week (String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index) {
        for (Week c : Week.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
