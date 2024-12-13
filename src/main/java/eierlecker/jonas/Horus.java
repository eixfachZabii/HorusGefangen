package eierlecker.jonas;

import de.kyle.gefangenendilemma.api.Prisoner;
import de.kyle.gefangenendilemma.api.event.PostMessEvent;
import de.kyle.gefangenendilemma.api.result.PrisonerMessResult;

public class Horus implements Prisoner {

    private int totalRounds = -1; // Anzahl der Runden
    private int currentRound = 0; // Aktuelle Runde
    private int gameCounter = 0; // Anzahl der Spiele
    private boolean isTitForTat = false; // Erkenntnis über die Strategie des Gegners
    private boolean isNiceOpponent = false; // Erkenntnis, ob Gegner nett ist
    private int betrayStreak = 0; // Anzahl der aufeinanderfolgenden Betrügereien, (Eierleckerei)
    private PrisonerMessResult currentDecision = PrisonerMessResult.COOPERATE; // Eigene current Entscheidung

    @Override
    public String getName() {
        return "HorusSmart";
    }

    @Override
    public PrisonerMessResult messAround(String opponent) {
        currentRound++; // Runde aktualisieren

        // Letzte Runde: Eier klauen. (Gegner kann mir nix)
        if (currentRound == totalRounds) {
            return PrisonerMessResult.BETRAY;
        }

        // Gegner als nett erkannt: Ausnutzerei
        if (isNiceOpponent) {
            return PrisonerMessResult.BETRAY;
        }

        // TitForTat Gegner erkannt: Ausnutzerei
        if (isTitForTat) {
            currentDecision = (currentRound % 2 == 0) ? PrisonerMessResult.BETRAY : PrisonerMessResult.COOPERATE;
            return currentDecision;
        }

        // Standard: TitForTat-ähnliches Verhalten
        return currentDecision;
    }

    @Override
    public void onPostMessEvent(PostMessEvent postMessEvent) {

        // Gegnerentscheidung speichern
        PrisonerMessResult opponentDecision = postMessEvent.result();

        // Prüfen, ob Gegner nett ist
        if (!isNiceOpponent && currentRound > 2) {
            if (currentDecision == PrisonerMessResult.BETRAY) {
                if (opponentDecision == PrisonerMessResult.COOPERATE) {
                    betrayStreak++; // Betrügei Anzahl updaten
                    if (betrayStreak >= 3) {
                        isNiceOpponent = true; // Gegner ist nette Mann
                    }
                } else {
                    betrayStreak = 0; // Gegner hat geklaut :(
                }
            }
        } else {
            // Gegner ist nett, aber wie lange?
            if (opponentDecision == PrisonerMessResult.BETRAY) {
                isNiceOpponent = false; // Gegner ist nicht mehr nett :(
                isTitForTat = true; // Zu TitForTat wechseln weil pussy
            }
        }

        // Prüfen, ob Gegner TitForTat spielt (Kopiert meinen drip)
        if (!isTitForTat && currentRound > 2) {
            if (opponentDecision == currentDecision) {
                isTitForTat = true; // Gegner spiegelt unsere Entscheidungen
            }
        }

        // Punkte-Tracking (optional, für Analyse)
        int earnedPoints = postMessEvent.points();
        System.out.println("Runde " + currentRound + ": Punkte erhalten: " + earnedPoints);

        // Letzte Entscheidung für nächste Runde speichern
        currentDecision = opponentDecision == PrisonerMessResult.COOPERATE
                ? PrisonerMessResult.COOPERATE
                : PrisonerMessResult.BETRAY;

        // Nach dem ersten Spiel Rundenanzahl feststellen
        if (totalRounds == -1 && gameCounter == 0) {
            totalRounds = currentRound; // Gesamtanzahl der Runden speichern
            currentRound = 0; // Zurücksetzen für nächstes Spiel
            gameCounter++; // Erstes Spiel abgeschlossen
        }
    }
}
