package ca.uvic.cs.chisel.nerf.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Handles the backend DB for everything SmartBoard DartBoard.  This handles players
 * scores, throws, games, etc...
 * 
 * @author irbull
 *
 */
public class NerfDB implements INerfDB {
	
	static boolean regenerateName = true;
	static NerfDB _instance = null;

	private EPackage nerfModelPackage;
	private EClass personClass;
	private EClass gameClass;
	private EAttribute gameID;
	
	private EReference games;
	private EReference gameThrows;
	
	
	private EAttribute name;
	
	private EClass throwClass;
	private EAttribute xPos;
	private EAttribute yPos;
	private EAttribute point;
	
	private EFactory nerfModelFactory;
	private EList<EObject> playerList = null;
	

	/**
	 * Nerf Database
	 */
	private NerfDB() {
		createModel();
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xml", 
				   new XMIResourceFactoryImpl());
		
		nerfModelPackage.setNsURI("http://dartboard.smartboard");
		resourceSet.getPackageRegistry().put(nerfModelPackage.getNsURI(), nerfModelPackage);
		
		URI fileUri = URI.createFileURI(SCORE_FILE);
		
		try {
			Resource scoreResource = resourceSet.getResource(fileUri, true);
			scoreResource.load(null);
			playerList = scoreResource.getContents();
		} catch (Exception e) {
			System.out.println("Regenerating Players");
			this.regenerateNames();
			
		}
		
		
	}
	
	/**
	 * Get the singleton instance
	 * @return
	 */
	public static NerfDB getInstance() {
		if (_instance == null ) {
			_instance = new NerfDB();
		}
		return _instance;
		
	}

	/* (non-Javadoc)
	 * @see ca.uvic.cs.chisel.nerf.model.INerfDB#dispose()
	 */
	public void save() {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI fileUri = URI.createFileURI(SCORE_FILE);
		Resource scoreResource = resourceSet.createResource(fileUri);
		scoreResource.getContents().addAll(playerList);
		try {
			scoreResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see ca.uvic.cs.chisel.nerf.model.INerfDB#getPlayers()
	 */
	public List<String> getPlayers() {
		ArrayList<String> listOfPlayers = new ArrayList<String>();
		for (EObject eObject : playerList) {
			listOfPlayers.add((String)eObject.eGet(name));
		}

		return listOfPlayers;
	}
	
	/* (non-Javadoc)
	 * @see ca.uvic.cs.chisel.nerf.model.INerfDB#getCurrentScore(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public int getCurrentScore(String player, String gameID) throws PlayerNotFoundException {
		EObject person = getPlayer(player);
		if (person == null) throw new PlayerNotFoundException();
		EList<EObject> playersGames = (EList<EObject>) person.eGet(games);
		return getHighScore(playersGames, gameID);
		
	}
	
	@SuppressWarnings("unchecked")
	private int getHighScore(EList<EObject> playersGames, String gameID) {
		int highScoreSoFar = 0;
		for (EObject object : playersGames) {
			if ( object.eGet(this.gameID).equals(gameID)) {
				int tmpScore = getGameScore((EList<EObject>) object.eGet(gameThrows));
				if ( tmpScore > highScoreSoFar ) highScoreSoFar = tmpScore;
			}
		}
		return highScoreSoFar;
	}
	
	private int getGameScore( EList<EObject> listOfThrows) {
		int score = 0;
		for (EObject object : listOfThrows) {
			score += (Integer) object.eGet(point);
		}
		return score;
	}
	
	/* (non-Javadoc)
	 * @see ca.uvic.cs.chisel.nerf.model.INerfDB#getTotalAttempts(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public int getTotalAttempts(String player, String gameID) throws PlayerNotFoundException {
		EObject person = getPlayer(player);
		if (person == null) throw new PlayerNotFoundException();
		EList<EObject> playersGames = (EList<EObject>) person.eGet(games);
		int counter = 0;
		for (EObject object : playersGames) {
			if (object.eGet(this.gameID).equals(gameID)) counter++;
		}
		return counter;
	}
	
	/* (non-Javadoc)
	 * @see ca.uvic.cs.chisel.nerf.model.INerfDB#getTotalScore(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public int getTotalScore(String player, String gameID) throws PlayerNotFoundException {
		EObject person = getPlayer(player);
		if (person == null) throw new PlayerNotFoundException();
		EList<EObject> playersGames = (EList<EObject>) person.eGet(games);
		return getTotalScore(playersGames, gameID);
	}
	
	@SuppressWarnings("unchecked")
	private int getTotalScore(EList<EObject> playersGames, String gameID) {
		int total = 0;
		for (EObject object : playersGames) {
			if ( object.eGet(this.gameID).equals(gameID)) {
				total += getGameScore((EList<EObject>) object.eGet(gameThrows));
			}
		}
		return total;
	}
	
	
	/* (non-Javadoc)
	 * @see ca.uvic.cs.chisel.nerf.model.INerfDB#getAverageScore(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public int getAverageScore(String player, String gameID) throws PlayerNotFoundException {
		EObject person = getPlayer(player);
		if (person == null) throw new PlayerNotFoundException();
		EList<EObject> playersGames = (EList<EObject>) person.eGet(games);
		return getAverageScore(playersGames, gameID);

	}
	
	@SuppressWarnings("unchecked")
	private int getAverageScore(EList<EObject> playersGames, String gameID) {
		int total = 0;
		int counter = 0;
		for (EObject object : playersGames) {
			if ( object.eGet(this.gameID).equals(gameID)) {
				total += getGameScore((EList<EObject>) object.eGet(gameThrows));
				counter++;
			}
		}
		if ( counter > 0 ) return total / counter;
		return 0;
	}
	
	private EObject getPlayer(String player) throws PlayerNotFoundException {
		for (Iterator<EObject> iterator = playerList.iterator(); iterator.hasNext();) {
			EObject person = iterator.next();
			if ( person.eGet(name).equals(player) ) {
				return person;
			}
		}
		throw new PlayerNotFoundException();
	}
	
	public boolean playerExists(String player) {
		for (Iterator<EObject> iterator = playerList.iterator(); iterator.hasNext();) {
			EObject person = iterator.next();
			if ( person.eGet(name).equals(player) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Create a new person and give them a default score
	 * @param name
	 * @param score
	 */
	public void createPerson(String name) {
		EObject person = nerfModelFactory.create(personClass);
		person.eSet(this.name, name);
		playerList.add(person);
	}

	
	@SuppressWarnings("unchecked")
	public void addGame(String player, Game game) throws PlayerNotFoundException {
		EObject person = getPlayer(player);
		EObject currentGame = nerfModelFactory.create(gameClass);
		currentGame.eSet(gameID, game.gameID);
		List<Throw> currentGamesThrows = game.getThrows();
		for (Throw currentThrow : currentGamesThrows) {
			EObject modelThrow = nerfModelFactory.create(throwClass);
			modelThrow.eSet(xPos, currentThrow.getX());
			modelThrow.eSet(yPos, currentThrow.getY());
			modelThrow.eSet(point, currentThrow.getPoint());
			((List<EObject>)currentGame.eGet(gameThrows)).add(modelThrow);
		}
		((List<EObject>)person.eGet(games)).add(currentGame);
		
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Game> getGames(String player) throws PlayerNotFoundException {
		EObject person = getPlayer(player);
		List<EObject> playersGames = (List<EObject>) person.eGet(games);
		List<Game> games = new ArrayList<Game>(playersGames.size());
		for (EObject object : playersGames) {
			Game game = new Game((String) object.eGet(gameID)); 
			EList<EObject> eThrows = (EList<EObject>) object.eGet(gameThrows);
			for (EObject thr : eThrows) {
				int x = ((Integer)thr.eGet(xPos)).intValue();
				int y = ((Integer)thr.eGet(yPos)).intValue();
				int score = ((Integer)thr.eGet(point)).intValue();
				game.addThrow(x, y, score);
			}
			games.add(game);
		}
		return games;
	}
	
	/**
	 * Regenerate all the names in the XML file.
	 */
	public void regenerateNames() {
		if ( regenerateName ) {
			playerList = new BasicEList<EObject>();
			createPerson("Ian");
			createPerson("Chris C.");
			createPerson("Peter");
			createPerson("Chris B.");
			createPerson("Cher");
			createPerson("Jodels");
			createPerson("Del");
			createPerson("Sean");
			createPerson("TDog");
			createPerson("WW");
			createPerson("Peggy");
			
		}
	}
	
	/* (non-Javadoc)
	 * @see ca.uvic.cs.chisel.nerf.model.INerfDB#setCurrentScore(java.lang.String, int)
	 */
	public void setCurrentScore(String player, int score) throws PlayerNotFoundException {
//		EObject person = getPlayer(player);
//		
//		person.eSet(this.score, score);
	}
	
	/* (non-Javadoc)
	 * @see ca.uvic.cs.chisel.nerf.model.INerfDB#addToScores(java.lang.String, int)
	 */
	public void addToScores(String player, int score) throws PlayerNotFoundException {
//		EObject person = getPlayer(player);
//		
//		int totalScore = (Integer) person.eGet(this.totalScore);
//		int totalAttempts = (Integer) person.eGet(this.totalAttempts);
//		
//		person.eSet(this.totalScore, totalScore + score);
//		person.eSet(this.totalAttempts, totalAttempts + 1);
	}
	
	/**
	 * Save the ecore model to a file.  This can be used to create a metamodel and generate the
	 * code from it.
	 */
	private void saveEcore() {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl() );
		Resource ecoreResource = resourceSet.createResource(URI.createFileURI("nerfdb.ecore"));
		ecoreResource.getContents().add(nerfModelPackage);
		try {
			ecoreResource.save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the data model.  The model consists of a Person with 2 attributes, score and name
	 */
	private void createModel() {
		nerfModelPackage = EcoreFactory.eINSTANCE.createEPackage();
		nerfModelPackage.setName("smartboarddartboard");
		nerfModelPackage.setNsPrefix("ca.uvic.cs.chisel.nerf");
		personClass = EcoreFactory.eINSTANCE.createEClass();
		personClass.setName("Person");
		
		name = EcoreFactory.eINSTANCE.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.eINSTANCE.getEString());
		
		gameClass = EcoreFactory.eINSTANCE.createEClass();
		gameClass.setName("Game");
		
		gameID = EcoreFactory.eINSTANCE.createEAttribute();
		gameID.setName("gameID");
		gameID.setEType(EcorePackage.eINSTANCE.getEString());
		
		throwClass = EcoreFactory.eINSTANCE.createEClass();
		throwClass.setName("Throw");
		xPos = EcoreFactory.eINSTANCE.createEAttribute();
		xPos.setName("xPos");
		xPos.setEType(EcorePackage.eINSTANCE.getEInt());
		
		yPos = EcoreFactory.eINSTANCE.createEAttribute();
		yPos.setName("yPos");
		yPos.setEType(EcorePackage.eINSTANCE.getEInt());
		
		point = EcoreFactory.eINSTANCE.createEAttribute();
		point.setName("point");
		point.setEType(EcorePackage.eINSTANCE.getEInt());

		games = EcoreFactory.eINSTANCE.createEReference();
		games.setContainment(true);
		games.setEType(gameClass);
		games.setName("games");
		games.setUpperBound(-1);
		
		gameThrows = EcoreFactory.eINSTANCE.createEReference();
		gameThrows.setContainment(true);
		gameThrows.setEType(throwClass);
		gameThrows.setName("gameThrows");
		gameThrows.setUpperBound(-1);
		
		throwClass.getEStructuralFeatures().add(xPos);
		throwClass.getEStructuralFeatures().add(yPos);
		throwClass.getEStructuralFeatures().add(point);
		
		personClass.getEStructuralFeatures().add(games);
		gameClass.getEStructuralFeatures().add(gameThrows);
		
		personClass.getEStructuralFeatures().add(name);
		gameClass.getEStructuralFeatures().add(gameID);
		nerfModelFactory = nerfModelPackage.getEFactoryInstance();
		
		nerfModelPackage.getEClassifiers().add(personClass);
		nerfModelPackage.getEClassifiers().add(gameClass);
		nerfModelPackage.getEClassifiers().add(throwClass);
	}
	
	public static void main( String[] args ) {
		getInstance().save();
		getInstance().saveEcore();
	}

	
}
