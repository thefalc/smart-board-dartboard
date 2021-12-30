package ca.uvic.cs.chisel.nerf;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 25-Jan-07
 */
public interface INerfUI {

	public void repaintDartBoard();

	public void stopGame();

	public void startGame();

	public boolean isActive();

}
