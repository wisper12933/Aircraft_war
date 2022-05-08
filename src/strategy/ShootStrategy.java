package strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.AbstractBullet;

import java.util.List;

/**
 * @author 李星佐
 */
public interface ShootStrategy {
    List<AbstractBullet> ballistic(AbstractAircraft aircraft,int direction,int shootNum,int power);
}
