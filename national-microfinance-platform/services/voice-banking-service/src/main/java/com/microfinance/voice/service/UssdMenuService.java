package com.microfinance.voice.service;

import com.microfinance.voice.domain.model.UssdSession;
import com.microfinance.voice.domain.repository.UssdSessionRepository;
import com.microfinance.voice.dto.UssdRequest;
import com.microfinance.voice.dto.UssdResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UssdMenuService {

    private final UssdSessionRepository sessionRepository;

    public UssdResponse handleUssdRequest(UssdRequest request) {
        String text = request.getText() != null ? request.getText() : "";
        Optional<UssdSession> sessionOpt = sessionRepository.findBySessionId(request.getSessionId());

        UssdSession session;
        if (text.isEmpty() || sessionOpt.isEmpty()) {
            // New Session or empty input means start menu
            session = sessionOpt.orElseGet(() -> UssdSession.builder()
                    .id(UUID.randomUUID().toString())
                    .sessionId(request.getSessionId())
                    .phoneNumber(request.getPhoneNumber())
                    .currentMenu("MAIN")
                    .status("ACTIVE")
                    .build());
            session.setCurrentMenu("MAIN");
            sessionRepository.save(session);
            
            return new UssdResponse("Welcome to NDMP Voice Banking\n1. Check Balance\n2. Transfer Funds\n3. Pay Bills\n4. Exit", false);
        }

        session = sessionOpt.get();
        String[] inputArray = text.split("\\*");
        String lastInput = inputArray[inputArray.length - 1];

        // Basic State Machine
        UssdResponse response;
        switch (session.getCurrentMenu()) {
            case "MAIN":
                if ("1".equals(lastInput)) {
                    response = new UssdResponse("Your balance is 15,000 ETB.\n0. Back", false);
                    session.setCurrentMenu("BALANCE");
                } else if ("2".equals(lastInput)) {
                    response = new UssdResponse("Enter recipient phone number:", false);
                    session.setCurrentMenu("TRANSFER_PHONE");
                } else if ("3".equals(lastInput)) {
                    response = new UssdResponse("1. Water\n2. Electricity\n0. Back", false);
                    session.setCurrentMenu("BILLS");
                } else if ("4".equals(lastInput)) {
                    response = new UssdResponse("Thank you for using NDMP Voice Banking.", true);
                    session.setStatus("COMPLETED");
                } else {
                    response = new UssdResponse("Invalid choice. Try again.\n1. Check Balance\n2. Transfer Funds\n3. Pay Bills\n4. Exit", false);
                }
                break;
            case "BALANCE":
                if ("0".equals(lastInput)) {
                    response = new UssdResponse("Welcome to NDMP Voice Banking\n1. Check Balance\n2. Transfer Funds\n3. Pay Bills\n4. Exit", false);
                    session.setCurrentMenu("MAIN");
                } else {
                    response = new UssdResponse("Invalid choice. 0 to go back.", false);
                }
                break;
            case "TRANSFER_PHONE":
                response = new UssdResponse("Enter amount to transfer:", false);
                session.setCurrentMenu("TRANSFER_AMOUNT");
                break;
            case "TRANSFER_AMOUNT":
                response = new UssdResponse("Transfer successful.\n0. Main Menu", false);
                session.setCurrentMenu("BALANCE"); // Reuse balance logic for back button
                break;
            case "BILLS":
                if ("0".equals(lastInput)) {
                    response = new UssdResponse("Welcome to NDMP Voice Banking\n1. Check Balance\n2. Transfer Funds\n3. Pay Bills\n4. Exit", false);
                    session.setCurrentMenu("MAIN");
                } else {
                    response = new UssdResponse("Enter account number:", false);
                    session.setCurrentMenu("BILL_ACCOUNT");
                }
                break;
            case "BILL_ACCOUNT":
                response = new UssdResponse("Bill payment successful.\n0. Main Menu", false);
                session.setCurrentMenu("BALANCE");
                break;
            default:
                response = new UssdResponse("Unknown error. Goodbye.", true);
                session.setStatus("ERROR");
                break;
        }

        sessionRepository.save(session);
        return response;
    }
}
