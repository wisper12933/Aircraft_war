package edu.hitsz.aircraft;

import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.supply.AbstractSupply;

import java.util.List;

public abstract class EnemyAircraft extends AbstractAircraft {

    public EnemyAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<AbstractBullet> shoot() {
        return null;
    }

    /**道具生成方法
     * 可生成道具的类需实现
     * 不可实现的类可返回null
     * */
    public abstract AbstractSupply bonus();

}
