package eierlecker.jonas;

import de.kyle.gefangenendilemma.api.Prisoner;
import de.kyle.gefangenendilemma.api.event.PostMessEvent;
import de.kyle.gefangenendilemma.api.result.PrisonerMessResult;

import java.util.ArrayList;
import java.util.List;

public class Horus implements Prisoner {

    private int totalRounds = -1; // Total number of rounds, aka how many times we gotta mess around.
    private int currentRound = 0; // Counting how deep we are in this chaos.
    private int gameCounter = 0; // How many games have we survived so far?
    private int thresholdRounds = 0; // The magic 20% checkpoint of all rounds.
    private boolean isTitForTat = false; // Is the opponent doing "Copycat" shenanigans?
    private boolean isNiceOpponent = false; // Is the opponent too sweet and naive for their own good?
    private boolean isTitForTwoTat = false; // Opponent pulling "Double Copycat" tricks?
    private int betrayStreak = 0; // Count how many betrayals we can spam in a row.
    private List<PrisonerMessResult> opponentHistory = new ArrayList<>(); // Opponent's chaotic move archive.
    private PrisonerMessResult currentDecision = PrisonerMessResult.COOPERATE; // Our current plot twist.

    @Override
    public String getName() {
        return "HorusSmartPlus"; // The big-brain version of Horus, Eierlecker certified.
    }

    @Override
    public PrisonerMessResult messAround(String opponent) {
        currentRound++; // Tick-tock, next round, let's go!

        // YOLO on the last round: Always betray because why not? Smells like Eierlecker to me.
        if (currentRound == totalRounds) {
            return PrisonerMessResult.BETRAY;
        }

        // Found a "Nice" opponent? Time to exploit the fluff out of them!
        if (isNiceOpponent) {
            return PrisonerMessResult.BETRAY;
        }

        // Fighting Double Copycat? Let’s mess with their rhythm every 3 rounds like a DJ.
        if (gameCounter >= 1 && currentRound > thresholdRounds && isTitForTwoTat) {
            currentDecision = (currentRound % 3 == 0) ? PrisonerMessResult.BETRAY : PrisonerMessResult.COOPERATE;
            return currentDecision;
        }

        // Fighting Single Copycat? Let’s alternate like a moody teenager.
        if (gameCounter >= 1 && currentRound > thresholdRounds && isTitForTat) {
            currentDecision = (currentRound % 2 == 0) ? PrisonerMessResult.BETRAY : PrisonerMessResult.COOPERATE;
            return currentDecision;
        }

        // Default: Be a wannabe Copycat and hope for the best.
        return currentDecision;
    }

    @Override
    public void onPostMessEvent(PostMessEvent postMessEvent) {
        // Store what the opponent did because receipts are important.
        PrisonerMessResult opponentDecision = postMessEvent.result();
        opponentHistory.add(opponentDecision);

        // Start overthinking life only after 20% of the game has passed and if we’ve played before.
        if (gameCounter >= 1 && currentRound > thresholdRounds) {
            // Sniff out "Nice" opponents: The overly polite ones we can trick.
            if (!isNiceOpponent && currentRound > 2) {
                if (currentDecision == PrisonerMessResult.BETRAY && opponentDecision == PrisonerMessResult.COOPERATE) {
                    betrayStreak++;
                    if (betrayStreak >= 3) {
                        isNiceOpponent = true; // Found a sucker! Exploit time!
                    }
                } else {
                    betrayStreak = 0; // Whoops, not that nice after all.
                }
            }

            // Spot "Double Copycat": Betray-betray-cooperate pattern detected!
            if (!isTitForTwoTat && opponentHistory.size() >= 3) {
                int size = opponentHistory.size();
                if (opponentHistory.get(size - 1) == PrisonerMessResult.BETRAY &&
                        opponentHistory.get(size - 2) == PrisonerMessResult.BETRAY &&
                        opponentHistory.get(size - 3) == PrisonerMessResult.COOPERATE) {
                    isTitForTwoTat = true; // Busted! It’s Double Copycat!
                }
            }

            // Sniff out "Single Copycat": If they mirror our moves too much, we know what’s up.
            if (!isTitForTat && currentRound > 2) {
                if (opponentDecision == currentDecision) {
                    isTitForTat = true; // Ah, you’re that guy.
                }
            }
        }

        // Update our drama plotline based on their move.
        currentDecision = opponentDecision == PrisonerMessResult.COOPERATE
                ? PrisonerMessResult.COOPERATE
                : PrisonerMessResult.BETRAY;

        // If it’s the first game, figure out how long this madness will last.
        if (totalRounds == -1 && gameCounter == 0) {
            totalRounds = currentRound;
            thresholdRounds = (int) Math.ceil(totalRounds * 0.2); // Math magic to find 20%.
            currentRound = 0; // Reset, because we’re starting fresh.
            gameCounter++; // Ding ding, game one done!
        }
    }
}
