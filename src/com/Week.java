package com;

public enum Week {
    Monday("Monday", 1),
    monday("monday", 1),
    Tuesday("Tuesday", 2),
    tuesday("tuesday", 2),
    Wednesday("Wednesday", 3),
    wednesday("wednesday", 3),
    Thursday("Thursday", 4),
    thursday("thursday", 4),
    Friday("Friday", 5),
    friday("friday", 5),
    Saturday("Saturday", 6),
    saturday("saturday", 6),
    Sunday("Sunday", 0),
    sunday("sunday", 0);

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

    public static boolean contains(String value){
        for (Week c : Week.values()) {
            if (c.getName().equals(value)) {
                return true;
            }
        }
        return false;
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
