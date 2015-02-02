package com.bocom.bbip.gdeupsb.entity;

import com.bocom.bbip.data.annotation.GeneratedValue;
import com.bocom.bbip.data.annotation.Id;
import com.bocom.bbip.data.annotation.StepSequenceGenerator;
import java.io.Serializable;

public class GdLotChkCtlKey implements Serializable {
    private static final long serialVersionUID = 6539653963160257606L;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdLotChkCtlGameId")
    private String gameId;

    @Id
    @GeneratedValue
    @StepSequenceGenerator(type="GdLotChkCtlDrawId")
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