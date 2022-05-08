package strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.AbstractBullet;

import java.util.LinkedList;
import java.util.List;

public class MobShoot implements ShootStrategy{
    @Override
    public List<AbstractBullet> ballistic(AbstractAircraft aircraft, int direction, int shootNum, int power) {
        return new LinkedList<>();
    }
}
