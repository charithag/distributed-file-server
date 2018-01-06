package org.dc.file.search;

public final class Constants {
    private static final String[] FILES = {
            "Adventures of Tintin",
            "Jack and Jill",
            "Glee",
            "The Vampire Diarie",
            "King Arthur",
            "Windows XP",
            "Harry Potter",
            "Kung Fu Panda",
            "Lady Gaga",
            "Twilight",
            "Windows 8",
            "Mission Impossible",
            "Turn Up The Music",
            "Super Mario",
            "American Pickers",
            "Microsoft Office 2010",
            "Happy Feet",
            "Modern Family",
            "American Idol",
            "Hacking for Dummies"
    };

    public static String[] getFiles(){
        return FILES.clone();
    }

    public static class MessageType{
        public static final String REG = "REG";
        public static final String REGOK = "REGOK";
        public static final String UNREG = "UNREG";
        public static final String RATE = "RATE";
        public static final String UNROK = "UNROK";
        public static final String JOIN = "JOIN";
        public static final String JOINOK = "JOINOK";
        public static final String LEAVE = "LEAVE";
        public static final String LEAVEOK = "LEAVEOK";
        public static final String SER = "SER";
        public static final String SEROK = "SEROK";
    }
}
