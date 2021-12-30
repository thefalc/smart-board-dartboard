package ca.uvic.cs.chisel.nerf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import ca.uvic.cs.chisel.nerf.shapes.BouncingCircles;
import ca.uvic.cs.chisel.nerf.shapes.ConcentricCircles;
import ca.uvic.cs.chisel.nerf.shapes.HarderConcentricCircles;
import ca.uvic.cs.chisel.nerf.shapes.PulsatingNerfShapeCollection;
import ca.uvic.cs.chisel.nerf.shapes.ShapeCollection;
import ca.uvic.cs.chisel.nerf.util.JKeyboard;
import ca.uvic.cs.chisel.nerf.util.audio.NerfAudio;


/**
 * 
 * 
 * @author Chris Callendar
 * @date 23-Jan-07
 */
public class NerfUI implements MouseListener, MouseMotionListener, INerfUI {

	private static final Color BLUE = new Color (0, 46, 123);
	private static final Color BG = Color.black;
	private static final int MIN_DARTS = 1;
	private static final int MAX_DARTS = 10;
	private static final int DEFAULT_DARTS = 3;
	private static final String TITLE = "NerfBoard v0.8.2";
	private static final String[] COL_NAMES = { "Name", "Score", "Avg."};
	
	private Frame frame;
	private Cursor targetCursor;
	private JPanel mainPanel;
	private JToolBar topPanel;
	private JPanel rightPanel;
	private JScrollPane highscoresScrollPane;
	private JTable highscoresTable;
	private DefaultTableModel highscoresModel;
	private JLabel scoreLabel;
	private JLabel gameOverLabel;
	private JButton resetButton;
	private JSlider dartsSlider;
	private JCheckBox playSounds;
	private JCheckBox showFireworks;
	private JLabel dartsThrownLabel;
	private JPanel highscoresPanel;
	
	private ShapeCollection shapes = null;
	private GameController controller;
	private JComboBox gameChooser;
	private JButton addPlayerButton;
	private JButton resetPlayerButton;
	private JButton deletePlayerButton;
		
	public NerfUI(JFrame frame, ShapeCollection shapes) {
		this.frame = frame;		
		this.shapes = shapes;
		this.controller = new GameController(this, shapes.getName());
		this.shapes.setGameController(controller);
		
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			targetCursor = toolkit.createCustomCursor(toolkit.getImage(NerfUI.class.getResource("/icons/target32.gif")), new Point(16, 16), "Target");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initialize(frame);
	}
	
