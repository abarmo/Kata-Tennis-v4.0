package com.highfi.tennis.services;

import com.highfi.tennis.player.Player;
import com.highfi.tennis.utils.GamePlayerPosition;

import java.util.List;

/**
 * Interface to be implemented by Services
 * 
 * @since Saturday 08 July 2017 AT 03h00 PM.
 * @author Mohamed ABARCHIDS
 * 
 */
public interface GameService {
			
	 void determineScoreWinnerAndScoreLooser(List<Player> listPlayers, String in) ;
	
	 GamePlayerPosition getWinnerPosition(List<Player> listPlayers) ;
	
	 GamePlayerPosition getLooserPosition(List<Player> listPlayers) ;

	 String displayCurrentScores();

	 String displayOldScores();
 
	 Boolean isGameOver(List<Player> listPlayers);

	 int getTieBreak();
 
	 void initGameTip(List<Player> listPlayers, GamePlayerPosition winnerPosition, GamePlayerPosition looserPosition);

}