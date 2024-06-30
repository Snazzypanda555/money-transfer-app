package com.techelevator.tenmo.services;


import com.techelevator.tenmo.enums.TransferStatusEnum;
import com.techelevator.tenmo.enums.TransferTypeEnum;
import com.techelevator.tenmo.model.TransferDto;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }
    public Double promptForDouble(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Double.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a Double number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printBalance(double balance) {
        System.out.println("----------------  current account balance  ------------------");
        System.out.println(String.format("Your current account balance is: $%.2f", balance));
        System.out.println("------------------------------------------------------------");
    }

    public void printUserList(List<User> userList) {
        if (userList == null || userList.size() == 0) {
            System.out.println("No User!");
            return;
        }
        System.out.println("-------------------   User List   ----------------------");
        for (User user : userList) {
            Integer userId = user.getId();
            String name = user.getUsername();
            System.out.println(String.format("User ID:%6d, Name:%20s", userId, name));
        }
        System.out.println("------------------------------------------------------------");

    }

    public void printTransferHistory(List<TransferDto> transferDtos) {
        if (transferDtos == null || transferDtos.size() == 0) {
            return;
        }
        System.out.println("-------------------   Transfer History   ----------------------");
        for (TransferDto transferDto : transferDtos) {
            System.out.println(String.format("Id:%-6s From:%-15s To:%-15s Type:%-10s Status:%-10s amount:$%.2f",
                    transferDto.getTransferId(),
                    transferDto.getAccountFromName(),
                    transferDto.getAccountToName(),
                    TransferTypeEnum.getDescById(transferDto.getTransferTypeId()),
                    TransferStatusEnum.getDescById(transferDto.getTransferStatusId()),
                    transferDto.getAmount()
            ));
        }
        System.out.println("------------------------------------------------------------");

    }

    public void printTransferById(List<TransferDto> transferDtos, int Id) {
        String message = "";
        if (transferDtos == null || transferDtos.size() == 0) {
            return;
        }
        for (TransferDto transferDto : transferDtos) {
            if(transferDto.getTransferId() == Id) {
                message = String.format("Id:%-6s From:%-15s To:%-15s Type:%-10s Status:%-10s amount:$%.2f\n",
                        transferDto.getTransferId(),
                        transferDto.getAccountFromName(),
                        transferDto.getAccountToName(),
                        TransferTypeEnum.getDescById(transferDto.getTransferTypeId()),
                        TransferStatusEnum.getDescById(transferDto.getTransferStatusId()),
                        transferDto.getAmount()
                );
            }
            }
        if (message.isEmpty()){
            System.out.print("Id is not correct ");
        } else{
            System.out.print(message);
        }

    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
