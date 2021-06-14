import java.awt.*;
import java.applet.Applet;
import java.lang.Math;
import java.net.URL;
import java.util.Vector;

public class game
{
	//CONSTANTS
	static int PLAY = -1;
	static int MOVE = 0;
	static int ANIME = 1;
	static int END = 2;
	//---	
	static int RIGHT	= 0;
	static int LEFT		= 1;
	//---
	static int EXPLO_FRAMES = 5;
	static int ANIME_FRAMES = 6;
	//---
    static int MAXBULLET= 50;
	static int MAXPARA	= 50;
	static int MAXLANDED= 20;
	static int MAXLINE	= 25;
	static int MAXEXPLO = 25;
	static int MAXBOMB  = 10;
	//---
	static int IMGNUM	= 31;
	static int BACK		= 0;
    static int BULLET	= 1;
    static int BOMB		= 2;
    static int PARA		= 3;
    static int CHOPPER	= 4;
    static int PLANE	= 6;
    static int EXPLO    = 8;
    static int LANDED	= 13;
    static int FINAL    = 14;
    static int SCORE	= 20;
	//---	
	static int OBJ_WIDTH 	= 32;
	static int OBJ_HEIGHT 	= 16;
	static int BOMB_WIDTH	= 6;
	static int PARA_WIDTH 	= 16;
	static int PARA_HEIGHT 	= 32;
	static int LANDED_HEIGHT = 16;
	static int GUNX		= 200;
	static int GUNY		= 220;
	static int LEFT_Y	= 25;
	static int RIGHT_Y	= 50;
	static int LEFT_X	= 416;
	static int RIGHT_X	= -16;
	static int SCORE_X  = 380;
	static int SCORE_Y  = 270;
	//---
	static int animeLX[] = {164, 164, 164, 180};
	static int animeLY[] = {244, 232, 220, 204};
	static int animeRX[] = {220, 220, 220, 204};
	static int animeRY[] = {244, 232, 220, 204};
	static double DROP_PROB = 0.05;
	static double BOMB_PROB = 0.025;
	
	//=============================================================//
    // GLOBALS
    //=============================================================//
    int score = 0;
    int stage = PLAY;
    int side  = -1;
    int frame = -1;
    int moved = 0;
    boolean Switch = true;
    
	Image img[] = new Image[IMGNUM];
	MediaTracker  tracker;

	int gun_eX = 200, gun_eY = 200;
    float gun_dX = 0, gun_dY =1;  //gun direction vector
	
    int bullet_num;
    int bulletsX[]	= new int[MAXBULLET];
    int bulletsY[]	= new int[MAXBULLET];    
    Vector bullets	= new Vector(MAXBULLET, 10);
    
    int bomb_num;
    int bombsX[]	= new int[MAXBOMB];
    int bombsY[]	= new int[MAXBOMB];    
    Vector bombs	= new Vector(MAXBOMB, 10);
	
	int left_num	= 0;
    int leftX[]		= new int[MAXLINE];
    int leftY[]		= new int[MAXLINE];
	int leftT[]		= new int[MAXLINE];    
    Vector left		= new Vector(MAXLINE, 5);
	
	int right_num	= 0;
    int rightX[]	= new int[MAXLINE];
    int rightY[]	= new int[MAXLINE];
	int rightT[]	= new int[MAXLINE];     
    Vector right	= new Vector(MAXLINE, 5);
	
	int para_num;
    int paraX[]	= new int[MAXPARA];
    int paraY[]	= new int[MAXPARA];    
    Vector para	= new Vector(MAXPARA, 10);
    
    int landedr_num, landedr_min;
    int landedrX[]	= new int[MAXLANDED];
    int landedrY[]	= new int[MAXLANDED];    
    Vector landedr	= new Vector(MAXLANDED, 5);
    
    int landedl_num, landedl_min;
    int landedlX[]	= new int[MAXLANDED];
    int landedlY[]	= new int[MAXLANDED];    
    Vector landedl	= new Vector(MAXLANDED, 5);
    
    int explo_num;
    int exploX[] = new int[MAXEXPLO];
    int exploY[] = new int[MAXEXPLO];    
    int exploF[] = new int[MAXEXPLO];
    Vector explo = new Vector(MAXEXPLO, 5);
    
    TextArea out;   //for debug messages;

