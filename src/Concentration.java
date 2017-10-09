package gui_programs;

import javax.imageio.ImageIO;
//Karamel Quitayen
//G Period Java
//Extra Credit Program: Concentration
//=====================================
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("serial")
public class Concentration extends JFrame implements ActionListener
{
	private Deck deck;
	private Vector<Button> buttons;
	
	private Timer timer = new Timer(1000, new TimerListener());
	
	private int index1, index2;
	private int pairsFound, pairsHidden, numTries;
	private boolean pickedCard1, pickedCard2;
	
	private JPanel cardsPanel = new JPanel(new GridLayout(4,13));
	private JPanel btnPanel = new JPanel();
	private JPanel infoPanel = new JPanel();
	private JButton reset = new JButton("New Game");
	private JButton showAll = new JButton("Show All");
	
	private static final Color feltGreen = new Color(0,82,0);
	private static final Font font = new Font("Consolas", Font.PLAIN, 20);
	
	public static void main(String[] args) throws IOException{
		new Concentration();
	}
	
	Concentration() throws IOException
	{
		//set up frame
		setTitle("Concentration");
		setSize(1000,550);
		setLocation(300,100);
		setResizable(false);
		setBackground(feltGreen);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//set up panels
		cardsPanel.setSize(1000,400);
		cardsPanel.setLocation(0,50);
		cardsPanel.setBackground(feltGreen);
		
		infoPanel.setSize(1000,100);
		infoPanel.setLocation(0,450);
		infoPanel.setBackground(feltGreen);
		
		btnPanel.setSize(1000,50);
		btnPanel.setLocation(0,0);
		btnPanel.setBackground(feltGreen);
		reset.addActionListener(this);
		showAll.addActionListener(this);
		btnPanel.add(reset);
		btnPanel.add(showAll);
		
		getContentPane().setLayout(null);
		getContentPane().add(btnPanel);
		setVisible(true);
		newGame();
	}
	
	public void actionPerformed (ActionEvent e)
	{
		if (e.getSource() == reset)
			newGame();
		else if (e.getSource() == showAll)
			revealCards();
	}
	
	public void revealCards()
	{
		showAll.setEnabled(false);
		updateInfoPanel(2);
		for (int i = 0; i < buttons.size(); i++)
		{
			buttons.elementAt(i).showCard();
			buttons.elementAt(i).disableButton(true);
		}
	}
	
	public void cardChosen(ActionEvent e)
	{
		for (int i = 0; i < buttons.size(); i++)
		{
			if (e.getSource() == buttons.elementAt(i))
			{
				buttons.elementAt(i).showCard();
				buttons.elementAt(i).disableButton(true);
				if (pickedCard1 == false) //first card picking
				{
					index1 = i;
					pickedCard1 = true;
				}
				else
				{
					index2 = i;
					pickedCard2 = true;
				}
				if (pickedCard1 && pickedCard2)
					evaluateCards();
				break;
			}
		}
	}
	
	private class TimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent Evt)
		{
			boolean match = false;
			numTries++;
			Card c1 = buttons.elementAt(index1).getCard();
			Card c2 = buttons.elementAt(index2).getCard();
			
			if ((c1.getSuit() <= 2 && c2.getSuit() <= 2)||(c1.getSuit() > 2 && c2.getSuit() > 2))
				if (c1.getVal() == c2.getVal())
					match = true;
			
			if (match)
			{
				buttons.elementAt(index1).setVisible(false);
				buttons.elementAt(index2).setVisible(false);
				pairsFound++; pairsHidden--;
			}
			else
			{
				buttons.elementAt(index1).hideCard();
				buttons.elementAt(index2).hideCard();
			}
				
			for (int i = 0; i < buttons.size(); i++)
				buttons.elementAt(i).setEnabled(true);
			
			updateInfoPanel(1);
			timer.stop();
		}
	}
	
	public void newGame()
	{
		showAll.setEnabled(true);
		pickedCard1 = false; pickedCard2 = false;
		index1 = -1; index2 = -1;
		pairsFound = 0; pairsHidden = 26; numTries = 0;
		deck = new Deck();
		deck.shuffle();
		
		getContentPane().remove(cardsPanel);
		cardsPanel.removeAll();
		buttons = new Vector<Button>();
		try{
			buttons.addElement(new Button(new Card(1,1)));
			buttons.removeAllElements();
		} 
		catch (IOException e2) {}
		
		for (int i = 0; i < deck.getDeck().size(); i++)
		{
			try {
				Button b = new Button(deck.getDeck().elementAt(i));
				b.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						cardChosen(e);
					}});
				cardsPanel.add(b);
				buttons.addElement(b);
			} 
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		getContentPane().add(cardsPanel);
		updateInfoPanel(1);
	}
	
	public void updateInfoPanel(int gameStatus)//1=pairFound, 2=gave up, 3= won
	{
		getContentPane().remove(infoPanel);
		infoPanel.removeAll();
		if (gameStatus == 1)
		{
			JLabel found = new JLabel("<html><font color = 'white'>Pairs Found: " + pairsFound + "\t");
			JLabel hidden = new JLabel("<html><font color = 'yellow'>Pairs Remaining: " + pairsHidden + "\t");
			JLabel tries = new JLabel("<html><font color = 'orange'> Tries: " + numTries);
			found.setFont(font); hidden.setFont(font); tries.setFont(font);
			infoPanel.add(found); infoPanel.add(hidden); infoPanel.add(tries);
		}
		else if (gameStatus == 2)
		{
			JLabel loser = new JLabel("<html><font color = 'white'>YOU SUCK, YOU GIVER UP-ER!");
			loser.setFont(font);
			infoPanel.add(loser);
		}
		else
			win();
		
		getContentPane().add(infoPanel);
		getContentPane().repaint();
		getContentPane().validate();
	}
	public void win()
	{
		showAll.setEnabled(false);
		getContentPane().remove(infoPanel);
		
		infoPanel.removeAll();
		JLabel winner = new JLabel("<html><font color = 'white'>YOU DID IT, YOU SON OF A GUN!");
		winner.setFont(font);
		infoPanel.add(winner);
		
		getContentPane().add(infoPanel);
		getContentPane().repaint();
		getContentPane().validate();
		
		for (int i = 0; i < 52; i++)
		{
			buttons.elementAt(i).setVisible(true);
			buttons.elementAt(i).disableButton(true);
		}
	}
	public void evaluateCards()
	{
		for (int i = 0; i < buttons.size(); i++)
			if (i != index1 && i != index2 )
				buttons.elementAt(i).disableButton(false);
		
		pickedCard1 = false; pickedCard2 = false;
		timer.start();
	}
}

@SuppressWarnings("serial")
class Button extends JButton
{
	private Card card;
	Button(Card c) throws IOException
	{
		card = c;
		hideCard();
		setBorder(BorderFactory.createEmptyBorder());
		setContentAreaFilled(false);
		
		Image img = null;
		String path = card.getImage(2);
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setDisabledIcon(new ImageIcon(img));
	}
	public void disableButton(boolean visible)
	{
		String path = "";
		if (visible)
			path = card.getImage(1);
		else
			path = card.getImage(2);
		
		Image img = null;
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setDisabledIcon(new ImageIcon(img));
		setEnabled(false);
	}
	public void showCard()
	{
		Image img = null;
		String path = card.getImage(1);
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setIcon(new ImageIcon(img));
	}
	public void hideCard()
	{
		Image img = null;
		String path = card.getImage(2);
		try {
			img = ImageIO.read(ResourceLoader.load(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setIcon(new ImageIcon(img));
	}
	public Card getCard(){
		return card;
	}
}