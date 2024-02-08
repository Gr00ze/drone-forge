package com.Gr00ze.drones_mod.entities;

import com.Gr00ze.drones_mod.entities.controllers.PIDController;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.joml.Vector3f;

import java.util.List;

public class Drone extends AbstractDrone{


    Float targetYaw = null;
    public Drone(EntityType<? extends Mob> mobType, Level level) {
        super(mobType, level);
    }



    @Override
    public void tick() {
        super.tick();
        //calculateCollision();
        calculateBoundingBox();
        calculateRotationAngle();
        calculateMovement();
        calculatePilotInput();
    }

    private void calculateCollision() {
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0, 0.2, 0));
        if (!list.isEmpty()) {

            for (Entity collidingEntity : list) {
                boolean isPassenger = false;
                for (Entity passenger : this.getPassengers()) {
                    isPassenger = passenger == collidingEntity;
                }
                if (isPassenger) continue;
                Vec3 vec = collidingEntity.getDeltaMovement();
                collidingEntity.setPos(collidingEntity.getX(),this.getY() + this.getBoundingBox().getYsize(),collidingEntity.getZ());
                collidingEntity.setDeltaMovement(vec.x,vec.y < 0 ? 0 : vec.y,vec.z);
                collidingEntity.resetFallDistance();
                collidingEntity.setOnGround(true);
            }
        }
    }
    private void calculateBoundingBox() {

    }

    private void calculateMovement() {
        long currentTickTime = this.tickCount; // Tempo attuale (tick corrente)
        float deltaTime = (currentTickTime - lastTickTime) * 0.05f; // Conversione da tick a secondi (20 tick per secondo)

        float totalForce = getMotorForce(1) + getMotorForce(2) + getMotorForce(3) + getMotorForce(4),
                acceleration = totalForce / getWeight(),
                yaw = this.getAngle(DroneAngle.YAW),
                pitch = this.getAngle(DroneAngle.PITCH),
                roll = this.getAngle(DroneAngle.ROLL),
                ax = acceleration * (Mth.sin(roll) * Mth.cos(yaw) + Mth.sin(pitch) * Mth.sin(yaw)),
                ay = acceleration * Mth.cos(pitch) * Mth.cos(roll),
                az = acceleration * (Mth.sin(-pitch) * Mth.cos(yaw) + Mth.sin(roll) * Mth.sin(yaw));
        Vector3f v1 = this.getDeltaMovement().toVector3f();
        float
                v2x = ax * deltaTime + v1.x,
                v2y = ay * deltaTime + v1.y,
                v2z = az * deltaTime + v1.z;

        Vector3f v2 = new Vector3f(v2x,v2y,v2z);
        this.setDeltaMovement(v2.x,v2.y,v2.z);

        lastTickTime = currentTickTime;
    }
    public void calculateRotationAngle(){
        float
                f1 = getMotorForce(1),
                f2 = getMotorForce(2),
                f3 = getMotorForce(3),
                f4 = getMotorForce(4),
                pitchSpeed = (( f2 + f1 ) - ( f3 + f4 )),
                rollSpeed = (( f1 + f4 ) - ( f2 + f3 )),
                yawSpeed = (( f1 + f3 ) - ( f2 + f4 ));
        this.addAngle(DroneAngle.YAW, yawSpeed / 40);
        this.addAngle(DroneAngle.ROLL,  rollSpeed / 40);
        this.addAngle(DroneAngle.PITCH,pitchSpeed / 40);
        this.setYRot((this.getAngle(DroneAngle.YAW) * 180 / Mth.PI));
    };

    private void calculatePilotInput() {
        List <Entity> passengers = this.getPassengers();
        int size = passengers.size();
        if (size == 0) return;
        Entity rider = passengers.get(0);
        if (rider instanceof Player playerRider && !playerRider.level().isClientSide()){
            //System.out.println("playerRider.yya: "+playerRider.yya);
            float incrementSpeed = 0.01F;

            //this.setTargetYawAngle(rider.getYRot()*Mth.PI/180);
//            if(driverWantGoUp){
//                this.addW1(incrementSpeed);
//                this.addW2(incrementSpeed);
//                this.addW4(incrementSpeed);
//                this.addW3(incrementSpeed);
//            }else if(driverWantGoDown) {
//                this.addW1(-incrementSpeed);
//                this.addW2(-incrementSpeed);
//                this.addW4(-incrementSpeed);
//                this.addW3(-incrementSpeed);
//            }
            //instant model rotation

            targetYaw = rider.getYRot() * Mth.PI / 180;
            this.setYRot(rider.getYRot());
            float degreesYawAngle = this.getAngle(DroneAngle.YAW) * 180 / Mth.PI;

//            if (rider.getYRot() % 360 - degreesYawAngle % 360 < 180) {
//                //sx
//                this.addMotorForce(1,-incrementSpeed);
//                this.addMotorForce(2,+incrementSpeed);
//                this.addMotorForce(3,-incrementSpeed);
//                this.addMotorForce(4,+incrementSpeed);
//            }else {
//                this.addMotorForce(1,+incrementSpeed);
//                this.addMotorForce(2,-incrementSpeed);
//                this.addMotorForce(3,+incrementSpeed);
//                this.addMotorForce(4,-incrementSpeed);
//            }
            //forward back right left movement
            this.addMotorForce(1,- playerRider.zza/20 + playerRider.xxa/20 ) ;
            this.addMotorForce(2,- playerRider.zza/20 - playerRider.xxa/20 );
            this.addMotorForce(3, + playerRider.zza/20 - playerRider.xxa/20 );
            this.addMotorForce(4, + playerRider.zza/20 + playerRider.xxa/20 );

            //actual structure
            //2----1
            //3----4

        }
    }

    private void setTargetYawAngle(float targetYaw) {
        this.targetYaw = targetYaw;
    }
    private float getTargetYawAngle() {
        return this.targetYaw;
    }


    @Override
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (player.getMainHandItem().isEmpty()){
            player.startRiding(this);
        }
    }


}