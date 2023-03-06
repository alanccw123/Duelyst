package events;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import utils.StaticConfFiles;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.EffectAnimation;
import structures.Board;
import utils.BasicObjectBuilders;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.AttackChecker;
import utils.MovementChecker;
import views.html.defaultpages.todo;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		Board board = gameState.getGameBoard();
		
		Tile clicked = board.getTile(tilex, tiley);

		// do nothing if it is not player's turn
		if (!gameState.isPlayerTurn()) {
			return;
		}
		
		// do nothing when some moving animation is playing
		if (!gameState.isReady()) {
			return;
		}
		
		
		//the user clicks on a new tile
		if (gameState.unitLastClicked == null && gameState.cardLastClicked == null) {
			
			if (clicked.isHasUnit()) {

				// player clicks on an unit
				Unit selected = clicked.getUnit();

				// cannot operate on AI's units
				// if (selected.getPlayer() != 1) {
				// 	return;
				// }


				// check if the unit has movement action left
				if (selected.canMove()) {
					// generate lists of tiles for movement & attack
					List<Tile> range = MovementChecker.checkMovement(clicked, board); // tiles that the unit can move to
					List<Tile> attackable = AttackChecker.checkAllAttackRange(range, board, selected.getPlayer()); // tiles (with enemy unit) that can be attacked from tiles within movement range
					attackable.addAll(AttackChecker.checkAttackRange(clicked, board, selected.getPlayer()));// plus those that can be attacked from the unit current location

					// highlight the tiles for movement in white
					for (Tile tile : range) {
						BasicCommands.drawTile(out, tile, 1);
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						gameState.highlightedForMovement.add(tile);
					}
					
					// highlight the tiles for attack in red
					for (Tile tile : attackable) {
						BasicCommands.drawTile(out, tile, 2);
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						gameState.highlightedForAttack.add(tile);
					}

					// keep tracked of the unit selected
					gameState.unitLastClicked = selected;
					String debug = String.format("selected Unit id:%d x%d y%d", selected.getId(), clicked.getTilex(), clicked.getTiley());
					BasicCommands.addPlayer1Notification(out, debug, 2);

				// if the unit has moved but not yet attacked
				}else if (selected.canAttack()) {
					// only check 1 tile surrounding for target
					List<Tile> attackable = AttackChecker.checkAttackRange(clicked, board, selected.getPlayer());

					// should not select the unit if there is no valid target
					if (attackable.isEmpty()) {
						return;
					}

					// highlight the tiles for attack in red
					for (Tile tile : attackable) {
						BasicCommands.drawTile(out, tile, 2);
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						gameState.highlightedForAttack.add(tile);
					}

					// keep tracked of the unit selected
					gameState.unitLastClicked = selected;
	

					String debug = String.format("selected Unit id:%d x%d y%d", selected.getId(), clicked.getTilex(), clicked.getTiley());
					BasicCommands.addPlayer1Notification(out, debug, 2);
				}
				
				
			}
			
		// else if the player last selected a card
		}else if (gameState.cardLastClicked != null) {
			// play the card if the tile clicked is valid
			if (gameState.cardLastClicked.getCardname().equals("Truestrike")) {
                if (clicked.isHasUnit()) {
                    Unit selected = clicked.getUnit();
                    for (Unit u : gameState.getAIUnits()) {
                        if (u.getId() == (selected.getId())) {
                        	BasicCommands.addPlayer1Notification(out, String.format("Play card id: %d", gameState.cardLastClicked.getId()), 1);
                        	gameState.clearhighlight(out);
                            int index = gameState.getCardPosition(gameState.cardLastClicked);
                            gameState.removePlayerCard(index);
                            gameState.displayHand(out);
                            if (selected.getHealth() > 2) {
                                selected.setHealth(selected.getHealth() - 2);
                                EffectAnimation inmolation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
                                BasicCommands.playEffectAnimation(out, inmolation, clicked);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                BasicCommands.setUnitHealth(out, selected, selected.getHealth());
                            } else {
                                selected.setHealth(0);
                                gameState.unitTakeDamage(selected, out, 0);
                            }
                            if (selected.getId() == 100) {
                                gameState.setAiHealth(selected.getHealth());
                                BasicCommands.setPlayer2Health(out, gameState.getAi());
                            }
                            gameState.getPlayer().setMana(gameState.getPlayer().getMana() - gameState.cardLastClicked.getManacost());
                            BasicCommands.setPlayer1Mana(out, gameState.getPlayer());
                            gameState.cardLastClicked = null;
                        }
                    }
                }      		
			}else if(gameState.cardLastClicked.getCardname().equals("Sundrop Elixir")) {
				if(clicked.isHasUnit()) {
					gameState.clearhighlight(out);
					// remove card from hand
					int index = gameState.getCardPosition(gameState.cardLastClicked);
					gameState.removePlayerCard(index);
					gameState.displayHand(out);
					BasicCommands.addPlayer1Notification(out, String.format("Play card id: %d", gameState.cardLastClicked.getId()), 1);
					Unit selected = clicked.getUnit();
					selected.setHealth(selected.getHealth() + 5);
					int currentHealth = selected.getHealth();
			        if (selected.getId()==99 && currentHealth > 20) selected.setHealth(20);
			        else if (selected.getId()==100 && currentHealth > 20) selected.setHealth(20);
			        else if ((selected.getId()==0 || selected.getId()==12) && currentHealth > 3) selected.setHealth(3);
			        else if ((selected.getId()==1 || selected.getId()==13) && currentHealth > 4) selected.setHealth(4);
			        else if ((selected.getId()==2 || selected.getId()==11) && currentHealth > 2) selected.setHealth(2);
			        else if ((selected.getId()==3 || selected.getId()==10) && currentHealth > 5) selected.setHealth(5);
			        else if ((selected.getId()==5 || selected.getId()==15) && currentHealth > 4) selected.setHealth(4);
			        else if ((selected.getId()==6 || selected.getId()==16) && currentHealth > 10) selected.setHealth(10);
			        else if ((selected.getId()==7 || selected.getId()==17) && currentHealth > 3) selected.setHealth(3);
			        else if ((selected.getId()==9 || selected.getId()==19) && currentHealth > 6) selected.setHealth(6);
			        else if ((selected.getId()==20 || selected.getId()==30) && currentHealth > 4) selected.setHealth(4);
			        else if ((selected.getId()==21 || selected.getId()==31) && currentHealth > 3) selected.setHealth(3);
			        else if ((selected.getId()==23 || selected.getId()==33) && currentHealth > 3) selected.setHealth(3);
			        else if ((selected.getId()==24 || selected.getId()==34) && currentHealth > 3) selected.setHealth(3);
			        else if ((selected.getId()==25 || selected.getId()==35) && currentHealth > 1) selected.setHealth(1);
			        else if ((selected.getId()==26 || selected.getId()==36) && currentHealth > 4) selected.setHealth(4);
			        else if ((selected.getId()==28 || selected.getId()==38) && currentHealth > 1) selected.setHealth(1);
			        else if ((selected.getId()==29 || selected.getId()==39) && currentHealth > 6) selected.setHealth(6);
			        EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
			        BasicCommands.playEffectAnimation(out, buff, clicked);
			        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			        BasicCommands.setUnitHealth(out, selected, selected.getHealth());
			        if (selected.getId()==99){
			            gameState.setHumanHealth(selected.getHealth());
			            BasicCommands.setPlayer1Health(out, gameState.getPlayer());
			        }
			        if (selected.getId()==100){
			            gameState.setAiHealth(selected.getHealth());
			            BasicCommands.setPlayer2Health(out, gameState.getAi());
			        }
			        
			        gameState.getPlayer().setMana(gameState.getPlayer().getMana() - gameState.cardLastClicked.getManacost());
			        BasicCommands.setPlayer1Mana(out, gameState.getPlayer());
			        try {
			            Thread.sleep(1);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
			        gameState.cardLastClicked = null;
				}
			}else if(gameState.cardLastClicked.getCardname().equals("Staff of Y'Kir'")) {//Assuming the card is in the player's hand and testing the function
				if(clicked.isHasUnit()) {
					Unit selected = clicked.getUnit();
					if(selected.getId()==100) {
						gameState.clearhighlight(out);
						// remove card from hand
						int index = gameState.getCardPosition(gameState.cardLastClicked);
						gameState.removePlayerCard(index);
						gameState.displayHand(out);
						EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
						BasicCommands.playEffectAnimation(out, buff, clicked);
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						selected.setAttack(selected.getAttack()+2);
						BasicCommands.setUnitAttack(out, selected, selected.getAttack());
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			        
			        //gameState.getAi().setMana(gameState.getAi().getMana() - gameState.cardLastClicked.getManacost());
			        //BasicCommands.setPlayer2Mana(out, gameState.getAi());
			        //try {
			        //    Thread.sleep(1);
			        //} catch (InterruptedException e) {
			        //    e.printStackTrace();
			        //}
			        //remove card
						for (Unit u : gameState.getPlayerUnits()){
							if (u.getId()==1 || u.getId()==13){
								BasicCommands.playEffectAnimation(out, buff, u.getTile());
								try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
								u.setAttack(u.getAttack()+1); //Attack plus 1
								u.setHealth(u.getHealth()+1); //Health plus 1
								BasicCommands.setUnitAttack(out, u, u.getAttack());
								try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
								BasicCommands.setUnitHealth(out, u, u.getHealth());
								try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
							}
						}
						gameState.removeAICard(index);
						gameState.cardLastClicked = null;
					}
				}
			}else if(gameState.cardLastClicked.getCardname().equals("Entropic Decay")) {//Assuming the card is in the player's hand and testing the function
				if(clicked.isHasUnit()) {
					Unit selected = clicked.getUnit();
					if(selected.getId()!=100 && selected.getId()!=99) {
						gameState.clearhighlight(out);
						// remove card from hand
						int index = gameState.getCardPosition(gameState.cardLastClicked);
						gameState.removePlayerCard(index);
						gameState.displayHand(out);
						EffectAnimation inmolation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
						BasicCommands.playEffectAnimation(out, inmolation, clicked);
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						selected.setHealth(0);
						BasicCommands.setUnitHealth(out, selected, 0);
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
						gameState.unitTakeDamage(selected, out, 0);
						for (Unit u : gameState.getPlayerUnits()){
							if (u.getId()==1 || u.getId()==13){
								EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
								BasicCommands.playEffectAnimation(out, buff, u.getTile());
								try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
								u.setAttack(u.getAttack()+1); //Attack plus 1
								u.setHealth(u.getHealth()+1); //Health plus 1
								BasicCommands.setUnitAttack(out, u, u.getAttack());
								try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
								BasicCommands.setUnitHealth(out, u, u.getHealth());
								try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
							}
						}
						gameState.removeAICard(index);
						gameState.cardLastClicked = null;
					}
				}
			}else if (gameState.highlightedForCard.contains(clicked)) {
				gameState.clearhighlight(out);
				// remove card from hand
				int index = gameState.getCardPosition(gameState.cardLastClicked);
				gameState.removePlayerCard(index);
				gameState.displayHand(out);
			
				// execute the card's effects
				gameState.cardLastClicked.playCard(out, gameState, clicked);
				
				gameState.cardLastClicked = null;
			}
		}

		// else the user last selected an unit, this means the current clicked tile is a target for action
			else {
			
			// user clicks on a target for movement
			if (gameState.highlightedForMovement.contains(clicked)) {
				gameState.clearhighlight(out);
				gameState.moveUnit(gameState.unitLastClicked, clicked, out);	
			
			// user clicks on a target for attack
			}else if (gameState.highlightedForAttack.contains(clicked)) {
				gameState.clearhighlight(out);	
				gameState.attack(gameState.unitLastClicked, clicked.getUnit(), out);
			}

			// user clicks on the same unit twice to cancelled the selection
			else if (clicked == gameState.unitLastClicked.getTile()) {
				gameState.clearhighlight(out);

			// user clicks on a random tile, no action is performed
			}else {
				return;
			}
			
			//clear highlight and reference to the last clicked unit
			//so that the event-processor is ready to process a new action for another unit	
			gameState.unitLastClicked = null;
			
		}	
		

	}

}
