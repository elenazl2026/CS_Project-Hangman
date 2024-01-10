import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static final int max_attempts = 7;
    private static final int hints_limits = 3;

    private List<String> wordList;
    private Random random;
    private Scanner scanner;
    private String selectedWord;
    private Set<Character> guessedLetters;
    private Set<Character> correctGuesses;
    private int attempts;
    private int hintsRemaining;

    public Main() {
        wordList = loadWordListFromFile("src/HangmanWordsList.txt");
        random = new Random();
        scanner = new Scanner(System.in);
        guessedLetters = new HashSet<>();
        correctGuesses = new HashSet<>();
        hintsRemaining = hints_limits;
    }

    public void playGame() {
        System.out.println("Welcome to Hangman!");

        selectWord();
        initializeDisplayWord();

        while (attempts > 0) {
            System.out.println("Word: " + getDisplayWord());
            System.out.println("Attempts remaining: " + attempts);
            System.out.println("Letters already guessed: " + guessedLetters);
            System.out.println("Hints remaining: " + hintsRemaining);
            System.out.print("Enter a letter guess or 'hint': ");
            String guessInput = scanner.nextLine();

            if (guessInput.equalsIgnoreCase("hint")) {
                if (hintsRemaining > 0) {
                    provideHint();
                    continue;
                } else {
                    System.out.println("You have used all your hints.");
                    continue;
                }
            }

            char guess = extractGuess(guessInput);

            if (isInvalidGuess(guess)) {
                System.out.println("Invalid guess. Please enter a single letter or 'hint'.");
                continue;
            }

            if (isAlreadyGuessed(guess)) {
                System.out.println("You already guessed that letter. Try again.");
                continue;
            }

            evaluateGuess(guess);

            if (isWordGuessed()) {
                System.out.println("Congratulations! You won the ultimate game of Hangman!");
                System.out.println("The word is: " + selectedWord);
                break;
            }
        }

        if (attempts == 0) {
            System.out.println("Game over! You lost.");
            System.out.println("The word was: " + selectedWord);
        }
    }

    private void selectWord() {
        int randomIndex = random.nextInt(wordList.size());
        selectedWord = wordList.get(randomIndex).toLowerCase(); // Convert selected word to lowercase**
        attempts = max_attempts;
    }

    private void initializeDisplayWord() {
        StringBuilder displayWord = new StringBuilder();
        for (int i = 0; i < selectedWord.length(); i++) {
            displayWord.append("_");
        }
        System.out.println("A word has been selected. Start guessing!");
        System.out.println("Word: " + displayWord);
    }

    private String getDisplayWord() {
        StringBuilder displayWord = new StringBuilder();
        for (int i = 0; i < selectedWord.length(); i++) {
            char letter = selectedWord.charAt(i);
            if (correctGuesses.contains(letter)) {
                displayWord.append(letter).append(" ");
            } else {
                displayWord.append("_ ");
            }
        }
        return displayWord.toString().trim();
    }

    private void provideHint() {
        int index = -1;
        for (int i = 0; i < selectedWord.length(); i++) {
            char letter = selectedWord.charAt(i);
            if (!correctGuesses.contains(letter)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            char hintLetter = selectedWord.charAt(index);
            correctGuesses.add(hintLetter);
            hintsRemaining--;
            System.out.println("Hint: The word contains the letter '" + hintLetter + "'.");
        }
    }

    private char extractGuess(String guessInput) {
        return guessInput.toLowerCase().charAt(0);
    }

    private boolean isInvalidGuess(char guess) {
        return !Character.isLetter(guess) || guessedLetters.contains(guess);
    }

    private boolean isAlreadyGuessed(char guess) {
        if (guessedLetters.contains(guess)) {
            return true;
        }
        guessedLetters.add(guess);
        return false;
    }

    private void evaluateGuess(char guess) {
        if (String.valueOf(guess).equalsIgnoreCase("hint")) {
            provideHint();
            return;
        }

        boolean correct = false;
        for (int i = 0; i < selectedWord.length(); i++) {
            if (selectedWord.charAt(i) == guess) {
                correctGuesses.add(guess);
                correct = true;
            }
        }

        if (correct) {
            System.out.println("Correct guess!");
            displayUpdatedWord();
            return;
        }

        System.out.println("Incorrect guess!");
        attempts--;
    }

    private void displayUpdatedWord() {
        String updatedWord = getDisplayWord();
        System.out.println("Updated Word: " + updatedWord);
    }

    private boolean isWordGuessed() {
        for (int i = 0; i < selectedWord.length(); i++) {
            if (!correctGuesses.contains(selectedWord.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private List<String> loadWordListFromFile(String filePath) {
        List<String> wordList = new ArrayList<>();
        try {
            Path path = Path.of(filePath);
            wordList = Files.readAllLines(path);
        } catch (IOException e) {
            System.err.println("Error reading word list file: " + e.getMessage());
        }
        return wordList;
    }

    public static void main(String[] args) {
        Main hangman = new Main();
        hangman.playGame();
    }}