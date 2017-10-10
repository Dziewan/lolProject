package sample;

import interfaces.Error;
import interfaces.Format;
import interfaces.Path;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import net.rithms.riot.api.*;
import net.rithms.riot.api.endpoints.runes.dto.RunePage;
import net.rithms.riot.api.endpoints.runes.dto.RunePages;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.util.Set;


public class Controller {
    final String apiKey = "RGAPI-1148c4cc-633b-4c19-96a8-2885d1d36214";
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
    ImageView division = new ImageView();

    public void initialize() {
        apiConfig = getApiConfig();
        gridPane.setBackground(getBackgroundImage());
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
        RunePages runePages = riotApi.getRunesBySummoner(platform, summoner.getId());
        Divisions divisions = new Divisions(platform, nick, apiKey);


        division.setImage(getDivisionImage(divisions.tierSolo));
        textArea.setText(generateData(divisions, runePages));
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

    private Background getBackgroundImage() {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(Path.PICS_PATH + "lol" + Format.PNG),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );
        return new Background(backgroundImage);
    }
}
