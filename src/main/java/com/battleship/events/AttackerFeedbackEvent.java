package com.battleship.events;

import java.io.Serializable;

import com.battleship.client.BattleshipException;
import com.battleship.client.HitStatus;

public record AttackerFeedbackEvent(boolean attackSuccess, HitStatus hitStatus, BattleshipException exception) implements Serializable {

}