    //===========================================================//
	// Public Methods
	//===========================================================//
	public game(Image [] aImg, TextArea aOut)
    {
    	img = aImg;
		out = aOut;  
    }
	//==========================================================//
	public boolean isReady()
	{
		if(left.isEmpty() && right.isEmpty() && (stage == PLAY))
			return true;
		return false;
	}
	//==========================================================//
	public boolean canAdd()
	{
		return (stage == PLAY);
	}
	//==========================================================//
    public void addBullet()
    {
        fo obj;
		obj = new fo(gun_eX, gun_eY, gun_dX, gun_dY, BULLET);
        bullets.addElement(obj);
        if(score>0)
			score = score - 1;
        debug("scene.addObj - added new bullet:\n");
        debug("   X=" + gun_eX + " Y=" + gun_eY + " dx=" + gun_dX + " dy=" + gun_dY + "\n");
    }
	//==========================================================//
	public void addChopper(boolean dir)
	{
		if(dir)
			addRight(CHOPPER);
		else
			addLeft(CHOPPER);
	}
	//==========================================================//
	public void addPlane(boolean dir)
	{
		if(dir)
			addRight(PLANE);
		else
			addLeft(PLANE);
	}
	//==========================================================//
    public boolean action()
    {
    	debug("start advance frame:\n");
    	advanceExplo();
		advanceBullets();
		advanceBombs();
		advanceLines();
		advancePara();			
		advanceLanded();
		debug("end advance frame:\n");

    	if (stage == PLAY)
  		{
    		if (landedl_num>=4)
			{
				side = LEFT;
				stage = MOVE;
			}
			else if(landedr_num>=4)
			{
				side = RIGHT;
				stage = MOVE;
			}
			return true;
		}
			
    	if (stage == MOVE)
    	{
    		if(para.isEmpty())
    		{
				movePara();
				if(moved > 3)
					stage = ANIME;
			}
			return true;
		}
		
		if (stage == ANIME)
		{
			if(Switch)
				frame++;
			Switch = !Switch;
			if(frame > 5)
				stage = END;
			return true;
		}
		
    	if (stage == END)
    		return false;      
	
		return true;	
    }
    //==========================================================//
	public void gunMove(int mX, int mY)
    {
        if(mY > GUNY)
            mY = GUNY;
        if(mY == GUNY && mX == GUNX)
        {
            mY = GUNY - 1;
            mX = GUNX - 1;
        }
        int tempX = GUNX - mX;
        int tempY = GUNY - mY;
        double tempN = Math.sqrt( (double)(tempX*tempX + tempY*tempY) );
        gun_dX = (float) (tempX/tempN);
        gun_dY = (float) (tempY/tempN);
        gun_eX = GUNX - (int) (gun_dX*25);
        gun_eY = GUNY - (int) (gun_dY*25);
    }		
	//==========================================================//
	public void draw(Graphics g)
    {
    	int i;
    	
		g.drawImage(img[BACK], 0, 0, null);
		
		g.setColor(Color.black);
		g.drawLine(0, GUNY + 40, 400, GUNY + 40);
		g.drawRect(GUNX - 20, GUNY, 40, 40);
		
		if(stage<ANIME)
		{
        	g.drawLine(GUNX, GUNY, gun_eX, gun_eY);
        	g.drawArc(GUNX - 7, GUNY - 7, 14, 14, 0, 180);
        }
        
        for(i=0; i<explo_num; i++)
			g.drawImage(img[EXPLO + exploF[i]], exploX[i], exploY[i], null);
			
        for(i=0; i<bullet_num; i++)
			g.drawImage(img[BULLET], bulletsX[i], bulletsY[i], null);
			
		for(i=0; i<bomb_num; i++)
			g.drawImage(img[BOMB], bombsX[i], bombsY[i], null);
		
		for(i=0; i<left_num; i++)
			if(leftT[i] == CHOPPER)
				g.drawImage(img[CHOPPER+LEFT], leftX[i], leftY[i], null);
			else
				g.drawImage(img[PLANE+LEFT], leftX[i], leftY[i], null);
				
		for(i=0; i<right_num; i++)
			if(rightT[i] == CHOPPER)
				g.drawImage(img[CHOPPER+RIGHT], rightX[i], rightY[i], null);
			else
				g.drawImage(img[PLANE+RIGHT], rightX[i], rightY[i], null);
				
		for(i=0; i<para_num; i++)
			g.drawImage(img[PARA], paraX[i], paraY[i], null);
			
		for(i=0; i<landedl_num; i++)
			g.drawImage(img[LANDED], landedlX[i], landedlY[i], null);
			
		for(i=0; i<landedr_num; i++)
			g.drawImage(img[LANDED], landedrX[i], landedrY[i], null);
			
		if(stage >= MOVE)
			for(i=1; i<moved+1; i++)
				if(side == LEFT)
					g.drawImage(img[LANDED], animeLX[i-1], animeLY[i-1], null);
				else if(side == RIGHT)
					g.drawImage(img[LANDED], animeRX[i-1], animeRY[i-1], null);
					
		if(stage == ANIME)
			g.drawImage(img[FINAL + frame], GUNX - 24, GUNY - 24, null);
		
		int temp = score;
		int d = 0;
		i = 0;
		while(true)
		{
			d = temp % 10;
			temp = (temp - d)/10;
			g.drawImage(img[SCORE + d], SCORE_X - 14 * i , SCORE_Y, null);
			i++;
			if(temp==0)
				break;
			
		}
		g.drawImage(img[SCORE + 10], SCORE_X - 14 * i - 40, SCORE_Y, null);		
    }	
	//==========================================================//
	
	
	
	
	
