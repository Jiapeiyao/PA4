package Game;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class ControlPanel extends JPanel implements ActionListener, MouseListener
{
	private final static int PAUSE = 150; // milliseconds
	private final static int DEFAULT_PAUSE_TIME = 200;
	private final static int DEFAULT_DIFFICULTY = 4;
	private final View view;
	private final Game game;
	
	private final JLabel GameLevel = new JLabel("Game Level: ");
	private final JTextField gameLevelTextField = new JTextField();
	private final JButton newGameButton= new JButton("New Game");
	private final JLabel  gameDurationLabel = new JLabel( "  Game duraion in seconds:" );
	private final JTextField gameDurationTextField = new JTextField();
	
	private final Timer animationTimer;
    private long gameStartTime;
    private int PAUSETIME;
	private int LEVEL;
    
    ControlPanel( View view, Game game ) 
    {
        this.view = view;
        this.game = game;
     
        setLayout( new GridLayout( 1, 3 ) );
        add( GameLevel );
        add( gameLevelTextField );
        add( newGameButton );
        add( gameDurationLabel );
        add( gameDurationTextField );

        animationTimer = new Timer( PAUSE, this );//------
        gameDurationTextField.setEditable( false );
        initialize();
        PAUSETIME = DEFAULT_PAUSE_TIME;
		LEVEL = DEFAULT_DIFFICULTY;
    }
    
    private void showTime(){
    	long stopTime = System.currentTimeMillis();
		int second = Math.round((stopTime - gameStartTime)/1000);
		int msecond = Math.round((stopTime - gameStartTime)%1000);
		String toString = ""+second+".";
		if (msecond<10)
			toString += "0";
		if (msecond<100)
			toString += "0";
		toString += msecond;
		gameDurationTextField.setText(toString);
    }
    private void changeLevel(){
    	long Duration = System.currentTimeMillis() -  gameStartTime;
    	if (Duration/1000 > 10){
    		if (LEVEL >1){
    			PAUSETIME+=20;
    			LEVEL--;
    		}
    		else{
    			JOptionPane.showMessageDialog(null, "Lowest Level! Hurry Up!");
    		}
    	}
    	else {
    		if (PAUSETIME>20){
    			PAUSETIME-=20;
    			LEVEL++;
    		}
    		else{
    			JOptionPane.showMessageDialog(null, "Congratulation! You have reached the highist level! (๑•̀ㅂ•́)و✧");
    			String str = JOptionPane.showInputDialog("Try More Critter: ");
    			int N = Integer.parseInt(str);
    			while (N<3 || N>15){
    				if (N>15){
    					str = JOptionPane.showInputDialog("Fail to create so many Critter! Try Less: ");
    					N = Integer.parseInt(str);
    				}
    				else {
    					str = JOptionPane.showInputDialog("That's too easy! Try More: ");
    					N = Integer.parseInt(str);
    				}
    			}
    			game.setNumOfCritter(N);
        		initiatizeLevel();
    			
    		}
    	}
    	gameLevelTextField.setText(""+LEVEL);
    }
    private void initiatizeLevel(){
    	PAUSETIME = DEFAULT_PAUSE_TIME;
		LEVEL = DEFAULT_DIFFICULTY;
    }
    
    private void initialize() {   
        newGameButton.addActionListener( new ActionListener() 
        {
            //@Override
            public void actionPerformed( ActionEvent actionEvent ) 
            {
                newGameButtonActionPerformed( actionEvent );
            }
        });
        
        view.addMouseListener( this );
    }
    
    private void newGameButtonActionPerformed( ActionEvent actionEvent ) 
    {
        // set the text field to the empty string;
        // record the start time of the game;
        // restart the Timer
        // start the game. 
    	gameDurationTextField.setText("");
    	gameStartTime = System.currentTimeMillis();
    	animationTimer.restart();
    	gameLevelTextField.setText(""+LEVEL);
    	game.start();
    }


	public void actionPerformed( ActionEvent e) {
		game.draw();
		view.repaint();
		if (game.isGameOver()){
			animationTimer.stop();
			showTime();
			changeLevel();
		}
		
    }

	public void mouseClicked(MouseEvent e) {	
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		animationTimer.stop();
		animationTimer.setDelay(PAUSETIME);
		game.processClick(e.getX(),e.getY());
		showTime();
		animationTimer.start();
		view.repaint();
	}

	public void mouseReleased(MouseEvent e) {	
	}

}
