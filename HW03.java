
import java.util.*;

class HW03 extends App {
    
    static class Player {
        Vector2 position;
        double radius;
        Vector3 color;
        int framesSinceFired;
    }
    
    static class Turret {
        Vector2 position;
        double radius;
        Vector3 color;
        int framesSinceFired;
        int health;
        
        Turret(){
            this.position = new Vector2(0.0,0.0);
            this.radius=8.0;
            this.framesSinceFired=0;
            this.color = Vector3.red;
            this.health = 3;
        }
        
        Turret(Vector2 pos){
            this.position = pos;
            this.radius=8.0;
            this.framesSinceFired=0;
            this.color = Vector3.red;
            this.health = 3;
        }
        
    }
    
    static class Portal {
        Vector2 position;
        double radius;
        Vector3 color;
        
        static int lastTeleportedPlayer = 0;
        static int lastDropped = 0;
        
        Portal(Vector2 pos, boolean blue){
            this.position = pos;
            this.radius = 3.0;
            
            if(blue) {
                this.color = Vector3.blue;
            } else {
                this.color = new Vector3(0.9, 0.8, 0.5); 
            }
            
            
        }
    }
    
    static class Bullet {
        Vector2 position;
        Vector2 velocity;
        double radius;
        Vector3 color;
        boolean alive;
        int age;
        int type;
        boolean teleported;
        
        static final int TYPE_PLAYER = 0;
        static final int TYPE_TURRET = 1;
        
        void fireBullet(Turret turret, Player player, int typeOfShot,Vector2 direction) {
            if(typeOfShot==TYPE_TURRET){
                position = turret.position;
                velocity = direction;                       
                color = turret.color;                       
                type = TYPE_TURRET;
            } else {
                position = player.position;
                velocity = direction;
                color = player.color;
                type = TYPE_PLAYER;
            }
            radius = 2.0;
            alive = true;
            age = 0;
        }
        
    }
    
    Player player;
    Turret[] turrets;
    Bullet[] bullets;
    Portal[] portals;
    boolean lastPortalBlue = false;
    
    void setup() {
        player = new Player();
        player.position = new Vector2(0.0, -40.0);
        player.radius = 4.0;
        player.color = Vector3.cyan;
        
        turrets = new Turret[2];
        turrets[0] = new Turret(new Vector2(-40.0,0.0));
        turrets[1] = new Turret(new Vector2(40.0,0.0));
        
        portals = new Portal[2];
        
        bullets = new Bullet[256];
        for (int bulletIndex = 0; bulletIndex < bullets.length; ++bulletIndex) {
            bullets[bulletIndex] = new Bullet();
        }
    }
    
    void dropPortal(Vector2 pos, boolean blue) {
        if(blue) {
            portals[0] = new Portal(pos,true);
        } else {
            portals[1] = new Portal(pos,false);
        }
    }
    
    void playerShoot(Vector2 direction) {
        for (int bulletIndex = 0; bulletIndex < bullets.length; ++bulletIndex) {
            Bullet bullet = bullets[bulletIndex];
            if (!bullet.alive) { // write to first unused ("dead") slot in bullets array
                bullet.fireBullet(turrets[0],player,Bullet.TYPE_PLAYER,direction);                        
                break;
            }
        }
    }
    
