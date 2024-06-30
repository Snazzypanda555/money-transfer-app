package com.techelevator.tenmo;

import com.techelevator.tenmo.enums.TransferStatusEnum;
import com.techelevator.tenmo.enums.TransferTypeEnum;
import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        //Step 3 - Gabriel
        try {
            String url = API_BASE_URL + "/account/balance/" + currentUser.getUser().getId();
            double balance = restTemplate.getForObject(url, double.class);
            consoleService.printBalance(balance);
        }
        catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : "+e.getStatusText());
        }
    }

    private void viewTransferHistory() {
//Step 5/6 - Gabriel
        TransferDto[] transferDtos = null;
        boolean keepLoop = true;
        try {
            String url = API_BASE_URL + "transfer/transfer history/" + currentUser.getUser().getId();
            transferDtos  = restTemplate.getForObject(url, TransferDto[].class);
            List<TransferDto> transferDtoList = Arrays.asList(transferDtos);
            consoleService.printTransferHistory(transferDtoList);
            while(keepLoop){
                int id = consoleService.promptForInt("Enter the transfer ID you want to view: \n(Press 0 to exit)\n");
                if (id==0){
                    keepLoop=false;
                    break;
                }
                else{
                    consoleService.printTransferById(transferDtoList, id);
                }
            }
        }
        catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : "+e.getStatusText());
        }
    }



    //approve or reject pending transfers
    private void viewPendingRequests() {
        //step 8/9
        List<TransferDto> transferDtoList = getPendingRequests();
        int transferId = 0;
        if (transferDtoList == null || transferDtoList.isEmpty()) {
            return;
        }

        while (true) {
            transferId = consoleService.promptForInt("Please Enter Id of pending transfer that you want to accept or reject: \n (type 0 to cancel)\n ");
            if (transferId == 0) {
                break;
            }
            if (!isTransferIdAvailable(transferDtoList, transferId)) {
                System.out.println("Invalid transfer ID. Please try again.");
                break;
            }


            String userSelection = consoleService.promptForString("Would you like to (A)pprove or (R)eject the transfer? type (A/R): \n type (C) to cancel\n ").trim().toUpperCase();
            if (userSelection.equals("C")) {
                break;
            } else if (userSelection.equals("A")) {
                approveTransfer(transferId);
            } else if (userSelection.equals("R")) {
                rejectTransfer(transferId);
            } else {
                System.out.println("Invalid action. Returning to previous command...");
            }
        }


    }

    private void approveTransfer(Integer transferId) {
        try {
            String url = API_BASE_URL + "/transfer/approve/" + transferId;
            restTemplate.put(url, null, Void.class);
            System.out.println("Transfer approved successfully!");
        } catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
        } catch (RestClientResponseException e) {
            System.out.println(e.getRawStatusCode() + " : " + e.getStatusText());
        }
    }

    // Reject a transfer
    private void rejectTransfer(Integer transferId) {
        try {
            String url = API_BASE_URL + "/transfer/reject/" + transferId;
            restTemplate.postForObject(url, null, Void.class);
            System.out.println("Transfer rejected successfully!");
        } catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
        } catch (RestClientResponseException e) {
            System.out.println(e.getRawStatusCode() + " : " + e.getStatusText());
        }
    }



    //show the pending requests
    private List<TransferDto> getPendingRequests() {
        List<TransferDto> transferDtoList = new ArrayList<>();
        try {
            String url = API_BASE_URL + "/transfer/Pending Transfers/" + currentUser.getUser().getId();
            TransferDto[] transferDto  = restTemplate.getForObject(url, TransferDto[].class);
            transferDtoList = Arrays.asList(transferDto);
            if (transferDtoList == null || transferDtoList.isEmpty()) {
                System.out.println("No Transfer History.");
            }
            else {
                consoleService.printTransferHistory(transferDtoList);
            }

        }
        catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
        }
        catch (RestClientResponseException e) {
            System.out.println(e.getRawStatusCode());
        }
        return transferDtoList;
    }

    private boolean isTransferIdAvailable(List<TransferDto> transferDto, int transferId) {

        for (TransferDto dto : transferDto) {
            if (transferId == dto.getTransferId()) {
                return true;
            }
        }
        System.out.println("transfer is not correct!");
        return false;
    }


    private void sendBucks() {
        List<User> userList = listUserList();
        if(userList == null || userList.isEmpty()){
            return;
        }
        Integer userId = consoleService.promptForInt("Enter id of user you want to send money to (0 to cancel): ");
        if (userId.equals(0)){
            return;
        } else if (!isUserIdAvailable(userId,userList)){
            return;
        }

        Double amount = consoleService.promptForDouble("Enter amount: ");
        if(!isAmountAvailable(amount)) {
            return;
        }
        sendBucksHelper(userId, amount);
    }


    private void requestBucks() {

        List<User> userList = listUserList();
        if (userList == null || userList.size() == 0) {
            return;
        }

        Integer userId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel):");
        if (userId.equals(0)) {
            return;
        }
        if (!isUserIdAvailable(userId, userList)) {
            return;
        }


        Double amount = consoleService.promptForDouble("Enter amount:");
        if (!isAmountAvailable(amount)) {
            return;
        }

        requestBucks(userId, amount);

    }

    private List<User> listUserList() {
        List<User> userList = new ArrayList<>();
        try {
            String url = API_BASE_URL + "/user/listAll/";
            User[] users = restTemplate.getForObject(url, User[].class);
            userList = Arrays.asList(users);
            consoleService.printUserList(userList);
        } catch (ResourceAccessException e) {
            System.out.println(e.getMessage());
        } catch (RestClientResponseException e) {
            System.out.println(e.getRawStatusCode());
        }
        return userList;
    }


    private void requestBucks(Integer userId, Double amount) {

        String url = API_BASE_URL + "/transfer/request";

        //fill entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        BucksDto bucksDto = new BucksDto();
        bucksDto.setFromUserId(currentUser.getUser().getId());
        bucksDto.setToUserId(userId);
        bucksDto.setAmount(amount);
        HttpEntity<BucksDto> entity = new HttpEntity<>(bucksDto, headers);

        Integer TransferId = restTemplate.postForObject(url, entity, Integer.class);
        if (TransferId.equals(-1)) {
            System.out.println("something wrong with request transfer");
        }
        else {
            System.out.println("request successfully!");
        }
    }

    private void sendBucksHelper (Integer userId, Double amount){
        String url = API_BASE_URL + "/transfer/send";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        BucksDto bucksDto = new BucksDto();
        bucksDto.setFromUserId(currentUser.getUser().getId());
        bucksDto.setToUserId(userId);
        bucksDto.setAmount(amount);

        HttpEntity<BucksDto> entity = new HttpEntity<>(bucksDto, headers);
        restTemplate.postForObject(url, entity, Integer.class);
        System.out.println("Amount sent successfully!");
    }

    private boolean isAmountAvailable(Double amount) {
        if (amount > 0) {
            return true;
        } else {
            System.out.println("can't request a zero or negative amount.");
            return false;
        }
    }

    private boolean isUserIdAvailable(Integer userId, List<User> userList) {

        if (userId.equals(currentUser.getUser().getId())) {
            System.out.println("Not be allowed to request money from yourself. ");
            return false;
        }
        //check whether the user exists
        for (User user : userList) {
            if (userId.equals(user.getId())) {
                return true;
            }
        }
        System.out.println("userId is not correct!");
        return false;
    }



}
