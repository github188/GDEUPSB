package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdLotPlnCtlKey implements Serializable {
    private static final long serialVersionUID = 6679247627278132536L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdLotPlnCtlDrawId")
    private String drawId;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdLotPlnCtlGameId")
    private String gameId;

    public String getDrawId() {
        return drawId;
    }

    public void setDrawId(String drawId) {
        this.drawId = drawId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}