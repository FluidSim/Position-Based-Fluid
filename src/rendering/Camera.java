package rendering;

import org.lwjgl.input.Keyboard;

import egl.math.Matrix4;
import egl.math.Quat;
import egl.math.Vector3;

public class Camera {
	
	public Vector3 eye; 
	public Vector3 target;
	public Vector3 up; 
	
	/** right direction (perpendicular to up and view direction */
	private Vector3 right;
	
	/** View direciton */
	private Vector3 dir;
	
	/** x distance to center of box */
	private float xtrans = Renderer.system.rangex/2;
	
	/** z distance to center of box */
	private float ztrans = Renderer.system.rangez/2;
	
	/** Translation matrix from origin to center of box */
	private Matrix4 translation;
	
	/** Translation matrix from center of box to origin */
	private Matrix4 transBack;
		
	/** Camera constructor */
	public Camera(){
		eye = new Vector3(xtrans, 0f, -ztrans*2);
		target = new Vector3(xtrans, 0f, ztrans);
		dir = new Vector3(0,0,1);
		up = new Vector3(0, 1, 0);
		right = new Vector3(1,0,0);
		translation = Matrix4.createTranslation(xtrans,0,ztrans);
		transBack = Matrix4.createTranslation(-xtrans,0,-ztrans);
	}

	/** Listens to key presses and updates camera based on desired movements */
	void update() {
		Vector3 motion = new Vector3();
		float rotX = 0;
		float rotY = 0;
		boolean pressed = false;
		boolean rotPressed = false;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)) { motion.add(dir); pressed = true; }
		if(Keyboard.isKeyDown(Keyboard.KEY_S)) { motion.add(dir.clone().negate()); pressed = true;}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)) { motion.add(right); pressed = true;}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)) { motion.add(right.clone().negate()); pressed = true; }
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) { motion.add(up); pressed = true;}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { motion.add(up.clone().negate()); pressed = true;}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) { rotX = -20f; rotPressed = true;}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)) { rotX = 20f; rotPressed = true; }
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) { rotY = -.15f; rotPressed = true;}
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) { rotY = .15f; rotPressed = true;}
		
		if(pressed){
			Matrix4 trans = Matrix4.createTranslation(motion);
			trans.mulPos(eye);
			trans.mulPos(target);
			trans.mulDir(up);
        }
		
        if(rotPressed){
			Quat qx = new Quat(rotX, right.x,right.y,right.z).normalize();
			Matrix4 x = qx.toRotationMatrix(new Matrix4());
			Matrix4 y = Matrix4.createRotationY(rotY);
			if(rotY == 0){ y.setIdentity();}
			if(rotX == 0){ x.setIdentity();}
			Matrix4 finalRot = x.mulAfter(y);
			
			transBack.mulPos(eye);
			finalRot.mulPos(eye);
			translation.mulPos(eye);
			up.set(finalRot.mulDir(up).normalize());
			right.set(finalRot.mulDir(right).normalize());
			dir.set(finalRot.mulDir(dir).normalize());
		}
	}
}
