package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;

public class HpSupply extends AbstractSupply{

    public HpSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    public void executeHp(HeroAircraft heroAircraft){
        int health = 30;
        System.out.println("加血道具生效");
        heroAircraft.increaseHp(health);
    }

}
