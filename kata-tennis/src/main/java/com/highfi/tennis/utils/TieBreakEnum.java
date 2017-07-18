package com.highfi.tennis.utils;

public enum TieBreakEnum {

	TIEBREAK_NULL(null),
	TIEBREAK_OK("OK"),	
	TIEBREAK_KO("KO");
	
	

    private final String tieBreak;

    /**
     * @param advantage
     */
    private TieBreakEnum(final String tieBreak) {
        this.tieBreak = tieBreak;
    }

    @Override
    public String toString() {
        return tieBreak;
    }
    
    public static TieBreakEnum fromValue(final TieBreakEnum tieBreakEnum) {
        for (TieBreakEnum my: TieBreakEnum.values()) {
            if (my.tieBreak.equals(tieBreakEnum)) {
                return my;
            }
        }
        return null;
    }
}
