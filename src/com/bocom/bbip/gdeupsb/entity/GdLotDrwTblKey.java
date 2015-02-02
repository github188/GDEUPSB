package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdLotDrwTblKey implements Serializable {
    private static final long serialVersionUID = 8783421669450603648L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdLotDrwTblGameId")
    private String gameId;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdLotDrwTblDrawId")
    private String drawId;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getDrawId() {
        return drawId;
    }

    public void setDrawId(String drawId) {
        this.drawId = drawId;
    }
}