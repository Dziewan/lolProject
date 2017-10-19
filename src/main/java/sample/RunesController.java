package sample;

import com.sun.org.apache.xpath.internal.operations.Mod;
import interfaces.Default;
import interfaces.Format;
import interfaces.Path;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.LightBase;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.runes.dto.RunePage;
import net.rithms.riot.api.endpoints.runes.dto.RunePages;
import net.rithms.riot.api.endpoints.runes.dto.RuneSlot;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;
import sun.misc.IOUtils;

import javax.swing.text.Style;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class RunesController {

    final String apiKey = Default.API_KEY;
    static Model model;
    Summoner summoner;
    ApiConfig apiConfig;
    RiotApi riotApi;
    RunePages runePages;
    Set<RunePage> pages;
    Map<String, String> runeMap;

    @FXML GridPane runePane = new GridPane();

    @FXML ComboBox pageChooser = new ComboBox();

    public void initialize() throws FileNotFoundException {
        runeMap = initRuneMap();
        apiConfig = getApiConfig(apiKey);
        riotApi = new RiotApi(apiConfig);
        try {
            summoner = riotApi.getSummonerByName(model.getPlatform(), model.getSummonerName());
        } catch (RiotApiException e) {

        }
        pageChooser.setTranslateX(230);
        pageChooser.setTranslateY(330);
        runePane.setBackground(getBackgroundImage("emptyRunePage"));
        initRuneData();
    }

    private Map<String, String> initRuneMap() {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < Default.RUNE_ID.length; ++i) {
            map.put(Default.RUNE_ID[i], Default.RUNE_DESC[i]);
        }
        return map;
    }

    public void initRuneData() {
        try {
            runePages = riotApi.getRunesBySummoner(model.getPlatform(), summoner.getId());
            pages = runePages.getPages();
        } catch (RiotApiException e) {
            System.out.println(e.getErrorCode()+" "+e.getMessage());
        }
        List<RunePage> listOfPages = new ArrayList<>();
        for (RunePage runePage : pages) {
            pageChooser.getItems().add(runePage.getName());
        }
        listOfPages.addAll(pages);
        pageChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                choosePage(listOfPages);
            }
        });
    }

    public void choosePage(List<RunePage> listOfPages) {
        String chosenPage = pageChooser.getSelectionModel().getSelectedItem().toString();
        for (RunePage runePage : listOfPages) {
            if (chosenPage.equals(runePage.getName())) {
                try {
                    getRuneData(runePage);
                } catch (RiotApiException e) {
                    System.out.println(e.getErrorCode() + " " + e.getMessage());
                }
                break;
            }
        }
    }

    private void getRuneData(RunePage page) throws RiotApiException {
        List<RuneSlot> slots = new ArrayList<>();

        if (runePane.getChildren().size() > 1) {
            clearList();
        }

        runePane.setBackground(getBackgroundImage("runePage"));

        for (RuneSlot slot : page.getSlots()) {
            slots.add(slot);
        }

        Map<String, Integer> amount = new HashMap<>();

        for (int i = 0; i < slots.size(); ++i) {
            String id = slots.get(i).getRuneId() + "";
            if (!amount.containsKey(id)) {
                amount.put(id, 1);
            } else {
                amount.put(id, amount.get(id) + 1);
            }

            ImageView imageView = new ImageView(new Image(Path.RUNES_PATH + id + Format.PNG));
            imageView.prefWidth(10);
            imageView.prefHeight(10);
            imageView.setTranslateX(Default.RUNE_POSITIONS[slots.get(i).getRuneSlotId()].x);
            imageView.setTranslateY(Default.RUNE_POSITIONS[slots.get(i).getRuneSlotId()].y);
            runePane.getChildren().addAll(imageView);
        }

        int labX = 400;
        int labY = 240;
        List<Label> labels = new ArrayList<>();

        for (String id : amount.keySet()) {

            String description = runeMap.get(id);
            char[] desc = description.substring(1).replace(",", ".").toCharArray();
            int p = 0;
            StringBuilder value = new StringBuilder();
            while (Character.isDigit(desc[p]) || desc[p] == '.') value.append(desc[p++]);
            DecimalFormat df = new DecimalFormat("#,##");
            String val = ""+ Double.valueOf(df.format((amount.get(id) * Double.valueOf(value.toString()))));

            boolean toDeny = false;
            for (Label cur : labels) {
                if (cur.getText().contains(description.substring(p + 1))) {

                    String newVal = ""+ Double.valueOf(df.format((Double.valueOf(cur.getText().replace(",", ".")
                            .substring(1, 4)) + Double.valueOf(val.replace(",", ".")))));
                    cur.setText(description.charAt(0) + newVal + description.substring(p + 1));
                    toDeny = true;
                    break;
                }
            }
            if (toDeny) continue;

            Label label = new Label(description.charAt(0) + val + description.substring(p));
            label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            label.setTranslateX(labX);
            label.setTranslateY(labY);
            labels.add(label);

            labY += 20;
        }
        runePane.getChildren().addAll(labels);
    }

    private void clearList() {
        int toDelete = 0;
        for (int i = 0; i < runePane.getChildren().size(); ++i) {
            if (!runePane.getChildren().get(i).getTypeSelector().equals("ComboBox")) {
                toDelete = i;
                break;
            }
        }
        runePane.getChildren().remove(toDelete, runePane.getChildren().size());
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

    private ApiConfig getApiConfig(String apiKey) {
        return new ApiConfig().setKey(apiKey);
    }
}

