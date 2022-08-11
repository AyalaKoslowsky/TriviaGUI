package il.ac.tau.cs.sw1.trivia;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import java.util.Random;

public class TriviaGUI {

	private static final int MAX_ERRORS = 3;
	private Shell shell;
	private Label scoreLabel;
	private Composite questionPanel;
	private Label startupMessageLabel;
	private Font boldFont;
	private String lastAnswer;
	private static int score;
	private List<String> QuestionsList;
	private boolean UsedPass;
	private boolean Used5050;
	
	// Currently visible UI elements.
	Label instructionLabel;
	Label questionLabel;
	private List<Button> answerButtons = new LinkedList<>();
	private Button passButton;
	private Button fiftyFiftyButton;
	private String CorrectAnswer;
	private int wrongAnswers;
	private int times;

	public void open() {
		createShell();
		runApplication();
	}

	/**
	 * Creates the widgets of the application main window
	 */
	private void createShell() {
		Display display = Display.getDefault();
		shell = new Shell(display);
		shell.setText("Trivia");

		// window style
		Rectangle monitor_bounds = shell.getMonitor().getBounds();
		shell.setSize(new Point(monitor_bounds.width / 3,
				monitor_bounds.height / 4));
		shell.setLayout(new GridLayout());

		FontData fontData = new FontData();
		fontData.setStyle(SWT.BOLD);
		boldFont = new Font(shell.getDisplay(), fontData);

		// create window panels
		createFileLoadingPanel();
		createScorePanel();
		createQuestionPanel();
	}

