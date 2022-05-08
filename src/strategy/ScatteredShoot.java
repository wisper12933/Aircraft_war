package strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import thread.MusicThread;

import java.util.LinkedList;
import java.util.List;

public class ScatteredShoot implements ShootStrategy{
    //散射模式
    @Override
    public List<AbstractBullet> ballistic(AbstractAircraft aircraft, int direction, int shootNum, int power) {
        List<AbstractBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction*3;
        int speedY = aircraft.getSpeedY() + direction*10;
        AbstractBullet abstractBullet;
        for (int i=0; i<shootNum ;i++){
            //多子弹轨道偏移,且有散射效果
            if(aircraft instanceof HeroAircraft){
                int dx = Math.max((10 - shootNum), 0);
                abstractBullet = new HeroBullet(x +(i*2 - shootNum + 1)*dx,(y + direction*12),(i*2 - shootNum + 1),speedY,power);
            }else{
                abstractBullet = new EnemyBullet(x +(i*2 - shootNum + 1)*10,y,(i*2 - shootNum + 1)*2,speedY,power);
            }

            res.add(abstractBullet);
        }

        return res;
    }
}
