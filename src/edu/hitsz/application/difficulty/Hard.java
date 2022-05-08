package edu.hitsz.application.difficulty;

import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.Game;
import edu.hitsz.application.ImageManager;
import factory.BossFactory;
import factory.EnemyFactory;
import strategy.BossShoot;

public class Hard extends Game {
    public Hard(){
        this.backGround = ImageManager.BACKGROUND_IMAGE3;
        this.needBoss = true;
        this.eliteRate = 75;
        this.enemyMaxNumber =7;
        this.upgradeBossHp = 150;
        this.bossAppear = 500;
        this.changeBgm = true;
        EliteEnemy.isHard = true;
        BossFactory.isHpUpgrade = true;
        BossShoot.easyBossShoot = false;
    }

    @Override
    public void difficultUpgrade() {
        if (this.time % 45000 == 0 && this.time>0){
            if (this.eliteRate > 70){
                this.eliteRate -= 1;
                System.out.println("难度提升，当前精英机生成概率："+(100-eliteRate)+"%");
            }
            if (EnemyFactory.upgradeEliteHp < 90){
                EnemyFactory.upgradeEliteHp += 30;
                System.out.println("难度提升，当前精英机血量为："+(90+EnemyFactory.upgradeEliteHp));
            }
            if (EnemyFactory.upgradeMobSpeed < 4){
                EnemyFactory.upgradeMobSpeed += 1;
                System.out.println("难度提升，当前普通机速度为："+(9+EnemyFactory.upgradeMobSpeed));
            }
        }
    }

    @Override
    public boolean heroShootRate() {
        return this.time % 480 == 0;
    }

}
