package edu.hitsz.aircraft;

import edu.hitsz.application.Game;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.AbstractBullet;
import strategy.Context;
import strategy.MobShoot;
import edu.hitsz.supply.AbstractSupply;

import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends EnemyAircraft {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<AbstractBullet> shoot() {
        Context context = new Context(new MobShoot());
        return context.executeShootStrategy(this,0,0,0);
    }

    @Override
    public AbstractSupply bonus() { return null; }

    @Override
    public void upgrade(){
        this.vanish();
        Game.bombScore += 10;
    }

}
