/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.nerf.util;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 26-Jan-07
 */
public class JKeyboard {

	private static final Color FORE_COLOR = Color.white;
	private static final Color TEXT_BG_COLOR = new Color(0, 0, 128);
	private static final Color BG_COLOR = new Color(0, 0, 64);
	private static final int KEY_HEIGHT = 48;

	private List<KeyListener> listeners;
	private HashMap<Integer, KeyButton> keyButtons;
	
	// gui components
	private JPanel mainPanel;
	private JPanel keyboardPanel;
	private JTextField textField;
	
	private KeyButton leftShift;
	private KeyButton rightShift;
	private KeyButton leftAlt;
	private KeyButton rightAlt;
	private KeyButton leftCtrl;
	private KeyButton rightCtrl;
	private boolean capsOn = false;
	private boolean shiftOn = false;
	private boolean ctrlOn = false;
	private boolean altOn = false;
	private Font font;
	private final int keySize;

	public JKeyboard() {
		this(KEY_HEIGHT);
	}
	
	public JKeyboard(int keySize) {
		super();
		this.keySize = keySize;
		this.listeners = new ArrayList<KeyListener>();
		this.keyButtons = new HashMap<Integer, KeyButton>();
		
		initialize();
	}
	
	////////////////////////////////////////////////////////
	// GUI METHODS
	////////////////////////////////////////////////////////
	
	private void initialize() {
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(getKeyboardPanel(), BorderLayout.CENTER);
		getTextField().requestFocus();
	}
	
	private Font getFont() {
		if (font == null) {
			font = mainPanel.getFont().deriveFont(Font.BOLD, 16f);
		}
		return font;
	}
	
