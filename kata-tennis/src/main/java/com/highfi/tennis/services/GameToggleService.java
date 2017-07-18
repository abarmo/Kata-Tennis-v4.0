package com.highfi.tennis.services;

/**
 * Interface created in order to decide or identify whether the tiebreak game or normal scoring game to be played.
 * To be implemented by Services
 * 
 * @since Tuesday 18 July 2017 AT 11h00 PM.
 * @author Mohamed ABARCHIDS
 * 
 */
public interface GameToggleService {

	/**
	 * Run the Game with specific game toggle (TieBreak Or Normal Scoring)
	 */
	void run();

}