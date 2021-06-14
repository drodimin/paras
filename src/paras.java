import  java.applet.Applet;
import  java.awt.*;

public  class  paras  extends  Applet
implements  Runnable  {

    //CONSTANTS
    static int SLEEP = 30;
    static int SCREEN_WIDTH = 400;
	static int SCREEN_HEIGHT = 300;
	static int DEBUG_WIDTH = 600;
    static int PHASE_DUR = 45000;
    static int PAUSE_DUR = 4000;
	static int ONE_TICK = 80;
	static int IMGNUM = 31;
	static String names[] = {"bg.gif","bullet.gif","bomb.gif","para.gif","chopper_r.gif",
						   "chopper_l.gif","plane_r.gif","plane_l.gif","explo0.gif","explo1.gif",
                           "explo2.gif","explo3.gif","explo4.gif","landed.gif", "final0.gif",
                           "final1.gif","final2.gif","final3.gif","final4.gif","final5.gif",						   "d0.gif","d1.gif","d2.gif","d3.gif","d4.gif","d5.gif","d6.gif",
						   "d7.gif","d8.gif","d9.gif","d10.gif"};

	static boolean DEBUG = false;
	static boolean STAT = false;
    
	//=============================================================//
    // GLOBALS
    //=============================================================//   	
    	Thread  th;
        boolean load = false;
			
        //Debug        
        TextArea out = null;
        
        //Graphics
        Image img[];
    	
    	//Game
        boolean play = false;
		boolean wait = true;
		boolean phaseEven = true;
        int phase_num = 0;
        int interval;
        boolean direction;
        double event_prob = 0.7, dir_prob = 0.3;
        
        //Graphics:
        MediaTracker  tracker;
        Image buffer;
		Image title;	
        Graphics offScreen;
        int font_height = 16;
           
        //action:
        boolean fire = false;
        boolean shoot_qued = false;
        
        //classes:
        game gm;
		        
        //timing:
        long curr_time;
		int update_time = 0;
		timer action	= null;
        timer phase		= null;
		timer launch	= null;
		timer frame		= null;
        int  fps = 0;
		int frame_count = 0;
    
 
    //=============================================================//
	// Init 
	//=============================================================//
    public  void  init()  {
    	if(DEBUG)
    	{
        	resize(DEBUG_WIDTH, SCREEN_HEIGHT);
        	setLayout(new FlowLayout(FlowLayout.RIGHT));
        	out = new TextArea(10, 25);
        	add(out);
        }  
        else
        	resize(SCREEN_WIDTH, SCREEN_HEIGHT);    
     }

	//=============================================================//
	// Start 
	//=============================================================//
    public  void  start()  
    {
		buffer = createImage(SCREEN_WIDTH, SCREEN_HEIGHT);
        offScreen = buffer.getGraphics();
        
        if(DEBUG)
        {
        	FontMetrics fm = offScreen.getFontMetrics();
        	font_height = fm.getAscent();
        }
		
		loadGraphics();
		
        th =  new  Thread(this);
        th.start();

    }
    
    //=============================================================//
	// Load Graphics
	//=============================================================//
    public  void  loadGraphics()  
    {
    	title = getImage(getCodeBase(), "img/title.gif");    
                
        tracker  =  new  MediaTracker(this);
		tracker.addImage(title,  0);
		
		img = new Image[IMGNUM];
		
    
		for(int i=0; i<IMGNUM; i++)
		{
			img[i] = getImage(getCodeBase(), "img/" + names[i]);    
			tracker.addImage(img[i],  1);
		}
    }
    
    //=============================================================//
	// reset
	//=============================================================//
    public  void  reset()  
    {
        load = false;
		out = null;
		play = false;
		wait = true;
		phaseEven = true;
        phase_num = 0;
        event_prob = 0.7;
        dir_prob = 0.3;        
		fire = false;
		shoot_qued = false;
        gm = null;
		update_time = 0;
		action	= null;
        phase	= null;
		launch	= null;
		frame	= null;
		fps = 0;
		frame_count = 0;
    }
    
    //=============================================================//
    // StartGame 
    //=============================================================//
   	void startGame()
    {
        gm = new game(img, out);
		if(action == null)
		{
			action = new timer();
			action.setDur(ONE_TICK);
		}
		action.setTime(curr_time);
		startPhase();
		play = true;
    }   
    
    //=============================================================//
    // EndGame 
    //=============================================================//
   	void endGame()
    {
    	reset();
    }   
    
    //=============================================================//
    // StartPhase 
    //=============================================================//
    void startPhase()
    {
		phaseEven = !phaseEven;
		phase_num++;
				
		curr_time = System.currentTimeMillis();
		if(phase == null)
		{
			phase = new timer();
			phase.setDur(PHASE_DUR);
		}
		phase.setTime(curr_time);

		
		if(launch == null)
			launch = new timer();
			
		int new_dur = PAUSE_DUR - 100 * phase_num;
		if(new_dur > 1000)				
			launch.setDur(PAUSE_DUR - 100 * phase_num);
		launch.setTime(curr_time);

    	event_prob = event_prob + 0.025;
    	dir_prob = dir_prob + 0.05;
		
		wait = true;
    }
    
    //=============================================================//
    // FindFPS
    //=============================================================//
    void findFPS()
    {
		if(frame == null)
		{
			frame = new timer();
			frame.setTime(curr_time);
			frame.setDur(1000);
		}
    	frame_count++;
        if (frame.event(curr_time))
        {
        		fps = frame_count;
			    frame_count = 0;
        }        
    }
    
    //=============================================================//
    //	Stop 
    //=============================================================//
    public  void  stop()  {
        th.stop();
        th  =  null;
		play = false;
    }

	//=============================================================//
    //	Run
    //=============================================================//
    public  void  run()  
    {				
        try  { tracker.waitForID(0); }  
            catch  (InterruptedException  e)  { return; }
        
        Thread  me  =  Thread.currentThread();
        
        while  (th  ==  me)  
        {
            try  { Thread.sleep(SLEEP); }  
                catch  (InterruptedException  e)  { break; }
            repaint();
        }
    }

	    
    public  void  paint(Graphics  g)  {
            update(g);
    }
    
    //=============================================================//
	//	Update
	//=============================================================//
    public  void  update(Graphics  g)  
    {
        offScreen = buffer.getGraphics();
       
        if  (!tracker.checkAll(true))
        {
            offScreen.setColor(Color.gray);
            offScreen.fillRect(0,  0,  size().width,  size().height);
            offScreen.setColor(Color.black);
            offScreen.drawString("Loading...",  5, font_height + 5);        
        } 
        else
        {	
			//draw background image
			if(!play)
        		offScreen.drawImage(title, 0, 0, this);
        	else
        	{
        		//fire game action
        		tick();
        		//draw frame on offscreen graphics         
        		gm.draw(offScreen);     
        		if(STAT)
					showStat(offScreen);	
        	}
        }        
       //draw frame from buffer on actuall graphics
       g.drawImage(buffer,0,0,this);
	}
	
    
    //=============================================================//
    //	Tick
    //=============================================================//
	void tick()
	{	
		curr_time = System.currentTimeMillis();
		findFPS();
		
		if(!action.event(curr_time))
			return;
			
		if(phase.event(curr_time))
			startPhase();
		
		if(wait)
			wait = !gm.isReady();
		
		if(launch.event(curr_time) && !wait && gm.canAdd())
		{
			if(Math.random() < dir_prob)
				direction = !direction;
			if(Math.random() < event_prob)
			{
				if(phaseEven)
					gm.addPlane(direction);
				else
					gm.addChopper(direction);
			}

		}
			
		if(fire || shoot_qued)
		{
            gm.addBullet();
            shoot_qued = false;
        }
			
        if(!gm.action())
        	endGame();
    }
	
	//=============================================================//
    //	ShowStat
    //=============================================================//
    void showStat(Graphics g)
	{
		int height = 100;
		int inc = font_height + 5;
		
		height = height + inc;      	
        offScreen.drawString(fps + "fps",  5, height);
		
		height = height + inc;      	
        offScreen.drawString("phase: " + phase_num,  5, height);	
		
		height = height + inc;      	
        offScreen.drawString("phaseEven: " + phaseEven,  5, height);	
		
		height = height + inc;      	
        offScreen.drawString("elapsed: " + phase.elapsed(curr_time)/1000 + " sec",  5, height);	
		
		height = height + inc;      	
        offScreen.drawString("direction: " + direction,  5, height);
			
		height = height + inc;      	
        offScreen.drawString("wait: " + wait,  5, height);			
	}
	
    //=============================================================//
    //	 MouseDown
    //=============================================================//
    public boolean mouseDown(Event e, int x, int y)
    {
        if(!play)
        {
            startGame();
            return true;
        }
        fire = true;                 
        shoot_qued = true;
        return true;
    }

    //=============================================================//
    //	 MouseUp
    //=============================================================//    
    public boolean mouseUp(Event e, int x, int y)
    {
        if(play)
        {
            fire = false;
        } 
        return true;
    }
    
    //=============================================================//
    //	 MouseMove
    //=============================================================//
    public boolean mouseMove(Event e, int x, int y)
    {
        if(play)
            gm.gunMove(x, y);
        return true;
    }

    //=============================================================//
    //	 MouseDrag
    //=============================================================//
    public boolean mouseDrag(Event e, int x, int y)
    {
        if(play)
            gm.gunMove(x, y);
        return true;
    }
    
}


