package casino;

public class BetValidator {
    public static void validateBet(int betAmount) throws InsufficientBalanceException {
        if (CasinoApp.getPlayerBalance() < betAmount) {
            throw new InsufficientBalanceException("Insufficient balance to place the bet.");
        }
    }
}