    boolean circleCircleIntersect(double radius1, double radius2, Vector2 pos1, Vector2 pos2){
        if((radius1+radius2) > Vector2.distanceBetween(pos1,pos2)) {
            return true;
        }
        return false;
    }
    void loop() {
        // player
        if (keyHeld('W')) { player.position.y += 1.0; }
        if (keyHeld('A')) { player.position.x -= 1.0; }
        if (keyHeld('S')) { player.position.y -= 1.0; }
        if (keyHeld('D')) { player.position.x += 1.0; }
        
        if (player.framesSinceFired++ >= 8){            
            player.framesSinceFired = 0;
            if (keyHeld('I')) {
                Vector2 direction = new Vector2(0.0,1.0);
                playerShoot(direction);
            } 
            if (keyHeld('J')) {
                Vector2 direction = new Vector2(-1.0,0.0);
                playerShoot(direction);
            } 
            if (keyHeld('K')) {
                Vector2 direction = new Vector2(0.0,-1.0);
                playerShoot(direction);
            }
            if (keyHeld('L')) {
                Vector2 direction = new Vector2(1.0,0.0);
                playerShoot(direction);
            } 
        }
        
        
        
        //Portals
        if(Portal.lastTeleportedPlayer++ > 5 && Portal.lastDropped > 16){
        for(int portalIndex = 0; portalIndex < portals.length; portalIndex++){
             if(portals[portalIndex]!=null && portals[(portalIndex+1)%2]!=null){
                    if(circleCircleIntersect(player.radius, portals[portalIndex].radius, player.position, portals[portalIndex].position)){                        
                        player.position.x = portals[(portalIndex+1)%2].position.x;
                        player.position.y = portals[(portalIndex+1)%2].position.y;
                        Portal.lastTeleportedPlayer=0;
                        break;
                    }}}}
        if(Portal.lastDropped++ > 16){
             
            
            if (keyHeld('P')) { 
                dropPortal(new Vector2(player.position.x,player.position.y),!lastPortalBlue); 
                Portal.lastDropped = 0; 
            if(!lastPortalBlue) {
                lastPortalBlue = true;
            } else {
                lastPortalBlue = false;
            }}
            
        }
        drawCircle(player.position, player.radius, player.color);
        for (int portalIndex = 0; portalIndex < portals.length; portalIndex++){
            if(portals[portalIndex]!=null){
                Portal portal = portals[portalIndex];
                drawCircle(portal.position, portal.radius, portal.color);
            }}
        
        //turrets
        boolean alive = false;
        
        for(int turretIndex=0;turretIndex<turrets.length;turretIndex++){
            Turret turret = turrets[turretIndex];
            if (turret.health > 0){
                alive = true;
            }
            
            turret=turrets[turretIndex];
            // turret
            for (int bulletIndex = 0; bulletIndex < bullets.length; ++bulletIndex) {
                Bullet bullet = bullets[bulletIndex];
                
                if(bullet.alive){
                for(int portalIndex = 0; portalIndex < portals.length; portalIndex++){
                    if(portals[portalIndex]!=null && portals[(portalIndex+1)%2]!=null){
                    if(!bullet.teleported && circleCircleIntersect(bullet.radius, portals[portalIndex].radius, bullet.position, portals[portalIndex].position)){                        
                        bullet.position = portals[(portalIndex+1)%2].position;
                        bullet.teleported = true;
                    }}}}
                        if (bullet.alive && bullet.type == Bullet.TYPE_TURRET) {
                            if (circleCircleIntersect(bullet.radius, player.radius, bullet.position, player.position)) {
                                reset();
                            }
                        } else if (bullet.alive && turret.health > 0){
                            if (circleCircleIntersect(bullet.radius, turret.radius, bullet.position, turret.position)) {
                                bullet.alive = false;
                                turret.health--;
                                if (turret.health == 2) {
                                    turret.color = new Vector3(1.0, 0.33, 0.33);
                                } else if (turret.health == 1) {
                                    turret.color = new Vector3(1.0, 0.67, 0.67);
                                } 
                            }
                        }
                        
                    }
                
            
            
            
            if (turret.framesSinceFired++ == 16 && turret.health > 0) {
                turret.framesSinceFired = 0;
                
                // fire bullet
                for (int bulletIndex = 0; bulletIndex < bullets.length; ++bulletIndex) {
                    Bullet bullet = bullets[bulletIndex];
                    
                    if (!bullet.alive) { // write to first unused ("dead") slot in bullets array
                        bullet.fireBullet(turret,player,Bullet.TYPE_TURRET,Vector2.directionVectorFrom(turret.position, player.position));
                        
                        break;
                    }
                }
            }
            if(turret.health > 0) {
                drawCircle(turret.position, turret.radius, turret.color);
            }}
        
        
        if(!alive) {
            reset();
        }
        
        // bullets
        for (int bulletIndex = 0; bulletIndex < bullets.length; ++bulletIndex) {
            Bullet bullet = bullets[bulletIndex];
            
            if (!bullet.alive) { continue; } // skip dead bullets
            
            // kill bullets that are too old (they're probably off-screen anyway)
            if (bullet.age++ > 128) {
                bullet.alive = false;
            }
            
            // "physics"
            bullet.position = bullet.position.plus(bullet.velocity);
            
            // draw
            drawCircle(bullet.position, bullet.radius, bullet.color);
        }
    }
    
    public static void main(String[] arguments) {
        App app = new HW03();
        app.setWindowBackgroundColor(0.0, 0.0, 0.0);
        app.setWindowSizeInWorldUnits(128.0, 128.0);
        app.setWindowCenterInWorldUnits(0.0, 0.0);
        app.setWindowHeightInPixels(512);
        app.setWindowTopLeftCornerInPixels(64, 64);
        app.run();
    }
}
