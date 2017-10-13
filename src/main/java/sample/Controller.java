package sample;

import interfaces.Default;
import interfaces.Error;
import interfaces.Format;
import interfaces.Path;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import net.rithms.riot.api.*;
import net.rithms.riot.api.endpoints.champion_mastery.dto.ChampionMastery;
import net.rithms.riot.api.endpoints.runes.dto.RunePage;
import net.rithms.riot.api.endpoints.runes.dto.RunePages;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Controller {
    final String apiKey = "RGAPI-5e176569-ed52-4910-954d-e400db3aa8f5";
    final Platform platform = Platform.EUNE;

    Summoner summoner;
    ApiConfig apiConfig;
    RiotApi riotApi;

    @FXML GridPane gridPane = new GridPane();
    @FXML GridPane gridPane2 = new GridPane();

    @FXML TextArea textArea = new TextArea();
    @FXML TextArea name = new TextArea();

    @FXML Button player = new Button();

    @FXML Label nck = new Label();
    @FXML Label dywizja = new Label();
    @FXML Label lev = new Label();
    @FXML Label soloName = new Label();
    @FXML Label flexName = new Label();
    @FXML Label teamName = new Label();

    @FXML ImageView firstChamp = new ImageView();
    @FXML ImageView secondChamp = new ImageView();
    @FXML ImageView thirdChamp = new ImageView();
    @FXML ImageView firstChampMastery = new ImageView();
    @FXML ImageView secondChampMastery = new ImageView();
    @FXML ImageView thirdChampMastery = new ImageView();
    @FXML ImageView divSolo = new ImageView();
    @FXML ImageView divFlex = new ImageView();
    @FXML ImageView divFlexTeam = new ImageView();

    public void initialize() throws RiotApiException {
        apiConfig = getApiConfig(apiKey);
        gridPane.setBackground(getBackgroundImage("lol"));
        riotApi = new RiotApi(apiConfig);
        textArea.setEditable(false);
    }

    public void getData() throws RiotApiException {
        if (name.getText().isEmpty()) {
            textArea.setText(Error.WRONG_NICKNAME_ERROR);
            return;
        }

        String nick = name.getText();
        summoner = riotApi.getSummonerByName(platform, nick);
        Divisions divisions = new Divisions(platform, nick, apiKey);
        List<ChampionMastery> championMasteryList = riotApi.getChampionMasteriesBySummoner(platform, summoner.getId());
        List<String> championList = getThreeBestChampions(championMasteryList);


        lev.setText(summoner.getSummonerLevel()+"");
        dywizja.setText(divisions.tierSolo+" "+divisions.rankSolo);
        nck.setText(nick);
        textArea.setText(generateData(divisions));

        firstChamp.setImage(getChampionIcon(championList.get(0)));
        if (championMasteryList.get(0).getChampionLevel() >= 5) firstChampMastery.setImage(getMasteryImage(championMasteryList.get(0).getChampionLevel()));
        secondChamp.setImage(getChampionIcon(championList.get(1)));
        if (championMasteryList.get(1).getChampionLevel() >= 5) secondChampMastery.setImage(getMasteryImage(championMasteryList.get(1).getChampionLevel()));
        thirdChamp.setImage(getChampionIcon(championList.get(2)));
        if (championMasteryList.get(2).getChampionLevel() >= 5) thirdChampMastery.setImage(getMasteryImage(championMasteryList.get(2).getChampionLevel()));

        divSolo.setImage(getDivisionImage(divisions.tierSolo));
        divFlex.setImage(getDivisionImage(divisions.tierFlex));
        divFlexTeam.setImage(getDivisionImage(divisions.tierTeamFlex));

        soloName.setText("  "+divisions.tierSolo+" "+divisions.rankSolo);
        flexName.setText("  "+divisions.tierFlex+" "+divisions.rankFlex);
        teamName.setText("  "+divisions.tierTeamFlex+" "+divisions.rankTeamFlex);
        setSize();

    }

    private Image getChampionIcon(String championName) {
        String champ = championName.toLowerCase().replace("'", "").replace(" ", "");
        return new Image(Path.CHAMP_ICON_PATH + champ + Format.PNG);
    }

    private Image getMasteryImage(int level) {
        return new Image(Path.MASTERY_PATH + ""+level+ Format.PNG);
    }

    private Image getDivisionImage(String tier) {
        tier = tier.toUpperCase();
        return new Image(Path.DIV_PATH + tier + Format.PNG);
    }

    private String generateData(Divisions divisions) {
        String text = "Level : "+summoner.getSummonerLevel()+"\n"+"Nick : "+summoner.getName()+"\n";
        text += "\nRank Solo : "+divisions.tierSolo+" "+divisions.rankSolo+"\n";
        text += "Rank Flex : "+divisions.tierFlex+" "+divisions.rankFlex+"\n";
        text += "Rank Team Flex : "+divisions.tierTeamFlex+" "+divisions.rankTeamFlex+"\n";

        return text;
    }

    private ApiConfig getApiConfig(String apiKey) {
        return new ApiConfig().setKey(apiKey);
    }

    private List<String> getThreeBestChampions(List<ChampionMastery> championMasteryList) {
        return new ArrayList<>(Arrays.asList(
                getChampionNameById(championMasteryList.get(0).getChampionId()),
                getChampionNameById(championMasteryList.get(1).getChampionId()),
                getChampionNameById(championMasteryList.get(2).getChampionId())
        ));
    }

    private Background getBackgroundImage(String name) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(Path.PICS_PATH + name + Format.PNG),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );
        return new Background(backgroundImage);
    }

    private void setSize() {
        firstChamp.setFitWidth(Default.CHAMP_SIZE);
        firstChamp.setFitHeight(Default.CHAMP_SIZE);

        firstChampMastery.setFitWidth(Default.CHAMP_SIZE);
        firstChampMastery.setFitHeight(Default.CHAMP_SIZE);

        secondChamp.setFitWidth(Default.CHAMP_SIZE);
        secondChamp.setFitHeight(Default.CHAMP_SIZE);

        secondChampMastery.setFitWidth(Default.CHAMP_SIZE);
        secondChampMastery.setFitHeight(Default.CHAMP_SIZE);

        thirdChamp.setFitWidth(Default.CHAMP_SIZE);
        thirdChamp.setFitHeight(Default.CHAMP_SIZE);

        thirdChampMastery.setFitWidth(Default.CHAMP_SIZE);
        thirdChampMastery.setFitHeight(Default.CHAMP_SIZE);

        divSolo.setFitWidth(Default.DIV_SIZE);
        divSolo.setFitHeight(Default.DIV_SIZE);

        divFlex.setFitWidth(Default.DIV_SIZE);
        divFlex.setFitHeight(Default.DIV_SIZE);

        divFlexTeam.setFitWidth(Default.DIV_SIZE);
        divFlexTeam.setFitHeight(Default.DIV_SIZE);
    }

    private String getChampionNameById(int id) {
        switch (id) {
            case 266: return "Aatrox";
            case 412: return "Thresh";
            case 23: return "Tryndamere";
            case 79: return "Gragas";
            case 69: return "Cassiopeia";
            case 136: return "Aurelion Sol";
            case 13: return "Ryze";
            case 78: return "Poppy";
            case 14: return "Sion";
            case 1: return "Annie";
            case 202: return "Jhin";
            case 43: return "Karma";
            case 111: return "Nautilus";
            case 240: return "Kled";
            case 99: return "Lux";
            case 103: return "Ahri";
            case 2: return "Olaf";
            case 112: return "Viktor";
            case 34: return "Anivia";
            case 27: return "Singed";
            case 86: return "Garen";
            case 127: return "Lissandra";
            case 57: return "Maokai";
            case 25: return "Morgana";
            case 28: return "Evelynn";
            case 105: return "Fizz";
            case 74: return "Heimerdinger";
            case 238: return "Zed";
            case 68: return "Rumble";
            case 82: return "Mordekaiser";
            case 37: return "Sona";
            case 96: return "Kog'Maw";
            case 55: return "Katarina";
            case 117: return "Lulu";
            case 22: return "Ashe";
            case 30: return "Karthus";
            case 12: return "Alistar";
            case 122: return "Darius";
            case 67: return "Vayne";
            case 110: return "Varus";
            case 77: return "Udyr";
            case 89: return "Leona";
            case 126: return "Jayce";
            case 134: return "Syndra";
            case 80: return "Pantheon";
            case 92: return "Riven";
            case 121: return "Kha'Zix";
            case 42: return "Corki";
            case 268: return "Azir";
            case 51: return "Caitlyn";
            case 76: return "Nidalee";
            case 85: return "Kennen";
            case 3: return "Galio";
            case 45: return "Veigar";
            case 432: return "Bard";
            case 150: return "Gnar";
            case 90: return "Malzahar";
            case 104: return "Graves";
            case 254: return "Vi";
            case 10: return "Kayle";
            case 39: return "Irelia";
            case 64: return "Lee Sin";
            case 420: return "Illaoi";
            case 60: return "Elise";
            case 106: return "Volibear";
            case 20: return "Nunu";
            case 4: return "Twisted Fate";
            case 24: return "Jax";
            case 102: return "Shyvana";
            case 429: return "Kalista";
            case 36: return "Dr. Mundo";
            case 427: return "Ivern";
            case 131: return "Diana";
            case 223: return "Tahm Kench";
            case 63: return "Brand";
            case 113: return "Sejuani";
            case 8: return "Vladimir";
            case 154: return "Zac";
            case 421: return "Rek'Sai";
            case 133: return "Quinn";
            case 84: return "Akali";
            case 163: return "Taliyah";
            case 18: return "Tristana";
            case 120: return "Hecarim";
            case 15: return "Sivir";
            case 236: return "Lucian";
            case 107: return "Rengar";
            case 19: return "Warwick";
            case 72: return "Skarner";
            case 54: return "Malphite";
            case 157: return "Yasuo";
            case 101: return "Xerath";
            case 17: return "Teemo";
            case 75: return "Nasus";
            case 58: return "Renekton";
            case 119: return "Draven";
            case 35: return "Shaco";
            case 50: return "Swain";
            case 91: return "Talon";
            case 40: return "Janna";
            case 115: return "Ziggs";
            case 245: return "Ekko";
            case 61: return "Orianna";
            case 114: return "Fiora";
            case 9: return "Fiddlesticks";
            case 31: return "Cho'Gath";
            case 33: return "Rammus";
            case 7: return "LeBlanc";
            case 16: return "Soraka";
            case 26: return "Zilean";
            case 56: return "Nocturne";
            case 222: return "Jinx";
            case 83: return "Yorick";
            case 6: return "Urgot";
            case 203: return "Kindred";
            case 21: return "Miss Fortune";
            case 62: return "Wukong";
            case 53: return "Blitzcrank";
            case 98: return "Shen";
            case 201: return "Braum";
            case 5: return "Xin Zhao";
            case 29: return "Twitch";
            case 11: return "Master Yi";
            case 44: return "Taric";
            case 32: return "Amumu";
            case 41: return "Gangplank";
            case 48: return "Trundle";
            case 38: return "Kassadin";
            case 161: return "Vel'Koz";
            case 143: return "Zyra";
            case 267: return "Nami";
            case 59: return "Jarvan IV";
            case 81: return "Ezreal";
        }
        return "";
    }
}
