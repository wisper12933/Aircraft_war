package edu.hitsz.supply;

import edu.hitsz.application.Game;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.AbstractBullet;

import java.util.LinkedList;
import java.util.List;

public class BombSupply extends AbstractSupply{

    private List<AbstractFlyingObject> flyingObjectList = new LinkedList<>();

    public BombSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    public void addObject(AbstractFlyingObject flyingObject){
        flyingObjectList.add(flyingObject);
    }

    //通知所有观察者
    public void callAll(){
        for (AbstractFlyingObject flyingObject:flyingObjectList){
            flyingObject.upgrade();
        }
    }

    //炸弹生效
    public void executeBomb(Game game){
        System.out.println("炸弹道具生效");
        callAll();
        game.setScore(game.getScore()+Game.bombScore);
        Game.bombScore = 0;
    }

}
