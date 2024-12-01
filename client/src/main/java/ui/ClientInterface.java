package ui;

import java.util.Scanner;

public class ClientInterface {
    private static final Scanner scanner = new Scanner(System.in);

    public static String promptUsername() {
        System.out.print("Enter your username: ");
        return scanner.nextLine();
    }

    public static String promptInput() {
        System.out.print("Enter a string to send to the server (type 'exit' to quit): ");
        return scanner.nextLine();
    }

    public static void closeScanner() {
        scanner.close();
    }
}