	private void initialize(JFrame frame) {
		frame.setTitle(TITLE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(getTopPanel(), BorderLayout.NORTH);
		frame.getContentPane().add(getMainPanel(), BorderLayout.CENTER);
		frame.getContentPane().add(getRightPanel(), BorderLayout.EAST);
		Dimension dim = new Dimension(800, 600);
		frame.setSize(dim);
		frame.setPreferredSize(dim);
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(getImage("target.gif"));
		
		if (targetCursor != null) {
			getMainPanel().setCursor(targetCursor);
		}
		
		frame.pack();
		frame.getRootPane().setDefaultButton(getResetButton());
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.dispose();
			}
		});
		
		// choose the default game
		getGameChooserComboBox().setSelectedIndex(0);
		shapes.startTimer();
	}
	
	private Image getImage(String name) {
		return Toolkit.getDefaultToolkit().getImage(NerfUI.class.getResource("/icons/" + name));
	}
	
	private ImageIcon getIcon(String name) {
		return new ImageIcon(NerfUI.class.getResource("/icons/" + name));
	}
	
	private JToolBar getTopPanel() {
		if (topPanel == null) {
			topPanel = new JToolBar();
			topPanel.setFloatable(false);
			topPanel.setPreferredSize(new Dimension(100, 48));
			topPanel.setMinimumSize(new Dimension(50, 48));
			topPanel.add(getResetButton());
			topPanel.addSeparator();
			topPanel.add(getPlaySoundsCheckBox());
			topPanel.add(getFireworksCheckBox());
			topPanel.addSeparator();
			topPanel.add(new JLabel("Darts:  "));
			topPanel.add(getDartsSlider());
			topPanel.addSeparator();
			topPanel.add(new JLabel("Darts thrown:  "));
			topPanel.add(getDartsThrownLabel());
			topPanel.add(Box.createHorizontalGlue());
			topPanel.add(getGameOverLabel());
			topPanel.add(getScoreLabel());
		}
		return topPanel;
	}
	
	private JCheckBox getPlaySoundsCheckBox() {
		if (playSounds == null) {
			playSounds = new JCheckBox("Sounds?", NerfAudio.PLAY);
			playSounds.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					NerfAudio.PLAY = playSounds.isSelected();
				}
			});
		}
		return playSounds;
	}
	
	private JCheckBox getFireworksCheckBox() {
		if (showFireworks == null) {
			showFireworks = new JCheckBox("Fireworks?", GameController.SHOW_FIREWORKS);
			showFireworks.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					GameController.SHOW_FIREWORKS = showFireworks.isSelected();
				}
			});
		}
		return showFireworks;
	}

	private JLabel getDartsThrownLabel() {
		if (dartsThrownLabel == null) {
			dartsThrownLabel = new JLabel("0");
		}
		return dartsThrownLabel;
	}

	private JSlider getDartsSlider() {
		if (dartsSlider == null) {
			dartsSlider = new JSlider(MIN_DARTS, MAX_DARTS, DEFAULT_DARTS);
			dartsSlider.setPaintTicks(true);
			dartsSlider.setPaintLabels(true);
			dartsSlider.setPaintTrack(true);
			dartsSlider.setSnapToTicks(true);
			dartsSlider.setToolTipText("Number of darts");
			Dictionary<Integer, JLabel> labels = new Hashtable<Integer, JLabel>(MAX_DARTS * 2);
			for (int i = MIN_DARTS; i <= MAX_DARTS; i++) {
				labels.put(i, new JLabel(String.valueOf(i)));
			}
			dartsSlider.setLabelTable(labels);
			dartsSlider.setPreferredSize(new Dimension(60, 36));
			final BoundedRangeModel model = dartsSlider.getModel();
			model.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (!model.getValueIsAdjusting()) {
						controller.setMaxDarts(model.getValue());
					}
				}
			});
		}
		return dartsSlider;
	}

	private JLabel getGameOverLabel() {
		if (gameOverLabel == null) {
			gameOverLabel = new JLabel("", JLabel.CENTER);
			gameOverLabel.setForeground(BLUE);
			gameOverLabel.setOpaque(true);
			gameOverLabel.setFont(gameOverLabel.getFont().deriveFont(Font.BOLD, 18f));
			gameOverLabel.setPreferredSize(new Dimension(125, 20));
			gameOverLabel.setMinimumSize(new Dimension(125, 20));
		}
		return gameOverLabel;
	}

	private JLabel getScoreLabel() {
		if (scoreLabel == null) {
			scoreLabel = new JLabel("0", JLabel.CENTER);
			scoreLabel.setOpaque(true);
			scoreLabel.setBorder(BorderFactory.createLoweredBevelBorder());
			scoreLabel.setBackground(Color.white);
			scoreLabel.setForeground(BLUE);
			scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 18f));
			scoreLabel.setPreferredSize(new Dimension(75, 20));
			scoreLabel.setMinimumSize(new Dimension(75, 20));
		}
		return scoreLabel;
	}

	private JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton(" Reset Game ");
			MouseAdapter resetScoreAdapter = new MouseAdapter() {
				private long start = 0;
				public void mousePressed(MouseEvent e) {
					start = System.currentTimeMillis();
				}
				public void mouseReleased(MouseEvent e) {
					long diff = System.currentTimeMillis() - start;
					if (diff < 300) {
						resetScore();
					}
				}
			};
			resetButton.addMouseListener(resetScoreAdapter);
		}
		return resetButton;
	}
	
	private JPanel getRightPanel() {
		if (rightPanel == null) {
			rightPanel = new JPanel(new BorderLayout());
			rightPanel.add(getGameChooserComboBox(), BorderLayout.NORTH);
			rightPanel.add(getHighscoresPanel(), BorderLayout.CENTER);
			rightPanel.setPreferredSize(new Dimension(220, 200));
		}
		return rightPanel;
	}
	
	private JPanel getHighscoresPanel() {
		if (highscoresPanel == null) {
			highscoresPanel = new JPanel(new BorderLayout());
			JLabel lbl = new JLabel("High Scores", JLabel.CENTER);
			lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
			lbl.setForeground(BLUE);
			highscoresPanel.add(lbl, BorderLayout.NORTH);
			highscoresScrollPane = new JScrollPane(getHighscoresTable());
			highscoresPanel.add(highscoresScrollPane, BorderLayout.CENTER);
			
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
			btnPanel.add(getAddPlayerButton());
			btnPanel.add(getResetPlayerButton());
			btnPanel.add(getDeletePlayerButton());
			highscoresPanel.add(btnPanel, BorderLayout.SOUTH);
		}
		return highscoresPanel;
	}
	
	private JButton getAddPlayerButton() {
		if (addPlayerButton == null) {
			addPlayerButton = new JButton(new AbstractAction("", getIcon("new_player.gif")) {
				public void actionPerformed(ActionEvent e) {
					addPlayer();
				}
			});
			addPlayerButton.setToolTipText("Add a new player");
		}
		return addPlayerButton;
	}

	private JButton getResetPlayerButton() {
		if (resetPlayerButton == null) {
			resetPlayerButton = new JButton(new AbstractAction("", getIcon("reset_player.gif")) {
				public void actionPerformed(ActionEvent e) {
					resetPlayer();
				}
			});
			resetPlayerButton.setToolTipText("Reset the stats for the selected player");
		}
		return resetPlayerButton;
	}
	

	private JButton getDeletePlayerButton() {
		if (deletePlayerButton == null) {
			deletePlayerButton = new JButton(new AbstractAction("", getIcon("delete_player.gif")) {
				public void actionPerformed(ActionEvent e) {
					deletePlayer();
				}
			});
			deletePlayerButton.setToolTipText("Delete the selected player");
		}
		return deletePlayerButton;
	}
	
	private JComboBox getGameChooserComboBox() {
		if (gameChooser == null) {
			gameChooser = new JComboBox(new ShapeCollection[] { 
					new ConcentricCircles(), 
					new HarderConcentricCircles(),
					new BouncingCircles(this),
					new PulsatingNerfShapeCollection(this)
				});
			gameChooser.setFont(gameChooser.getFont().deriveFont(Font.BOLD, 14f));
			gameChooser.setForeground(Color.white);
			gameChooser.setBackground(BLUE.brighter());
			gameChooser.setEditable(false);
			gameChooser.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						ShapeCollection shape = (ShapeCollection) gameChooser.getSelectedItem();
						setShapeCollection(shape);
					}
				}
			});
		}
		return gameChooser;
	}

	private JTable getHighscoresTable() {
		if (highscoresTable == null) {
			Object[][] data = controller.getData();
			highscoresModel = new DefaultTableModel(data, COL_NAMES) { 
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				public Class<?> getColumnClass(int columnIndex) {
					return (columnIndex == 0 ? String.class : Integer.class);
				}
			};
			highscoresTable = new JTable(highscoresModel);
			setupHighscoresTableColumns();
			addMouseListenerToHeaderInTable(highscoresTable);
			highscoresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			highscoresTable.setRowHeight(24);
			highscoresTable.setFont(highscoresTable.getFont().deriveFont(14f));
			
			highscoresTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						int row = highscoresTable.getSelectedRow();
						String player = null;
						if (row != -1) {
							player = (String) highscoresTable.getValueAt(row, 0);
						}
						controller.setPlayer(player);
					}
				}
			});
			
			// select the first player by default
			highscoresTable.getSelectionModel().setSelectionInterval(0, 0);
		}
		return highscoresTable;
	}
	
	 private void addMouseListenerToHeaderInTable(final JTable table) {
	        table.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        table.getTableHeader().setToolTipText("Click to sort");
	        table.getTableHeader().addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	                TableColumnModel columnModel = table.getColumnModel();
	                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
	                int column = table.convertColumnIndexToModel(viewColumn);
	                if (e.getClickCount() == 1 && column != -1) {
	                	controller.setSortBy(column);
	                	refreshHighscores(controller.getPlayer());
	                }
	            }
	        });
	    }

	private void setupHighscoresTableColumns() {
		highscoresTable.getTableHeader().setReorderingAllowed(false);
		highscoresTable.getTableHeader().setResizingAllowed(false);
		highscoresTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		highscoresTable.getColumnModel().getColumn(1).setMaxWidth(50);
		highscoresTable.getColumnModel().getColumn(2).setMaxWidth(50);
		highscoresTable.setColumnSelectionAllowed(false);
		highscoresTable.setCellSelectionEnabled(false);
		highscoresTable.setRowSelectionAllowed(true);
		highscoresTable.setShowHorizontalLines(true);
		highscoresTable.setShowVerticalLines(false);
	}
	
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(true) {
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					paintDartBoard(g);
				}
			};
			mainPanel.setBackground(BG);
			mainPanel.setOpaque(true);
			mainPanel.addMouseListener(this);
			mainPanel.addMouseMotionListener(this);
		}
		return mainPanel;
	}
	
	private void paintDartBoard(Graphics g) {
		Rectangle rect = getMainPanel().getBounds();
		// add a border?
		int pad = 0;
		rect = new Rectangle(rect.x + pad, rect.x + pad, rect.width - (2*pad), rect.height - (2*pad));
		shapes.render((Graphics2D)g, rect);
	}
	
	private boolean selectPlayerInTable(String player) {
		boolean selected = false;
		if (player != null) {
			for (int row = 0; row < highscoresModel.getRowCount(); row++) {
				String name = (String) highscoresModel.getValueAt(row, 0);
				if (player.equals(name)) {
					highscoresTable.getSelectionModel().setSelectionInterval(row, row);
					selected = true;
					break;
				}
			}
		}
		return selected;
	}
	
	private void refreshHighscores(String player) {
		// reset the table data
		Object[][] data = controller.getData();
		highscoresModel.setDataVector(data, COL_NAMES);

		// need to set the same properties on the columns
		setupHighscoresTableColumns();

		boolean selected = selectPlayerInTable(player);
		if (!selected && (data.length > 0)) {
			highscoresTable.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
	
	private void addPlayer() {
		String player = JKeyboard.showKeyboard("Add Player", "", null);
		if (player.length() > 0) {
			controller.addPlayer(player);
			refreshHighscores(player);
		}
	}	
	
	private void deletePlayer() {
		controller.deletePlayer();
		refreshHighscores(null);
	}

	private void resetPlayer() {
		String title = "Reset " + controller.getPlayer() + "?";
		String msg = "Are you sure you want to reset " + controller.getPlayer() + "?";
		int choice = JOptionPane.showConfirmDialog(getResetPlayerButton(), msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (choice == JOptionPane.YES_OPTION) {
			controller.resetPlayer();
		}
	}

	/** Resets the score and darts thrown.  No highscore will be entered.  */
	private void resetScore() {
		controller.newGame();
		updateScore();
	}
	
	/** Updates the score and darts thrown label, and checks if a highscore should be entered. */
	private void updateScore() {
		scoreLabel.setBackground(controller.isGameOver() ? Color.orange : Color.white);
		scoreLabel.setText(""+controller.getGame().getScore());
		dartsThrownLabel.setText(""+controller.getDartsThrown());
		//gameOverLabel.setVisible(controller.isGameOver());
		displayGameOverLabel(controller.isGameOver());
		
		if(controller.isGameOver() || controller.isNewGame()) {
			enableUI(true);
		}
		
		refreshHighscores(controller.getPlayer());
	}
	
	/** Shows/hides the "Game Over" label based on the display parameter */
	private void displayGameOverLabel(boolean display) {
		if(display) {
			gameOverLabel.setText("Game Over"); // = new JLabel("Game Over", JLabel.CENTER);
			gameOverLabel.setBackground(Color.orange);
			gameOverLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		}
		else {
			gameOverLabel.setText("");
			gameOverLabel.setBackground(topPanel.getBackground());
			gameOverLabel.setBorder(BorderFactory.createEmptyBorder());
		}
	}
	
	/**
	 * Enables/disables UI elements.  
	 * @param enabled True to enable the UI elements, false to disable them.
	 */
	private void enableUI(boolean enabled) {
		dartsSlider.setEnabled(enabled);
	}
	
	protected Frame getFrame() {
		if (frame == null) {
			Window window = SwingUtilities.windowForComponent(getMainPanel());
			if (window instanceof Frame) {
				frame = (Frame) window;
			}
		}
		return frame;
	}
	
	public void repaintDartBoard() {
		getMainPanel().repaint();
	}
	
	public void stopGame() {
		shapes.stopTimer();
	}
	
	public void startGame() {
		shapes.startTimer();
	}
	
	public boolean isActive() {
		if (frame != null) {
			return frame.isActive();	// frame?  or middle panel?
		}
		return false;
	}
	
	public void setShapeCollection(ShapeCollection shapes) {
		shapes.stopTimer();
		this.shapes = shapes;
		this.shapes.setGameController(controller);
		controller.setGameID(shapes.getName());	// must be set before the new game is created
		controller.newGame();
		shapes.startTimer();
		updateScore();
		repaintDartBoard();
		refreshHighscores(controller.getPlayer());
		getHighscoresTable().requestFocus();
	}
	
	// Mouse Listeners
	
	public void mousePressed(MouseEvent e) {
		enableUI(false);
		stopGame();
		shapes.mousePressed(e.getPoint());
		updateScore();
		getMainPanel().repaint();
	}
	
	public void mouseReleased(MouseEvent e) {
		shapes.mouseReleased(e.getPoint());
		startGame();
		getMainPanel().repaint();
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {}

	
	public static void main(String[] args) {
		new NerfUI(new JFrame(), new ConcentricCircles());
	}

}
