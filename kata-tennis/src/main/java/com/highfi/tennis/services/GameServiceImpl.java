package com.highfi.tennis.services;

import static com.highfi.tennis.utils.GameMessage.currentScrors;
import static com.highfi.tennis.utils.GameMessage.gameStatus;
import static com.highfi.tennis.utils.GameMessage.matchStatus;
import static com.highfi.tennis.utils.GameMessage.oldScors;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.highfi.tennis.player.Player;
import com.highfi.tennis.utils.AdvantageEnum;
import com.highfi.tennis.utils.GameMessage;
import com.highfi.tennis.utils.GamePlayerPosition;
import com.highfi.tennis.utils.ScoresEnum;


/**
 * Class Service of Management rules of KATA TENNIS
 *
 * @author Mohamed ABARCHID
 * @since Saturday 08 July 2017 AT 03h00 PM.
 */
public class GameServiceImpl implements GameService {

	private int tieBreak = 0;
	
	/**
	 * Initialize the game tip , if players are inside TieBreak so call : tiebreak updates if not : so call scoring updates
	 * 
     * @param listPlayers    is the list of players
     * @param winnerPosition position of the winner player in the list @param listPlayers
     * @param looserPosition position of the looser player in the list @param listPlayers
	 */
	@Override
	public void initGameTip(List<Player> listPlayers, GamePlayerPosition winnerPosition, GamePlayerPosition looserPosition){

		GameToggleService  callUpdateScroresService = new GameToggleService() {	    			
			@Override
			public void run() { callUpdateScrores(listPlayers, winnerPosition.getFixedPosition(), looserPosition.getFixedPosition()); }
		};
		
		GameToggleService  callUpdateTieBreakService = new GameToggleService() {	    			
			@Override
			public void run() { callUpdateTieBreak(listPlayers, winnerPosition.getFixedPosition(), looserPosition.getFixedPosition()); }
		};
		
		GameMessage.gameToggle.put(0, callUpdateScroresService);
		GameMessage.gameToggle.put(1, callUpdateTieBreakService);
	
    }
	
	
    /**
     * MASTER IMPORTANTE METHODE FOR NORMAL SCORING :
     * Calculate Sets and determine if a Player wins the opening Match or not yet.
     *
     * @param listPlayers    is the list of players
     * @param winnerPosition position of the winner player in the list @param listPlayers
     * @param looserPosition position of the looser player in the list @param listPlayers
     */
    private void updateScores(List<Player> listPlayers, int winnerPosition, int looserPosition) {
        listPlayers.get(winnerPosition).setGames(listPlayers.get(winnerPosition).getGames() + 1);
        initScoresAndAdventages(listPlayers);
        StringBuilder currentScores = updateDisplay(listPlayers);
        /** Player wins a set while his last games are 6-4  or 7-5 */
        if (listPlayers.get(winnerPosition).getGames() > (listPlayers.get(looserPosition).getGames() + 1) && (listPlayers.get(winnerPosition).getGames() == 6 || listPlayers.get(winnerPosition).getGames() == 7)) {
            listPlayers.get(winnerPosition).setSets(listPlayers.get(winnerPosition).getSets() + 1);
            setMatchWinner(listPlayers, winnerPosition, looserPosition);
            listPlayers.get(winnerPosition).setGames(0);
            listPlayers.get(looserPosition).setGames(0);
            updateOldDisplay(currentScores);
            /** We initialize the old current earned Set from scratch so to open another new Set */
            currentScores.setLength(0);        
        } 
    }


	
    /**
     * MASTER IMPORTANTE METHODE FOR TIEBREAK SCORING :
     * Calculate tieBreaks scoring and determine if a Player wins the opening Match or not yet.
     *
     * @param listPlayers    is the list of players
     * @param winnerPosition position of the winner player in the list @param listPlayers
     * @param looserPosition position of the looser player in the list @param listPlayers
     */
    private void callUpdateTieBreak(List<Player> listPlayers, int winnerPosition, int looserPosition) {
        if( tieBreak != 0 ) {
    	    int winnerTieBreak = listPlayers.get(winnerPosition).getTieBreak();   
    	    int looserTieBreak = listPlayers.get(looserPosition).getTieBreak();
    		listPlayers.get(winnerPosition).setTieBreak(winnerTieBreak+1);      
    	    winnerTieBreak = listPlayers.get(winnerPosition).getTieBreak();   
    	    StringBuilder currentScores = updateDisplay(listPlayers);
        	if( (winnerTieBreak > looserTieBreak+1) && winnerTieBreak >= 7 ) {
                listPlayers.get(winnerPosition).setGames(listPlayers.get(winnerPosition).getGames() + 1);
                initScoresAndAdventages(listPlayers);
                listPlayers.get(winnerPosition).setSets(listPlayers.get(winnerPosition).getSets() + 1);
                setMatchWinner(listPlayers, winnerPosition, looserPosition);
                listPlayers.get(winnerPosition).setTieBreak(0);
                listPlayers.get(looserPosition).setTieBreak(0);
                updateDisplay(listPlayers);
                listPlayers.get(winnerPosition).setGames(0);
                listPlayers.get(looserPosition).setGames(0);
                updateOldDisplay(currentScores);
                /** We initialize the old current earned Set from scratch so to open another new Set */
                currentScores.setLength(0);
                setTieBreak(0);
        	}
        }
	}

