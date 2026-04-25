package org.lytvynovych.bettingaiverification.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lytvynovych.bettingaiverification.entity.Bet;
import org.lytvynovych.bettingaiverification.entity.User;
import org.lytvynovych.bettingaiverification.repository.BetRepository;
import org.lytvynovych.bettingaiverification.repository.MatchRepository;
import org.lytvynovych.bettingaiverification.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvImportService {

    // Все зависимости строго вверху класса
    private final UserRepository userRepository;
    private final BetRepository betRepository;
    private final MatchRepository matchRepository;
    private final EntityManager entityManager;

    @Transactional
    public int importUsersFromCsv(MultipartFile file) {
        int batchSize = 100;
        int totalSaved = 0;
        List<User> batch = new ArrayList<>(batchSize);

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line.length < 2) continue;

                User user = new User();
                user.setUsername(line[0].trim());

                try {
                    user.setBalance(Double.parseDouble(line[1].trim()));
                } catch (NumberFormatException e) {
                    user.setBalance(0.0);
                }

                user.setBlocked(false);


                batch.add(user);

                if (batch.size() == batchSize) {
                    saveBatchAndClearCacheUsers(batch);
                    totalSaved += batchSize;
                }
            }

            if (!batch.isEmpty()) {
                saveBatchAndClearCacheUsers(batch);
                totalSaved += batch.size();
            }

            log.info("Successfully imported {} users from CSV.", totalSaved);
            return totalSaved;

        } catch (Exception e) {
            log.error("Failed to import CSV file", e);
            throw new RuntimeException("CSV Import failed: " + e.getMessage());
        }
    }

    private void saveBatchAndClearCacheUsers(List<User> batch) {
        userRepository.saveAll(batch);
        entityManager.flush();
        entityManager.clear();
        batch.clear();
    }

    @Transactional
    public int importBetsFromCsv(MultipartFile file) {
        int batchSize = 100;
        int totalSaved = 0;
        List<Bet> batch = new ArrayList<>(batchSize);

        // Форматтер для красивого чтения даты из CSV
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                // Теперь ожидаем минимум 5 колонок: username, match_external_id, created_at, amount, outcome
                if (line.length < 5) continue;

                String username = line[0].trim();

                Long matchExternalId;
                LocalDateTime createdAt;
                Double amount;

                try {
                    matchExternalId = Long.parseLong(line[1].trim());
                    // Парсим 3-ю колонку (индекс 2) как дату
                    createdAt = LocalDateTime.parse(line[2].trim(), dateFormatter);
                    // Парсим 4-ю колонку (индекс 3) как сумму
                    amount = Double.parseDouble(line[3].trim());
                } catch (Exception e) {
                    log.warn("Skipping bet for {}: Invalid number or date format. Error: {}", username, e.getMessage());
                    continue;
                }

                // Исход теперь в 5-й колонке (индекс 4)
                String outcome = line[4].trim();

                // Ищем через Optional
                var userOpt = userRepository.findByUsername(username);
                var matchOpt = matchRepository.findByExternalId(matchExternalId);

                if (userOpt.isEmpty()) {
                    log.warn("Skipping bet: User '{}' not found in database!", username);
                    continue;
                }
                if (matchOpt.isEmpty()) {
                    log.warn("Skipping bet: Match with external ID '{}' not found in database!", matchExternalId);
                    continue;
                }

                Bet bet = new Bet();
                bet.setUser(userOpt.get());
                bet.setMatch(matchOpt.get());
                bet.setAmount(amount);
                bet.setOutcome(outcome);
                bet.setCreatedAt(createdAt); // 🔥 Устанавливаем дату

                batch.add(bet);

                if (batch.size() == batchSize) {
                    betRepository.saveAll(batch);
                    entityManager.flush();
                    entityManager.clear();
                    batch.clear();
                    totalSaved += batchSize;
                }
            }

            if (!batch.isEmpty()) {
                betRepository.saveAll(batch);
                totalSaved += batch.size();
            }

            log.info("Successfully imported {} bets from CSV.", totalSaved);
            return totalSaved;

        } catch (Exception e) {
            log.error("Failed to import Bets CSV", e);
            throw new RuntimeException("Bets CSV Import failed: " + e.getMessage());
        }
    }
}