package com.bruinbeargames.ardenne;

public enum Phase {
	CARD_CLEANUP(false,true,false),
	ALLIED_CARD(true,true,true),
	GERMAN_CARD(false,true,true),
	GERMAN_ROLL_BRIDGE(false,true,false),
	GERMAN_PRE_MOVEMENT(false,true,true),
	GERMAN_MOVEMENT(false,true,true),
	GERMAN_POST_MOVEMENT(false,true,false),
	US_BARRAGE_DEFENSE(true,true,true),
	GERMAN_BARRAGE_ATTACK(false,true, true),
	GERMAN_BARRAGE_RESOLVE(false,false,true),
	GERMAN_BARRAGE_END(false,false,false),
	GERMAN_COMBAT(false,true,true),
	GERMAN_COMBAT_END(false,true,false),
	GERMAN_EXPLOTATION(false,true,true),
	GERMAN_POST_EXPLOTATION(false,true,false),
	GERMAN_SUPPLY(false,true,true),
	GERMAN_SUPPLY_END(false,true,false),
	GERMAN_END(false,false,false),
	ALLIED_PRE_MOVEMENT(true,true, true),
	ALLIED_REINFORCEMENT(true,true, false),
	ALLIED_MOVEMENT(true, true,false),
	ALLIED_POST_MOVEMENT(true, true,false),
	BRIDGE_GERMAN(false,true, false),
	BRIDGE_ALLIED(true,true, false),
	GERMAN_BARRAGE_DEFEND(false,true, true),
	ALLIED_BARRAGE_ATTACK(true,true,true),
	ALLIED_BARRAGE_RESOLVE(false,false,true),
	ALLIED_BARRAGE_END(false,false, false),
	ALLIED_COMBAT(true,true, true),
	ALLIED_COMBAT_END(true,true, false),
	ALLIED_EXPLOTATION(true,true, true),
	ALLIED_POST_EXPLOTATION(true,false, false),
	ALLIED_SUPPLY(true,false, true),
	ALLIED_END(false,false, false),
	NEXT_TURN(false,false, true),
	;

	/**
	 * each doSeaMove is associated with a side
	 */
		private final boolean isAlliedPhase;
		private final boolean isAIPhase;
		private final boolean isHelp;

		Phase(boolean isAllies, boolean isAI, boolean isHelp){
			isAlliedPhase = isAllies;
			isAIPhase = isAI;
			this.isHelp = isHelp;

		}
		public boolean isAllied() {
			return isAlliedPhase;
		}
		public boolean isAIPhase() {
			return isAIPhase;
		}
		public boolean isHelp(){
			return isHelp;
		}

}  

 