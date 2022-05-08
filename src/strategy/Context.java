package strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.AbstractBullet;

import java.util.List;

public class Context {
    private ShootStrategy shootStrategy;

    public Context(ShootStrategy shootStrategy){
        this.shootStrategy = shootStrategy;
    }

    public void setShootStrategy(ShootStrategy shootStrategy){
        this.shootStrategy = shootStrategy;
    }

    public List<AbstractBullet> executeShootStrategy(AbstractAircraft aircraft, int direction, int shootNum, int power){
        return this.shootStrategy.ballistic(aircraft,direction,shootNum,power);
    }
}
