package sample;

import net.rithms.riot.constant.Platform;

public class Model {

    static String summonerName;

    static Platform platform;

    public static String getSummonerName() {
        return summonerName;
    }

    public static void setSummonerName(String summonerName) {
        Model.summonerName = summonerName;
    }

    public static Platform getPlatform() {
        return platform;
    }

    public static void setPlatform(Platform platform) {
        Model.platform = platform;
    }
}