    /**
     * Rule management : if a player wins 3 sets, he wins the complete match
     **/
    private void setMatchWinner(List<Player> listPlayers, int winnerPosition, int looserPosition) {
        if (listPlayers.get(winnerPosition).getSets() == 3) {
            listPlayers.get(winnerPosition).setMatcheWinner(true);
            matchStatus.setLength(0);
            matchStatus.append("Player  ");
            matchStatus.append(listPlayers.get(0).isMatcheWinner() == true ? listPlayers.get(0).getName() : listPlayers.get(1).getName());
            matchStatus.append(" wins.");
        }
    }
    
    
	/**
     * Initialize only Player Scores, Advantages, SetWinner
     *
     * @param listPlayers is a list of player which contains tow players by order,
     *                    in order to conserve the display results of each Player.
     */
    private void initScoresAndAdventages(List<Player> listPlayers) {
        listPlayers.get(0).setScores(ScoresEnum.SCORE_00);
        listPlayers.get(1).setScores(ScoresEnum.SCORE_00);
        listPlayers.get(0).setAdvantage(AdvantageEnum.ADV_NULL);
        listPlayers.get(1).setAdvantage(AdvantageEnum.ADV_NULL);
        listPlayers.get(0).setWins(false);
        listPlayers.get(1).setWins(false);
        
    }

    /**
     * Display result of the Match
     *
     * @param listPlayers is a list of player which contains tow players by order,
     *                    in order to conserve the display results of each Player.
     */
    private StringBuilder updateDisplay(List<Player> listPlayers) {
        currentScrors.setLength(0);
        currentScrors.append("(");
        currentScrors.append(listPlayers.get(0).getGames());
        currentScrors.append("-");
        currentScrors.append(listPlayers.get(1).getGames());
        currentScrors.append(")");

        gameStatus.setLength(0);
        if(tieBreak != 0) {
            gameStatus.append(listPlayers.get(0).getTieBreak());
            gameStatus.append("-");
            gameStatus.append(listPlayers.get(1).getTieBreak());
        } else if (listPlayers.get(0).getAdvantage().equals(AdvantageEnum.ADV_OK) || listPlayers.get(1).getAdvantage().equals(AdvantageEnum.ADV_OK)) {
            gameStatus.append("Advantage for ");
            gameStatus.append(listPlayers.get(0).getAdvantage().equals(AdvantageEnum.ADV_OK) ? listPlayers.get(0).getName() : listPlayers.get(1).getName());
        } else if(listPlayers.get(0).getScores() == ScoresEnum.SCORE_40 && listPlayers.get(1).getScores() == ScoresEnum.SCORE_40) {
       	    gameStatus.append(" Deuce ");
        } else {
            gameStatus.append(listPlayers.get(0).getScores().value);
            gameStatus.append("-");
            gameStatus.append(listPlayers.get(1).getScores().value);
        }

        return currentScrors;
    }

    public void updateOldDisplay(StringBuilder currentScrors) {
        oldScors.append(currentScrors);
    }


