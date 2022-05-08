package factory;

import edu.hitsz.aircraft.EnemyAircraft;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.supply.BombSupply;
import edu.hitsz.supply.FireSupply;
import edu.hitsz.supply.HpSupply;

public class SupplyFactory extends AbstractFactory{
    @Override
    AbstractFlyingObject createObject(String type, EnemyAircraft enemy) {
        AbstractFlyingObject supply = null;

        switch (type) {
            case "HpSupply":
                supply = new HpSupply(
                        enemy.getLocationX(),
                        enemy.getLocationY(),
                        0,
                        enemy.getSpeedY()
                );
                break;
            case "FireSupply":
                supply = new FireSupply(
                        enemy.getLocationX(),
                        enemy.getLocationY(),
                        0,
                        enemy.getSpeedY()
                );
                break;
            case "BombSupply":
                supply = new BombSupply(
                        enemy.getLocationX(),
                        enemy.getLocationY(),
                        0,
                        enemy.getSpeedY()
                );
                break;
            default:
                break;
        }

        return supply;
    }

}