	//==========================================================//
	//					Private Methods							//
	//==========================================================//	
	private void debug(String message)
	{
		if(out != null)
			out.appendText(message);
	}
	//==========================================================//
	private void addLeft(int type)
    {
		if(left_num > MAXLINE-1)
			return;
			
        fo obj = new fo(LEFT_X, LEFT_Y, 0, 0, type);
        left.addElement(obj);
		
        debug("added to left dir\n");
	}
	//==========================================================//
	private void addRight(int type)
    {
		if(right_num > MAXLINE-1)
			return;
			
        fo obj = new fo(RIGHT_X, RIGHT_Y, 0, 0, type);
        right.addElement(obj);
		
        debug("added to right dir\n");
	}
	//==========================================================//
	private void addPara(float x, float y)
	{
		if(para_num > MAXPARA-1)
			return;
			
        fo obj = new fo(x, y, 0, 0, PARA);
        para.addElement(obj);
		
        debug("added para\n");
	}		
	//==========================================================//
	private void addBomb(float x, float y)
	{
		if(bomb_num > MAXBOMB-1)
			return;
			
        fo obj = new fo(x, y, (GUNX - x)/20, 0, BOMB);
        bombs.addElement(obj);
		
        debug("added bomb\n");
	}		
	//==========================================================//
	private void addExplo(float x, float y)
	{
		if(explo_num > MAXEXPLO-1)
			return;
			
        fo obj = new fo(x, y, 0, 0, 0);
        explo.addElement(obj);
		
        debug("added explo\n");
	}		
	//==========================================================//
	private void addLanded(float x)
	{
		fo obj = new fo(x, GUNY + 40 - LANDED_HEIGHT, 0, 0, 0);
		
		if(x < GUNX)
		{
			//add to the left
			if(landedl_num > MAXLANDED-1)
				return;
			landedl.addElement(obj);
		}
		else if(x > GUNX)
		{
			//add to the right
			if(landedr_num > MAXLANDED-1)
				return;
			landedr.addElement(obj);
		}
			
        debug("added landed\n");
	}		
	//==========================================================//
	private boolean inDropZone(int x)
	{
		if(x > (LEFT_X - 25) || x <(RIGHT_X + 25))
			return false;
		if(x > (GUNX - 40) && x < (GUNX + 25))
			return false;
		return true;
	}
	//==========================================================//
    private void advanceBullets()
    {
    	//debug("advanceBullets:\n");
		fo curr;
        int i = 0;
        
        boolean go = !bullets.isEmpty();
        bullet_num = 0;
        while(go)
        {
            if(i < bullets.size())
            {    
                curr = (fo) bullets.elementAt(i);
				if (curr.X<0 || curr.X>400 || curr.Y<0)
				{
					debug("   deleted bullet:\n");               
					bullets.removeElementAt(i);

				}
				else
                {
					curr.X = curr.X - curr.dX * 6;
					curr.Y = curr.Y - curr.dY * 6;
					
                    if(bullet_num < (MAXBULLET-1))
                    {
                        bulletsX[bullet_num] = (int)curr.X;
                        bulletsY[bullet_num] = (int)curr.Y;
                        bullet_num++;
                    }
                    i++;
                }
            }
            else
                break;
        }
    }
    //==========================================================//
    private void advanceBombs()
    {
		fo curr;
        int i = 0;
        int temp;
        boolean collide = false;
        boolean go = !bombs.isEmpty();
        bomb_num = 0;
        while(go)
        {
            if(i < bombs.size())
            {    
                curr = (fo) bombs.elementAt(i);
                collide = checkColl((int) curr.X, (int) curr.Y, BOMB_WIDTH, BOMB_WIDTH);
                if(collide)
                {
                	addExplo(curr.X, curr.Y);
                	score = score + 5;
                	side = 3;
                }     
               
				if (curr.Y > 255 || collide)
				{
					debug("   deleted bomb\n");               
					bombs.removeElementAt(i);

				}
				else if(curr.X > GUNX-12 && curr.X < GUNX+10 && curr.Y > GUNY-12 && curr.Y < GUNY+10)
				{
					debug("bomb hit:\n");
					stage = ANIME;
					bombs.removeElementAt(i);
				}							
				else
                {
                	curr.frame++;
					curr.X = curr.X + curr.dX;
					curr.Y = (float)(curr.Y + curr.frame * 1.2);
					
					
                    if(bomb_num < (MAXBOMB - 1) && curr.Y < 255)
                    {
                        bombsX[bomb_num] = (int)curr.X;
                        bombsY[bomb_num] = (int)curr.Y;
                        bomb_num++;
                    }
                    i++;
                }
            }
            else
                break;
        }
    }
	//==========================================================//
	private void advanceLines()
	{
		//debug("advanceLines:\n");
		fo curr;
		int i;
		boolean go;
		boolean collide;
		
		//advance left
		i = 0;
		go = !left.isEmpty();
        left_num = 0;
        while(go)
        {
        	collide = false;
            if(i < left.size())
            {              	
                curr = (fo) left.elementAt(i);
                collide = checkColl((int) curr.X, (int) curr.Y, OBJ_WIDTH, OBJ_HEIGHT );
                if(collide)
                {
                	addExplo(curr.X, curr.Y);
                	score = score + 3;
                }              	
				if (curr.X < RIGHT_X || collide)
				{
					debug("   deleted from left dir:\n");               
					left.removeElementAt(i);
				}
				else
                {
					curr.X = curr.X - 3;
					
                    if(left_num < (MAXBULLET-1))
                    {
						if(inDropZone((int)curr.X) && curr.Type == CHOPPER)
							if(Math.random() < DROP_PROB && curr.dropped < 3 && stage == -1)
								{
									addPara(curr.X, curr.Y + 40);
									curr.dropped++;
								}
						if(curr.X > GUNX + 20 && curr.X < GUNX + 175 &&  curr.Type == PLANE)
							if(Math.random() < BOMB_PROB && stage == -1)
								addBomb(curr.X, curr.Y + 40);
							
                        leftX[left_num] = (int)curr.X;
						leftY[left_num] = (int)curr.Y;
						leftT[left_num] = curr.Type;
                        left_num++;
                    }
                    i++;
                }
            }
            else
                break;
        }
		
		//advance right
		i = 0;
		go = !right.isEmpty();
        right_num = 0;
        while(go)
        {
        	collide = false;
            if(i < right.size())
            {    
                curr = (fo) right.elementAt(i);
                collide = checkColl((int) curr.X, (int) curr.Y, OBJ_WIDTH, OBJ_HEIGHT );
                if(collide)
  				{
                	addExplo(curr.X, curr.Y);  
                	score = score + 3;
                }              	
				if (curr.X > LEFT_X || collide)
				{
					debug("   deleted from right dir:\n");               
					right.removeElementAt(i);
				}
				else
                {
					curr.X = curr.X + 3;
					
                    if(right_num < (MAXBULLET-1))
                    {
						if(inDropZone((int)curr.X) && curr.Type == CHOPPER)
							if(Math.random() < DROP_PROB && curr.dropped < 3 && stage == -1)
								{
									addPara(curr.X, curr.Y + 20);
									curr.dropped++;
								}
						if(curr.X < GUNX - 20 &&  curr.X > GUNX - 175 && curr.Type == PLANE)
							if(Math.random() < BOMB_PROB && stage == -1)
								addBomb(curr.X, curr.Y + 20);
							
                        rightX[right_num] = (int)curr.X;
                        rightY[right_num] = (int)curr.Y;
						rightT[right_num] = curr.Type;
                        right_num++;
                    }
                    i++;
                }
            }
            else
                break;
        }
	}
	//==========================================================//
    private void advancePara()
    {
    	//debug("advancePara:\n");
		fo curr;
        int i = 0;
        boolean collide;
        boolean land;
        boolean go = !para.isEmpty();
        para_num = 0;
        while(go)
        {
        	collide = land = false;
            if(i < para.size())
            {    
                curr = (fo) para.elementAt(i);
                collide = checkColl((int) curr.X, (int) curr.Y, PARA_WIDTH, PARA_HEIGHT);
                land = (curr.Y > 225);
                if(collide || land)
				{
					if(collide)
					{
						addExplo(curr.X, curr.Y);
						score = score + 2;
					}
						
					if(land)
						addLanded(curr.X);
						
					debug("   deleted para:\n");               
					para.removeElementAt(i);

				}
				else 
                {
					curr.Y = curr.Y + 2;
					
                    if(para_num < (MAXPARA-1))
                    {
                        paraX[para_num] = (int)curr.X;
                        paraY[para_num] = (int)curr.Y;
                        para_num++;
                    }
                    i++;
                }
            }
            else
                break;
        }
    }	
	//==========================================================//
    private void advanceExplo()
    {
    	//debug("advanceExplo:\n");
		fo curr;
        int i = 0;
        boolean go = !explo.isEmpty();
        explo_num = 0;
        while(go)
        {
            if(i < explo.size())
            {    
                curr = (fo) explo.elementAt(i);
                if(curr.frame > 3)
				{
					debug("   deleted explo:\n");               
					explo.removeElementAt(i);

				}
				else 
                {
					curr.frame++;					
                    if(explo_num < (MAXEXPLO - 1))
                    {
                        exploX[explo_num] = (int)curr.X;
                        exploY[explo_num] = (int)curr.Y;
                        exploF[explo_num] = curr.frame;
                        explo_num++;
                    }
                    i++;
                }
            }
            else
                break;
        }
    }	
	//==========================================================//
    private void advanceLanded()
    {
    	//debug("advanceLanded:\n");
		fo curr;
        int i = 0, min = 300;
        
        //left
        boolean go = !landedl.isEmpty();
        landedl_num = 0;
        landedl_min = -1;
        while(go)
        {
            if(i < landedl.size())
            {    
                curr = (fo) landedl.elementAt(i);				
                if(landedl_num < (MAXLANDED - 1))
                {
                	if((GUNX - curr.X)<min)
                	{
                		min = GUNX - (int)curr.X;
                		landedl_min = i;
                	}
                	landedlX[landedl_num] = (int)curr.X;
                	landedlY[landedl_num] = (int)curr.Y;
                	landedl_num++;
                }
                i++;
            }
            else
                break;
        }
         
        //right
        i = 0;
        min = 300;
        go = !landedr.isEmpty();
        landedr_num = 0;
        landedr_min = -1;
        while(go)
        {
            if(i < landedr.size())
            {    
                curr = (fo) landedr.elementAt(i);				
                if(landedr_num < (MAXLANDED - 1))
                {
                	if((curr.X - GUNX)<min)
                	{
                		min = (int)curr.X - GUNX;
                		landedr_min = i;
                	}
                	landedrX[landedr_num] = (int)curr.X;
                	landedrY[landedr_num] = (int)curr.Y;
                	landedr_num++;
                }
                i++;
            }
            else
                break;
        }
    }	
	//==========================================================//
	private boolean checkColl(int objX, int objY, int width, int height)
    {
        int i = 0;
        
 		for(i=0; i<bullet_num; i++)
 			if((bulletsX[i] >= objX - 1) && (bulletsX[i] <= objX + width + 1))
 				if((bulletsY[i] >= objY - 1) && (bulletsY[i] <= objY + height +1))
 					return true;
 		
 		return false;	 
    }	
    
    //==========================================================//
	private void movePara()
    {
    	//debug("movePara:\n");
        fo curr;
        if(landedl_min == -1 && landedr_min == -1)
        	return;
        	
        if(side == LEFT)
        {
        	curr = (fo) landedl.elementAt(landedl_min);
        	if(curr.X < (GUNX - 40))
        		curr.X = curr.X + 3;
        	else
        	{
        		landedl.removeElementAt(landedl_min);
        		moved++;
        	}
        	return;
        }
		else if(side == RIGHT)
        {
        	curr = (fo) landedr.elementAt(landedr_min);
        	if(curr.X > (GUNX + 30))
        		curr.X = curr.X - 3;
        	else
        	{
        		landedr.removeElementAt(landedr_min);
        		moved++;
        	}
        	return;
        }

    }	
	//==========================================================//

}
