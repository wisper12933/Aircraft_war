package edu.hitsz.application.difficulty;

import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.Game;
import edu.hitsz.application.ImageManager;
public class Easy extends Game{
    public Easy(){
        this.backGround = ImageManager.BACKGROUND_IMAGE1;
        this.needBoss = false;
        this.eliteRate = 85;
        this.enemyMaxNumber = 5;
        this.upgradeBossHp = 0;
        this.bossAppear = Integer.MAX_VALUE;
        this.changeBgm = false;
        EliteEnemy.isHard = false;
    }

    @Override
    public void difficultUpgrade() {
    }

    @Override
    public boolean heroShootRate() {
        return this.time % 720 == 0;
    }

}