	/**
	 * Creates the widgets of the form for trivia file selection
	 */
	private void createFileLoadingPanel() {
		final Composite fileSelection = new Composite(shell, SWT.NULL);
		fileSelection.setLayoutData(GUIUtils.createFillGridData(1));
		fileSelection.setLayout(new GridLayout(4, false));

		final Label label = new Label(fileSelection, SWT.NONE);
		label.setText("Enter trivia file path: ");

		// text field to enter the file path
		final Text filePathField = new Text(fileSelection, SWT.SINGLE
				| SWT.BORDER);
		filePathField.setLayoutData(GUIUtils.createFillGridData(1));

		// "Browse" button
		final Button browseButton = new Button(fileSelection, SWT.PUSH);
		browseButton.setText("Browse");

		browseButton.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent selectionEvent) {
						String path = GUIUtils.getFilePathFromFileDialog(shell);
						filePathField.setText(path);
					}
				}
		);

		// "Play!" button
		final Button playButton = new Button(fileSelection, SWT.PUSH);
		playButton.setText("Play!");

		playButton.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent selectionEvent) {
						score=0;
						scoreLabel.setText(String.valueOf(score));
						lastAnswer="";
						File file =  new File(filePathField.getText());
						QuestionsList = new ArrayList<>();
						try {
							Scanner myReader = new Scanner(file);
							while (myReader.hasNextLine()){
								String Question = myReader.nextLine();
								QuestionsList.add(Question);
							}
							myReader.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						giveRandomQuestion();
					}
				}
		);
	}

	/**
	 * Creates the panel that displays the current score
	 */
	private void createScorePanel() {
		Composite scorePanel = new Composite(shell, SWT.BORDER);
		scorePanel.setLayoutData(GUIUtils.createFillGridData(1));
		scorePanel.setLayout(new GridLayout(2, false));

		final Label label = new Label(scorePanel, SWT.NONE);
		label.setText("Total score: ");

		// The label which displays the score; initially empty
		scoreLabel = new Label(scorePanel, SWT.NONE);
		scoreLabel.setLayoutData(GUIUtils.createFillGridData(1));
		scoreLabel.setText(String.valueOf(score));
	}

	/**
	 * Creates the panel that displays the questions, as soon as the game
	 * starts. See the updateQuestionPanel for creating the question and answer
	 * buttons
	 */
	private void createQuestionPanel() {
		questionPanel = new Composite(shell, SWT.BORDER);
		questionPanel.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true));
		questionPanel.setLayout(new GridLayout(2, true));

		// Initially, only displays a message
		startupMessageLabel = new Label(questionPanel, SWT.NONE);
		startupMessageLabel.setText("No question to display, yet.");
		startupMessageLabel.setLayoutData(GUIUtils.createFillGridData(2));
	}

	/**
	 * Serves to display the question and answer buttons
	 */
	private void updateQuestionPanel(String question, List<String> answers) {
		// Save current list of answers.
		List<String> currentAnswers = answers;
		
		// clear the question panel
		Control[] children = questionPanel.getChildren();
		for (Control control : children) {
			control.dispose();
		}

		// create the instruction label
		instructionLabel = new Label(questionPanel, SWT.CENTER | SWT.WRAP);
		instructionLabel.setText(lastAnswer + "Answer the following question:");
		instructionLabel.setLayoutData(GUIUtils.createFillGridData(2));

		// create the question label
		questionLabel = new Label(questionPanel, SWT.CENTER | SWT.WRAP);
		questionLabel.setText(question);
		questionLabel.setFont(boldFont);
		questionLabel.setLayoutData(GUIUtils.createFillGridData(2));

		// create the answer buttons
		answerButtons.clear();
		for (int i = 0; i < 4; i++) {
			Button answerButton = new Button(questionPanel, SWT.PUSH | SWT.WRAP);
			answerButton.setText(answers.get(i));
			GridData answerLayoutData = GUIUtils.createFillGridData(1);
			answerLayoutData.verticalAlignment = SWT.FILL;
			answerButton.setLayoutData(answerLayoutData);
			answerButton.setText(currentAnswers.get(i));

			answerButtons.add(answerButton);
		}

		for (int i=0; i<4; i++){
			int I=i;
			answerButtons.get(i).addSelectionListener(
					new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent selectionEvent) {
//							if (score<=0){
//								if (Used5050){
//									fiftyFiftyButton.setEnabled(false);
//								}
//								if(UsedPass){
//									passButton.setEnabled(false);
//								}
//							}
							if (currentAnswers.get(I).equals(CorrectAnswer)){
								score += 3;
								lastAnswer = "Correct! ";
							}
							else{
								if (score<=2) {
									if (Used5050){
										fiftyFiftyButton.setEnabled(false);
									}
									if (UsedPass){
										passButton.setEnabled(false);
									}
								}
								score -= 2;
								wrongAnswers++;
								lastAnswer = "Wrong... ";
							}
							times++;
							scoreLabel.setText(String.valueOf(score));
							if (wrongAnswers==3 || QuestionsList.size()==0){
								GUIUtils.showInfoDialog(shell, "GAME OVER", "Your final score is "+score+" after "+times+" Questions.");
							}
							giveRandomQuestion();
						}
					}
			);
		}


		// create the "Pass" button to skip a question
		passButton = new Button(questionPanel, SWT.PUSH);
		passButton.setText("Pass");
		GridData data = new GridData(GridData.END, GridData.CENTER, true,
				false);
		data.horizontalSpan = 1;
		passButton.setLayoutData(data);

		passButton.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent selectionEvent) {
						if (score<=0 && UsedPass){
							passButton.setEnabled(false);
						}
						else if (!UsedPass){
							giveRandomQuestion();
							UsedPass = true;
							if (score<=0){
								passButton.setEnabled(false);
							}
						}
						else{
							if (score==1) {
								passButton.setEnabled(false);
								if(Used5050){
									fiftyFiftyButton.setEnabled(false);
								}
							}
							score--;
							giveRandomQuestion();
						}
						scoreLabel.setText(String.valueOf(score));
						lastAnswer="";
						instructionLabel.setText(lastAnswer + "Answer the following question:");
					}
				}
		);
		
		// create the "50-50" button to show fewer answer options
		fiftyFiftyButton = new Button(questionPanel, SWT.PUSH);
		fiftyFiftyButton.setText("50-50");
		data = new GridData(GridData.BEGINNING, GridData.CENTER, true,
				false);
		data.horizontalSpan = 1;
		fiftyFiftyButton.setLayoutData(data);

		// two operations to make the new widgets display properly
		questionPanel.pack();
		questionPanel.getParent().layout();

		fiftyFiftyButton.addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent selectionEvent) {
						if (score<=0 && Used5050){
							fiftyFiftyButton.setEnabled(false);
						}
						if (!Used5050){
							Used5050 = true;
							usingFiftyFifty();
							scoreLabel.setText(String.valueOf(score));
							if (score<=0){
								fiftyFiftyButton.setEnabled(false);
							}
						}
						else {
							score--;
							usingFiftyFifty();
							scoreLabel.setText(String.valueOf(score));
							if (score<=0) {
								if(UsedPass){
									passButton.setEnabled(false);
								}
								if (Used5050){
									fiftyFiftyButton.setEnabled(false);
								}
							}
						}
					}
				}
		);
	}


	private void giveRandomQuestion() {
		Random rand = new Random();
		int QuestionNum = rand.nextInt(QuestionsList.size());
		List<String> QuestionAndAnswers = Arrays.asList(QuestionsList.get(QuestionNum).split("\t"));
		String currQuestion = QuestionAndAnswers.get(0);
		List<String> answers = QuestionAndAnswers.subList(1, QuestionAndAnswers.size());
		CorrectAnswer = answers.get(0);
		Collections.shuffle(answers);
		updateQuestionPanel(currQuestion, answers);
		QuestionsList.remove(QuestionsList.get(QuestionNum));
	}


	private void usingFiftyFifty(){
		int correctAnswerLoc=0;
		for (int i=0; i<4; i++){
			if(answerButtons.get(i).getText().equals(CorrectAnswer)){
				correctAnswerLoc=i;
			}
		}
		boolean foundFirst=false;
		boolean foundSec=false;
		List<Integer> alreadyUsed = new ArrayList<>();
		Random rand = new Random();
		while (!(foundFirst && foundSec)){
			int Num=rand.nextInt(4);
			if (Num!=correctAnswerLoc && !alreadyUsed.contains(Num) && !foundFirst){
				answerButtons.get(Num).setEnabled(false);
				foundFirst=true;
				alreadyUsed.add(Num);
			}
			else if(Num!=correctAnswerLoc && !alreadyUsed.contains(Num)){
				answerButtons.get(Num).setEnabled(false);
				foundSec=true;
				alreadyUsed.add(Num);
			}
		}
	}


	/**
	 * Opens the main window and executes the event loop of the application
	 */
	private void runApplication() {
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		boldFont.dispose();
	}
}
