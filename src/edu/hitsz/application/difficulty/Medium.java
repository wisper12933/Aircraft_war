package edu.hitsz.application.difficulty;

import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.application.Game;
import edu.hitsz.application.ImageManager;
import factory.BossFactory;
import factory.EnemyFactory;
import strategy.BossShoot;

public class Medium extends Game {
    public Medium(){
        this.backGround = ImageManager.BACKGROUND_IMAGE2;
        this.needBoss = true;
        this.eliteRate = 80;
        this.enemyMaxNumber = 6;
        this.upgradeBossHp = 0;
        this.bossAppear = 700;
        this.changeBgm = false;
        EliteEnemy.isHard = false;
        BossFactory.isHpUpgrade = false;
        BossShoot.easyBossShoot = true;
    }

    @Override
    public void difficultUpgrade() {
        if (this.time == 60000){
            EnemyFactory.upgradeMobSpeed = 2;
            System.out.println("难度提升，普通机速度加快");
        }
        if (this.time == 90000){
            EliteEnemy.isHard = true;
            System.out.println("难度提升，精英机开始散射");
        }
        if (this.time % 45000 == 0 && this.time>0){
            if (eliteRate > 75){
                this.eliteRate -= 1;
                System.out.println("难度提升，当前精英机生成概率："+(100-eliteRate)+"%");
            }
            if (EnemyFactory.upgradeEliteHp < 60){
                EnemyFactory.upgradeEliteHp += 15;
                System.out.println("难度提升，当前精英机血量为："+(90+EnemyFactory.upgradeEliteHp));
            }
        }
    }

    @Override
    public boolean heroShootRate() {
        return this.time % 600 == 0;
    }

}