	private JTextField getTextField() {
		if (textField == null) {
			textField = new JTextField();
			textField.setBorder(null);
			textField.setCaretColor(FORE_COLOR);
			textField.setFont(textField.getFont().deriveFont(Font.BOLD, 20f));
			textField.setBackground(TEXT_BG_COLOR);
			textField.setForeground(FORE_COLOR);
			textField.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					highlightKey(e);
					fireKeyPressedEvent(e);
				}
				public void keyReleased(KeyEvent e) {
					unhighlightKey(e);
					fireKeyReleasedEvent(e);
				}
				@Override
				public void keyTyped(KeyEvent e) {
					fireKeyTypeEvent(e);
				}
			});
		}
		return textField;
	}
	
	private JPanel getKeyboardPanel() {
		if (keyboardPanel == null) {
			keyboardPanel = new JPanel(new GridBagLayout());
			keyboardPanel.setBackground(BG_COLOR);
			addKeys();
		}
		return keyboardPanel;
	}

	////////////////////////////////////////////////////////
	// PRIVATE METHODS
	////////////////////////////////////////////////////////

	private void fireKeyPressedEvent(KeyEvent e) {
		for (KeyListener kl : listeners) {
			kl.keyPressed(e);
		}
	}

	private void fireKeyReleasedEvent(KeyEvent e) {
		for (KeyListener kl : listeners) {
			kl.keyReleased(e);
		}
	}

	private void fireKeyTypeEvent(KeyEvent e) {
		for (KeyListener kl : listeners) {
			kl.keyTyped(e);
		}
	}

	private void highlightKey(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyButtons.containsKey(keyCode)) {
			KeyButton btn = keyButtons.get(keyCode);
			btn.setHighlight(true);
		}
	}

	private void unhighlightKey(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyButtons.containsKey(keyCode)) {
			KeyButton btn = keyButtons.get(keyCode);
			btn.setHighlight(false);
		}
	}
		
	private void addKeys() {
		JPanel pnl = getKeyboardPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = 0;
		c.gridy = 0;
		
		// textfield and clear button row (16 columns)
		KeyButton btn = addKeyButton(pnl, c, null, false, 13);
		btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		btn.add(getTextField(), BorderLayout.CENTER);
		btn.setFillColor(TEXT_BG_COLOR);
		addKeyButton(pnl, c, getModifierKeyAction(KeyEvent.VK_CLEAR), false, 3);

		// first row
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_BACK_QUOTE, '`', '~'), true);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_1, '1', '!'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_2, '2', '@'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_3, '3', '#'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_4, '4', '$'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_5, '5', '%'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_6, '6', '^'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_7, '7', '&'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_8, '8', '*'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_9, '9', '('), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_0, '0', ')'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_MINUS, '-', '_'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_EQUALS, '=', '+'), false);
		addKeyButton(pnl, c, getSpecialKeyAction(KeyEvent.VK_BACK_SPACE, '\b'), false, 3);
		// second row
		addKeyButton(pnl, c, getSpecialKeyAction(KeyEvent.VK_TAB, '\t'), true, 2);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_Q), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_W), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_E), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_R), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_T), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_Y), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_U), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_I), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_O), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_P), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_OPEN_BRACKET, '[', '{'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_CLOSE_BRACKET, ']', '}'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_BACK_SLASH, '\\', '|'), false, 2);
		// third row
		addKeyButton(pnl, c, getModifierKeyAction(KeyEvent.VK_CAPS_LOCK), true, 2);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_A), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_S), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_D), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_F), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_G), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_H), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_J), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_K), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_L), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_SEMICOLON, ';', ':'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_QUOTE, '\'', '"'), false);
		addKeyButton(pnl, c, getSpecialKeyAction(KeyEvent.VK_ENTER, '\n'), false, 3);
		// fourth row
		leftShift = addKeyButton(pnl, c, getModifierKeyAction(KeyEvent.VK_SHIFT), true, 3);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_Z), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_X), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_C), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_V), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_B), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_N), false);
		addKeyButton(pnl, c, getLetterKeyAction(KeyEvent.VK_M), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_COMMA, ',', '<'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_PERIOD, '.', '>'), false);
		addKeyButton(pnl, c, getKeyAction(KeyEvent.VK_SLASH, '/', '?'), false);
		rightShift = addKeyButton(pnl, c, getModifierKeyAction(KeyEvent.VK_SHIFT), false, 3);
		// fifth row
		leftCtrl = addKeyButton(pnl, c, getModifierKeyAction(KeyEvent.VK_CONTROL), true, 2);
		leftAlt = addKeyButton(pnl, c, getModifierKeyAction(KeyEvent.VK_ALT), false, 2);
		addKeyButton(pnl, c, getSpecialKeyAction(KeyEvent.VK_SPACE, ' '), false, 8);
		rightAlt = addKeyButton(pnl, c, getModifierKeyAction(KeyEvent.VK_ALT), false, 2);
		rightCtrl = addKeyButton(pnl, c, getModifierKeyAction(KeyEvent.VK_CONTROL), false, 2);
	}
	
	private KeyAction getModifierKeyAction(int keyCode) {
		return new ModifierKeyAction(keyCode);
	}

	private KeyAction getLetterKeyAction(int keyCode) {
		String name = KeyEvent.getKeyText(keyCode);
		char c = Character.toLowerCase(name.charAt(0));
		char c2 = Character.toUpperCase(c);
		return new KeyAction(keyCode, c, c2);
	}

	private KeyAction getKeyAction(int keyCode, char c, char c2) {
		return new KeyAction(keyCode, c, c2);
	}
	
	private KeyAction getSpecialKeyAction(int keyCode, char c) {
		return new SpecialKeyAction(keyCode, c, KeyEvent.getKeyText(keyCode));
	}

	private KeyButton addKeyButton(JPanel pnl, GridBagConstraints c, final KeyAction action, boolean newRow) {
		return addKeyButton(pnl, c, action, newRow, 1);
	}
	
	private KeyButton addKeyButton(JPanel pnl, GridBagConstraints c, final KeyAction action, boolean newRow, int gridwidth) {
		c.gridwidth = gridwidth;
		if (newRow) {
			c.gridy = c.gridy + 1;
			c.gridx = 0;
		}
		
		final KeyButton btn = new KeyButton(action, getFont());
		if (action != null) {
			keyButtons.put(action.getKeyCode(), btn);
			btn.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					btn.setHighlight(true);
					action.keyPressed(btn);
					
					KeyEvent ke = new KeyEvent(btn, 0, System.currentTimeMillis(), getModifiers(), action.getKeyCode(), action.getKeyChar());
					fireKeyPressedEvent(ke);
				}
				public void mouseReleased(MouseEvent e) {
					btn.setHighlight(false);

					KeyEvent ke = new KeyEvent(btn, 0, System.currentTimeMillis(), getModifiers(), action.getKeyCode(), action.getKeyChar());
					fireKeyTypeEvent(ke);
					fireKeyReleasedEvent(ke);
				}
			});
		}
		
		int width = (gridwidth * keySize) + ((gridwidth - 1) * (c.insets.left + c.insets.right));
		Dimension size = new Dimension(width, keySize);
		btn.setMinimumSize(size);
		btn.setPreferredSize(size);
		btn.setMaximumSize(size);
		pnl.add(btn, c);
		
		c.gridx += gridwidth;
		
		return btn;
	}
	
	private void addCharacter(char c) {
		JTextField tf = getTextField();
		if (Character.isDefined(c)) {
			String text = tf.getText();
			switch (c) {
				case '\b' :	// BACKSPACE
					int posn = tf.getCaretPosition();
					int start = tf.getSelectionStart();
					int end = tf.getSelectionEnd();
					if ((end - start) > 0) {
						// deleted the selected text
						tf.setText(text.substring(0, start) + text.substring(end));
						tf.setCaretPosition(Math.max(0, posn - (end - start)));						
					} else if (posn > 0) {
						// delete the previous character
						tf.setText(text.substring(0, posn-1) + text.substring(posn));
						tf.setCaretPosition(posn-1);
					}
					break;
				case '\n' :
				case '\r' :
				case '\f' :
					// do nothing
					break;	
				default:
					tf.setText(text + c);
			}
		} else {
			System.out.println("Not defined");
		}
	}
	
	private int getModifiers() {
		int mod = 0;
		if (shiftOn) {
			mod |= KeyEvent.SHIFT_DOWN_MASK;
		}
		if (ctrlOn) {
			mod |= KeyEvent.CTRL_DOWN_MASK;
		}
		if (altOn) {
			mod |= KeyEvent.ALT_DOWN_MASK;
		}
		return mod;
	}
	
	private void turnOffTemporaryModifiers() {
		// these three modifiers only stay on for one more character type
		shiftOn = false;
		ctrlOn = false;
		altOn = false;
		leftShift.setPermanentlyHighlighted(false);
		rightShift.setPermanentlyHighlighted(false);
		leftAlt.setPermanentlyHighlighted(false);
		rightAlt.setPermanentlyHighlighted(false);
		leftCtrl.setPermanentlyHighlighted(false);
		rightCtrl.setPermanentlyHighlighted(false);
		getKeyboardPanel().repaint();
		
	}
	
	////////////////////////////////////////////////////////
	// PUBLIC METHODS
	////////////////////////////////////////////////////////
	
	public void clear() {
		getTextField().setText("");
	}
	
	public void setText(String text) {
		getTextField().setText(text == null ? "" : text);
		getTextField().selectAll();
	}
	
	public String getText() {
		return getTextField().getText();
	}
	
	public Component getComponent() {
		return mainPanel;
	}
	
	public void addKeyListener(KeyListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeKeyListener(KeyListener listener) {
		listeners.remove(listener);
	}

	public static String showKeyboard() {
		return showKeyboard("Keyboard", "", null);
	}

	public static String showKeyboard(String title, String initialString) {
		return showKeyboard(title, initialString, null);
	}

	public static String showKeyboard(String title, String initialString, Point locationOnScreen) {
		final JDialog dlg = new JDialog();
		dlg.setModal(true);
		dlg.setTitle(title);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		JKeyboard keyboard = new JKeyboard();
		keyboard.setText(initialString);
		dlg.getContentPane().add(keyboard.getComponent());

		// listen for the ENTER key which closes the dialog
		keyboard.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					dlg.setVisible(false);
				}
			}
		});
		
		dlg.pack();
		if (locationOnScreen == null) {
			// center on screen
			Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
			locationOnScreen = new Point(Math.max(0, (size.width / 2) - (dlg.getWidth() / 2)),
					Math.max(0, (size.height / 2) - (dlg.getHeight() / 2)));
		}
		dlg.setLocation(locationOnScreen);
		dlg.setVisible(true);	// blocks maybe	
		
		return keyboard.getText();
	}

	public static void main(String[] args) {
		showKeyboard();
	}
	
	/**
	 * Special key combinations: TAB, ENTER, BACKSPACE, SPACE
	 * These have a name and a single character value which isn't effected by shift or caps lock.
	 * 
	 * @author Chris Callendar
	 * @date 26-Jan-07
	 */
	class SpecialKeyAction extends KeyAction {
		
		private final String name;
		
		public SpecialKeyAction(int keyCode, char c, String name) {
			super(keyCode, c);
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
	}
	
	/**
	 * Handles the Shift, Caps Lock, Alt, and Control keys.
	 * These ones don't actually contain a valid character value.
	 * 
	 * @author Chris Callendar
	 * @date 26-Jan-07
	 */
	class ModifierKeyAction extends SpecialKeyAction {
		
		public ModifierKeyAction(int keyCode) {
			super(keyCode, '\0', KeyEvent.getKeyText(keyCode));
		}
		
		@Override
		public void keyPressed(KeyButton btn) {
			switch (getKeyCode()) {
				case KeyEvent.VK_SHIFT :
					shiftOn = !shiftOn;
					leftShift.setPermanentlyHighlighted(shiftOn);
					rightShift.setPermanentlyHighlighted(shiftOn);
					getKeyboardPanel().repaint();	// repaint to use the uppercase letters and numbers
					break;
				case KeyEvent.VK_CAPS_LOCK :
					capsOn = !capsOn;
					btn.setPermanentlyHighlighted(capsOn);
					getKeyboardPanel().repaint();	// repaint to use the uppercase letters and numbers
					break;
				case KeyEvent.VK_CONTROL :
					ctrlOn = !ctrlOn;
					leftCtrl.setPermanentlyHighlighted(ctrlOn);
					rightCtrl.setPermanentlyHighlighted(ctrlOn);
					break;
				case KeyEvent.VK_ALT :
					altOn = !altOn;
					leftAlt.setPermanentlyHighlighted(altOn);
					rightAlt.setPermanentlyHighlighted(altOn);
					break;
				case KeyEvent.VK_CLEAR :
					clear();	// clear the text in the textbox
					break;
			}
		}
	}
	
	/**
	 * All letters, numbers, and other single character keys.
	 * These all have different values when shift and caps lock is pressed. 
	 * 
	 * @author Chris Callendar
	 * @date 26-Jan-07
	 */
	class KeyAction {
		
		private final int keyCode;
		private final char c;
		private final char c2;
		
		public KeyAction(int keyCode, char c) {
			this(keyCode, c, c);
		}

		public KeyAction(int keyCode, char c, char c2) {
			this.keyCode = keyCode;
			this.c = c;
			this.c2 = c2;
		}
		
		public int getKeyCode() {
			return keyCode;
		}
		
		public String getName() {
			return "" + getKeyChar();
		}
		
		public char getKeyChar() {
			boolean useAlternate = (c != c2) && (capsOn ^ shiftOn);	// xor - if both are true then it is false
			return (useAlternate ? c2 : c);
		}

		@Override
		public String toString() {
			return getName();
		}
		
		public void keyPressed(KeyButton btn) {
			addCharacter(getKeyChar());
			
			// turn off shift, alt, and ctrl since they only stay one for one character
			turnOffTemporaryModifiers();
		}
		
	}
	
	class KeyButton extends JPanel {
		
		private final KeyAction action;
		private boolean highlight = false;
		private boolean permanentlyHighlighted = false;
		private Color fillColor = Color.black;
		private Color highlightColor = Color.blue;
		
		public KeyButton(KeyAction action, Font font) {
			super(new BorderLayout());
			this.action = action;
			setFont(font);
			setOpaque(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		public void setFillColor(Color fillColor) {
			this.fillColor = fillColor;
		}
		
		public void setHighlight(boolean highlight) {
			this.highlight = highlight;
			KeyButton.this.repaint();
		}
		
		public void setPermanentlyHighlighted(boolean highlighted) {
			this.permanentlyHighlighted = highlighted;
			KeyButton.this.repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			//super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Rectangle rect = KeyButton.this.getBounds();
			g2.setColor(highlight || permanentlyHighlighted ? highlightColor : fillColor);
			g2.fillRoundRect(1, 1, rect.width-2, rect.height-2, 5, 5);
			g2.setColor(FORE_COLOR);
			g2.setStroke(new BasicStroke(2f));
			g2.drawRoundRect(1, 1, rect.width-2, rect.height-2, 5, 5);
			
			FontMetrics metrics = g2.getFontMetrics();
			String name = (action != null ? action.getName() : "");
			int w = metrics.stringWidth(name);
			int h = metrics.getHeight();
			int x = (int)((rect.getWidth() / 2) - (w / 2d));
			int y = (int)((rect.getHeight() / 2) + (h / 2d)) - 1;
			g2.drawString(name, x, y);
			
		}
		
	}

}
