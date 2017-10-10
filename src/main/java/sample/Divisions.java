package sample;

import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiAsync;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.league.constant.LeagueQueue;
import net.rithms.riot.api.endpoints.league.dto.LeaguePosition;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.api.request.AsyncRequest;
import net.rithms.riot.api.request.RequestAdapter;
import net.rithms.riot.api.request.RequestListener;
import net.rithms.riot.constant.Platform;

import java.util.Iterator;
import java.util.Set;

public class Divisions {

    String rankSolo;
    String tierSolo;

    String rankFlex;
    String tierFlex;

    String rankTeamFlex;
    String tierTeamFlex;

    private class ExtendedSummoner {
        public Summoner summoner;
        public LeaguePosition leagueSolo;
        public LeaguePosition leagueFlexSR;
        public LeaguePosition leagueFlexTT;

        private ExtendedSummoner() {
        }
    }

    public Divisions(Platform platform, String nick, String key) throws RiotApiException {
        ApiConfig config = (new ApiConfig()).setKey(key);
        RiotApi riotApi = new RiotApi(config);
        RiotApiAsync riotApiAsync = riotApi.getAsyncApi();

        Summoner summon = riotApi.getSummonerByName(platform, nick);
        long id = summon.getId();

        final Divisions.ExtendedSummoner extendedSummoner = new Divisions.ExtendedSummoner();

        AsyncRequest requestSummoner = riotApiAsync.getSummoner(platform, id);
        requestSummoner.addListeners(new RequestListener[]{new RequestAdapter() {
            @Override
            public void onRequestSucceeded(AsyncRequest request) {
                extendedSummoner.summoner = (Summoner) request.getDto();
            }
        }});

        AsyncRequest requestLeague = riotApiAsync.getLeaguePositionsBySummonerId(platform, id);
        requestLeague.addListeners(new RequestListener[]{new RequestAdapter() {
            @Override
            public void onRequestSucceeded(AsyncRequest request) {
                Set<LeaguePosition> leaguePositions = (Set) request.getDto();
                if (leaguePositions != null && !leaguePositions.isEmpty()) {
                    Iterator iterator = leaguePositions.iterator();

                    while (iterator.hasNext()) {
                        LeaguePosition leaguePosition = (LeaguePosition) iterator.next();
                        if (leaguePosition.getQueueType().equals(LeagueQueue.RANKED_SOLO_5x5.name())) {
                            extendedSummoner.leagueSolo = leaguePosition;
                        } else if (leaguePosition.getQueueType().equals(LeagueQueue.RANKED_FLEX_SR.name())) {
                            extendedSummoner.leagueFlexSR = leaguePosition;
                        } else if (leaguePosition.getQueueType().equals(LeagueQueue.RANKED_FLEX_TT.name())) {
                            extendedSummoner.leagueFlexTT = leaguePosition;
                        }
                    }
                }
            }
        }});

        try {
            riotApiAsync.awaitAll();
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        if (extendedSummoner.leagueSolo != null && extendedSummoner.leagueSolo.getTier() != null) {
            rankSolo = extendedSummoner.leagueSolo.getRank();
            tierSolo = extendedSummoner.leagueSolo.getTier();
        } else {
            rankSolo = "";
            tierSolo = "Unranked";
        }

        if (extendedSummoner.leagueFlexSR != null && extendedSummoner.leagueFlexSR.getTier() != null) {
            rankFlex = extendedSummoner.leagueFlexSR.getRank();
            tierFlex = extendedSummoner.leagueFlexSR.getTier();
        } else {
            rankFlex = "";
            tierFlex = "Unranked";
        }

        if (extendedSummoner.leagueFlexTT != null && extendedSummoner.leagueFlexTT.getTier() != null) {
            rankTeamFlex = extendedSummoner.leagueFlexTT.getRank();
            tierTeamFlex = extendedSummoner.leagueFlexTT.getTier();
        } else {
            rankTeamFlex = "";
            tierTeamFlex = "Unranked";
        }
    }
}
