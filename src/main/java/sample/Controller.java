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

import java.util.*;


public class Controller {
    final String apiKey = "RGAPI-b48fe1fa-38b2-4bdd-acaa-b99ba65a4d4f";
    final Platform platform = Platform.EUNE;

    Summoner summoner;
    ApiConfig apiConfig;
    RiotApi riotApi;

    @FXML
    TextArea textArea = new TextArea();

    @FXML
    TextArea name = new TextArea();

    @FXML
    Button player = new Button();

    @FXML
    GridPane gridPane = new GridPane();

    @FXML
    GridPane gridPane2 = new GridPane();

    @FXML
    Label nck = new Label();

    @FXML
    Label dywizja = new Label();

    @FXML
    Label lev = new Label();

    @FXML
    ImageView firstChamp = new ImageView();

    @FXML
    ImageView secondChamp = new ImageView();

    @FXML
    ImageView thirdChamp = new ImageView();

    @FXML
    ImageView firstChampMastery = new ImageView();

    @FXML
    ImageView secondChampMastery = new ImageView();

    @FXML
    ImageView thirdChampMastery = new ImageView();

    @FXML
    ImageView divSolo = new ImageView();

    @FXML
    ImageView divFlex = new ImageView();

    @FXML
    ImageView divFlexTeam = new ImageView();

    @FXML
    Label soloName = new Label();

    @FXML
    Label flexName = new Label();

    @FXML
    Label teamName = new Label();

    public void initialize() throws RiotApiException {
        apiConfig = getApiConfig();
        gridPane.setBackground(getBackgroundImage("lol"));
        riotApi = new RiotApi(apiConfig);
        textArea.setEditable(false);

//        soloName.setText("  PLATINUM III");
//        flexName.setText("  GOLD IV");
//        teamName.setText("  UNRANKED");
    }

    public void getData() throws RiotApiException {
        if (name.getText().isEmpty()) {
            textArea.setText(Error.WRONG_NICKNAME_ERROR);
            return;
        }

        String nick = name.getText();
        summoner = riotApi.getSummonerByName(platform, nick);
        RunePages runePages = riotApi.getRunesBySummoner(platform, summoner.getId());
        Divisions divisions = new Divisions(platform, nick, apiKey);
        List<ChampionMastery> championMasteryList = riotApi.getChampionMasteriesBySummoner(platform, summoner.getId());
        List<net.rithms.riot.api.endpoints.static_data.dto.Champion> championList = getThreeBestChampions(championMasteryList);


        lev.setText(summoner.getSummonerLevel()+"");
        dywizja.setText(divisions.tierSolo+" "+divisions.rankSolo);
        nck.setText(nick);
        textArea.setText(generateData(divisions, runePages));

        firstChamp.setImage(getChampionIcon(championList.get(0).getName()));
        firstChampMastery.setImage(getMasteryImage(championMasteryList.get(0).getChampionLevel()));
        secondChamp.setImage(getChampionIcon(championList.get(1).getName()));
        secondChampMastery.setImage(getMasteryImage(championMasteryList.get(1).getChampionLevel()));
        thirdChamp.setImage(getChampionIcon(championList.get(2).getName()));
        thirdChampMastery.setImage(getMasteryImage(championMasteryList.get(2).getChampionLevel()));

        divSolo.setImage(getDivisionImage(divisions.tierSolo));
        divFlex.setImage(getDivisionImage(divisions.tierFlex));
        divFlexTeam.setImage(getDivisionImage(divisions.tierTeamFlex));

        soloName.setText("  "+divisions.tierSolo+" "+divisions.rankSolo);
        flexName.setText("  "+divisions.tierFlex+" "+divisions.rankFlex);
        teamName.setText("  "+divisions.tierTeamFlex+" "+divisions.rankTeamFlex);
        setSize();

    }

    private Image getChampionIcon(String championName) {
        return new Image(Path.CHAMP_ICON_URL + championName + Format.PNG);
    }

    private Image getMasteryImage(int level) {
        return new Image(Path.MASTERY_PATH + ""+level+ Format.PNG);
    }

    private Image getDivisionImage(String tier) {
        tier = tier.toUpperCase();
        return new Image(Path.DIV_PATH + tier + Format.PNG);
    }

    private String generateData(Divisions divisions, RunePages runePages) {
        String text = "Level : "+summoner.getSummonerLevel()+"\n"+"Nick : "+summoner.getName()+"\n";
        text += "\nRank Solo : "+divisions.tierSolo+" "+divisions.rankSolo+"\n";
        text += "Rank Flex : "+divisions.tierFlex+" "+divisions.rankFlex+"\n";
        text += "Rank Team Flex : "+divisions.tierTeamFlex+" "+divisions.rankTeamFlex+"\n";

        Set<RunePage> runePage = runePages.getPages();
        text += "\nStrony run \n\n";
        int i = 1;
        for (RunePage x : runePage) {
            text += i+". "+x.getName()+"\n";
            ++i;
        }
        return text;
    }

    private ApiConfig getApiConfig() {
        return new ApiConfig().setKey(apiKey);
    }

    private List<net.rithms.riot.api.endpoints.static_data.dto.Champion> getThreeBestChampions(List<ChampionMastery> championMasteryList) {
        List<net.rithms.riot.api.endpoints.static_data.dto.Champion> result = new ArrayList<>();
        if (championMasteryList != null) {
            for (int i = 0; i < 3; ++i) {
                net.rithms.riot.api.endpoints.static_data.dto.Champion champion = null;
                try {
                    champion = riotApi.getDataChampion(platform, championMasteryList.get(i).getChampionId());
                } catch (RiotApiException e) {
                    System.out.println(e.getErrorCode()+" "+e.getMessage());
                }
                result.add(champion);
            }
        }
        return result;
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
}