    /**
     * Calculate scores by tour.
     *
     * @param listPlayers is a list of player which contains tow players by order,
     *                    in order to conserve the display results of each Player.
     * @throws Exception 
     */
    private void callUpdateScrores(List<Player> listPlayers, int winnerPosition, int looserPosition) {
    	ScoresEnum winnerScore = listPlayers.get(winnerPosition).getScores();
        switch (winnerScore) {
            case SCORE_00:
                listPlayers.get(winnerPosition).setScores(ScoresEnum.SCORE_15);
        		updateDisplay(listPlayers);
                break;
            case SCORE_15:
                listPlayers.get(winnerPosition).setScores(ScoresEnum.SCORE_30);
        		updateDisplay(listPlayers);
                break;
            case SCORE_30:
                listPlayers.get(winnerPosition).setScores(ScoresEnum.SCORE_40);
        		updateDisplay(listPlayers);
                break;
            case SCORE_40:
                verifyAdvantages(listPlayers, winnerPosition, looserPosition);
                break;
    		default: 		 
   			    break;
        }		
	}



	/**
     * To be Optimized, because there is many conditional operators if/else
     * Determine which Player has an Advantage
     *
     * @param listPlayers is a list of player which contains tow players by order,
     *                    in order to conserve the display results of each Player.
     */
    private void verifyAdvantages(List<Player> listPlayers, int winnerPosition, int looserPosition) {
    	updateAdvantages(listPlayers, winnerPosition, looserPosition);
    	AdvantageEnum winnerAdventage = listPlayers.get(winnerPosition).getAdvantage();
		switch (winnerAdventage) {
		case ADV_NULL:
			 updateScores(listPlayers, winnerPosition, looserPosition);
			 break;		
		case ADV_KO:
			 listPlayers.get(winnerPosition).setAdvantage(AdvantageEnum.ADV_OK);
			 /**Lose The advantage even if he does not have an advantage before*/
			 listPlayers.get(looserPosition).setAdvantage(AdvantageEnum.ADV_KO);
			 updateDisplay(listPlayers);
			 break;
		case ADV_OK:
			 updateScores(listPlayers, winnerPosition, looserPosition);
			 break;
		default:		
			 break;
		}
		if( (listPlayers.get(winnerPosition).getGames() == 6 && listPlayers.get(looserPosition).getGames() == 6) ) {
			setTieBreak(1);
		}
    }

    /**
     * Update the advantage parameters of every Player. Mark both of the two players 
     * like as they were loosing their advantages even if they never get an advantage before
     *
     * @param listPlayers
     * @param winnerPosition
     * @param looserPosition
     */
    private void updateAdvantages(List<Player> listPlayers, int winnerPosition, int looserPosition) {
    	if (listPlayers.get(winnerPosition).getScores() == listPlayers.get(looserPosition).getScores() && isEqAdvantages(listPlayers, winnerPosition, looserPosition) ) {
	    	listPlayers.get(winnerPosition).setAdvantage(AdvantageEnum.ADV_KO);
	    	listPlayers.get(looserPosition).setAdvantage(AdvantageEnum.ADV_KO);
    	}
    }
    
    private boolean isEqAdvantages(List<Player> listPlayers, int winnerPosition, int looserPosition) {
    	return listPlayers.get(winnerPosition).getAdvantage().equals(listPlayers.get(looserPosition).getAdvantage()) ? true: false;
    }

    /**
     * Print current fighting Set by games
     */
    @Override
    public String displayCurrentScores() {
        return currentScrors.toString();
    }

    /**
     * Print old wined Sets
     */
    @Override
    public String displayOldScores() {
        return oldScors.toString();
    }

    @Override
    public void determineScoreWinnerAndScoreLooser(List<Player> listPlayers, String in) {
        if (StringUtils.equals(in, "a")) {
            listPlayers.get(0).setWins(true);
            listPlayers.get(1).setWins(false);
        } else {
            listPlayers.get(0).setWins(false);
            listPlayers.get(1).setWins(true);
        }
    }

    @Override
    public GamePlayerPosition getWinnerPosition(List<Player> listPlayers) {
        return listPlayers.stream().filter(player -> player.isWins()).findFirst().orElse(null).getGamePlayerPosition();
    }

    @Override
    public GamePlayerPosition getLooserPosition(List<Player> listPlayers) {
        return listPlayers.stream().filter(player -> !player.isWins()).findFirst().orElse(null).getGamePlayerPosition();
    }

    @Override
    public Boolean isGameOver(List<Player> listPlayers) {
        if (listPlayers.get(0).isMatcheWinner() || listPlayers.get(1).isMatcheWinner()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
	public int getTieBreak() {
		return tieBreak;
	}

	private void setTieBreak(int tieBreak) {
		this.tieBreak = tieBreak;
	}
 
}